package com.ecommerce.mq;

import com.alibaba.fastjson.JSON;
import com.ecommerce.dao.StockLogDOMapper;
import com.ecommerce.dataobject.StockLogDO;
import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;
import com.ecommerce.service.OrderService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import java.util.Map;

@Component
public class MqProducer {

    private DefaultMQProducer defaultMQProducer;
    private TransactionMQProducer transactionMQProducer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.topicname}")
    private String topicName;
    @Resource
    private OrderService orderService;
    @Resource
    private StockLogDOMapper stockLogDOMapper;

    @PostConstruct
    public void init() throws MQClientException {
        //Mq producer
        defaultMQProducer = new DefaultMQProducer("producer_group");
        defaultMQProducer.setNamesrvAddr(nameAddr);
        defaultMQProducer.start();

        transactionMQProducer = new TransactionMQProducer("transaction_producer_group");
        transactionMQProducer.setNamesrvAddr(nameAddr);
        transactionMQProducer.start();

        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {

                Map<String,Integer> argsMap = (Map) arg;

                //Create Order
                try{

                    if(argsMap.get("promoId") != null){
                        orderService.createFlashOrder(argsMap.get("userId"),argsMap.get("productId"),argsMap.get("orderAmount"),argsMap.get("promoId"), String.valueOf(argsMap.get("stockLogId")) );
                    }else{
                        orderService.createOrder(argsMap.get("userId"),argsMap.get("productId"),argsMap.get("orderAmount") , String.valueOf(argsMap.get("stockLogId")) );
                    }

                }catch(Exception e){
                    e.printStackTrace();

                    //Set stocklog status to rollback(2)
                    StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(String.valueOf(argsMap.get("stockLogId")));
                    if(stockLogDO == null){
                        return LocalTransactionState.ROLLBACK_MESSAGE;
                    }
                    stockLogDO.setStatus(2);
                    stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);

                    return LocalTransactionState.ROLLBACK_MESSAGE; // Msg send could fail
                }

                return LocalTransactionState.COMMIT_MESSAGE; // Message send could fail
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {

                String msgJsonString = new String(msg.getBody());
                Map<String,Object> bodyMap = JSON.parseObject(msgJsonString , Map.class);

                Integer productId = (Integer) bodyMap.get("productId");
                Integer orderAmount = (Integer) bodyMap.get("orderAmount");
                String stockLogId = (String) bodyMap.get("stockLogId");

                StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
                if(stockLogDO == null){
                    return LocalTransactionState.UNKNOW;
                }
                if(stockLogDO.getStatus() == 0){
                     return LocalTransactionState.UNKNOW;
                }else if(stockLogDO.getStatus() == 1){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }

                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });
    }

    //Synchronize stock
    //Send message to consumer for stock update
    public boolean asyncReduceStock(Integer productId, Integer orderAmount){

        Map<String,Object> bodyMap = new HashMap<>();
        bodyMap.put("productId",productId);
        bodyMap.put("orderAmount",orderAmount);

        Message message = new Message(topicName,"increase",
                JSON.toJSON(bodyMap).toString().getBytes(StandardCharsets.UTF_8));

        try {
            defaultMQProducer.send(message);
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean transactionalAsyncReduceStock(Integer productId ,Integer orderAmount, Integer userId, Integer promoId, String stockLogId )  {

        Map<String,Object> bodyMap = new HashMap<>();
        bodyMap.put("productId",productId);
        bodyMap.put("orderAmount",orderAmount);
        bodyMap.put("stockLogId",stockLogId);


        Map<String,Object> argsMap = new HashMap<>();
        argsMap.put("productId",productId);
        argsMap.put("orderAmount",orderAmount);
        argsMap.put("userId",userId);
        argsMap.put("promoId",promoId);
        argsMap.put("stockLogId",stockLogId);

        Message message = new Message("TransactionalMQ","TransactionalMQ",
                JSON.toJSON(bodyMap).toString().getBytes(StandardCharsets.UTF_8));

        try {
            TransactionSendResult sendResult = transactionMQProducer.sendMessageInTransaction(message,argsMap);
            if(sendResult.getLocalTransactionState() != LocalTransactionState.COMMIT_MESSAGE ){
                return false;
            }

        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
