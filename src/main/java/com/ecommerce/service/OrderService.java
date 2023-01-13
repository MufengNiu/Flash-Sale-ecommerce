package com.ecommerce.service;

import com.ecommerce.dataobject.OrderDO;
import com.ecommerce.error.SystemException;
import com.ecommerce.service.model.OrderModel;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;

public interface OrderService {

    OrderModel createOrder(Integer userId, Integer productId , Integer orderAmount, String stockLogId)
            throws SystemException;

    List<OrderDO> selectOrdersByUserId(Integer userId);

    OrderModel createFlashOrder(Integer userId, Integer productId, Integer orderAmount, Integer promoId, String stockLogId)
            throws SystemException, MQBrokerException, RemotingException, InterruptedException, MQClientException;
}
