
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for encrypt_password
-- ----------------------------
DROP TABLE IF EXISTS `encrypt_password`;
CREATE TABLE `encrypt_password`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `encrypy_password` varchar(128) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `userinfo`(`user_id` ASC) USING BTREE,
  CONSTRAINT `userinfo` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf16 COLLATE = utf16_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of encrypt_password
-- ----------------------------
INSERT INTO `encrypt_password` VALUES (1, 'fefewfw', 1);
INSERT INTO `encrypt_password` VALUES (9, '[116, 55, 65, 69, 82, 81, 68, 47, 72, 103, 71, 67, 98, 74, 79, 52, 68, 48, 48, 65, 107, 119, 61, 61]', 13);
INSERT INTO `encrypt_password` VALUES (11, '[114, 53, 83, 90, 51, 49, 67, 100, 97, 104, 105, 100, 97, 106, 114, 51, 90, 101, 73, 116, 111, 65, 61, 61]', 15);
INSERT INTO `encrypt_password` VALUES (12, '[57, 108, 119, 107, 78, 107, 118, 119, 101, 117, 82, 87, 118, 111, 107, 69, 121, 43, 47, 120, 67, 81, 61, 61]', 16);
INSERT INTO `encrypt_password` VALUES (13, '[103, 100, 121, 98, 50, 49, 76, 81, 84, 99, 73, 65, 78, 116, 118, 89, 77, 84, 55, 81, 86, 81, 61, 61]', 17);
INSERT INTO `encrypt_password` VALUES (14, '[52, 81, 114, 99, 79, 85, 109, 54, 87, 97, 117, 43, 86, 117, 66, 88, 56, 103, 43, 73, 80, 103, 61, 61]', 18);

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info`  (
  `orderId` varchar(32) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL COMMENT 'order id. String . Primary Key',
  `userId` int NOT NULL DEFAULT 0 COMMENT 'order placed by user',
  `productId` int NOT NULL DEFAULT 0 COMMENT 'product id',
  `orderAmount` int NOT NULL DEFAULT 0 COMMENT 'Amount ordered',
  `productPrice` double NOT NULL DEFAULT 0 COMMENT 'Single product price',
  `orderPrice` double NOT NULL DEFAULT 0 COMMENT 'Total price',
  `isFlashSale` tinyint NOT NULL DEFAULT 0 COMMENT '0: Not flash-sale order, 1: flash-sale order',
  `promoId` int NOT NULL DEFAULT 0 COMMENT 'promoId if is flash sell',
  PRIMARY KEY (`orderId`) USING BTREE,
  INDEX `productId`(`productId` ASC) USING BTREE,
  INDEX `user_id`(`userId` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('2023011100021600', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100021700', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100021800', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100021900', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100022000', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100022100', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100022200', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100022300', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100022400', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100022500', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100022600', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100022700', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100022800', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100022900', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100023000', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100023100', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100023200', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100023300', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100023400', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100023500', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100023600', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100023700', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100023800', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100023900', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100024000', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100024100', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100024200', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100024300', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100024400', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100024500', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100024600', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100024700', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100024800', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100024900', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100025000', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100025100', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100025200', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100025300', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100025400', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100025500', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100025600', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100025700', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100025800', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100025900', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100026000', 13, 6, 1, 2100, 2100, 0, 0);
INSERT INTO `order_info` VALUES ('2023011100026100', 13, 6, 1, 2100, 2100, 0, 0);
INSERT INTO `order_info` VALUES ('2023011100026200', 13, 6, 1, 2100, 2100, 0, 0);
INSERT INTO `order_info` VALUES ('2023011100026300', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100026400', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100026500', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100026600', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100026700', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100026800', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100026900', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100027000', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100027100', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100027200', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100027300', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100027400', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100027500', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100027600', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100027700', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100027800', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100027900', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100028000', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100028100', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011100028200', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200028300', 13, 6, 1, 2100, 2100, 0, 0);
INSERT INTO `order_info` VALUES ('2023011200028400', 13, 6, 1, 2100, 2100, 0, 0);
INSERT INTO `order_info` VALUES ('2023011200028500', 13, 6, 1, 2100, 2100, 0, 0);
INSERT INTO `order_info` VALUES ('2023011200028600', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200028700', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200028800', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200028900', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200029000', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200029100', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200029200', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200029300', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200029400', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200029500', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200029600', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200029700', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200029800', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200029900', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200030000', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200030100', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200030200', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200030300', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200030400', 13, 5, 1, 500, 500, 1, 1);
INSERT INTO `order_info` VALUES ('2023011200030500', 13, 5, 1, 500, 500, 1, 1);

-- ----------------------------
-- Table structure for order_sequence_info
-- ----------------------------
DROP TABLE IF EXISTS `order_sequence_info`;
CREATE TABLE `order_sequence_info`  (
  `name` varchar(32) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL,
  `curr_value` int NOT NULL DEFAULT 0,
  `step` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_sequence_info
-- ----------------------------
INSERT INTO `order_sequence_info` VALUES ('order_info', 306, 1);

-- ----------------------------
-- Table structure for product_info
-- ----------------------------
DROP TABLE IF EXISTS `product_info`;
CREATE TABLE `product_info`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'product id',
  `title` varchar(64) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL COMMENT 'product name',
  `price` double(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'product prce',
  `description` varchar(500) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL COMMENT 'product description',
  `sales` int NOT NULL DEFAULT 0 COMMENT 'amount sold',
  `imgUrl` varchar(1000) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL DEFAULT '' COMMENT 'image url',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf16 COLLATE = utf16_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_info
-- ----------------------------
INSERT INTO `product_info` VALUES (5, 'Iphone1', 12312.00, '32423', 248, 'https://img0.baidu.com/it/u=2433670360,2613129194&fm=253&app=120&size=w931&n=0&f=JPEG&fmt=auto?sec=1671814800&t=d980102a494294bdef641ccdae0e865d');
INSERT INTO `product_info` VALUES (6, 'Yeezy', 2100.00, 'Yeezy Shoes', 37, 'https://cdn.shopify.com/s/files/1/0017/5908/4588/products/358539_01_jpg_1200x.webp?v=1671692724');
INSERT INTO `product_info` VALUES (7, 'shoes', 32.18, 'Nike shoes', 17, 'https://static.nike.com/a/images/t_PDP_864_v1/f_auto,b_rgb:f5f5f5/de9b0c0a-e2be-45b6-9f16-3ebeb7e78e9c/react-escape-run-2-road-running-shoes-S2Lpkt.png');
INSERT INTO `product_info` VALUES (8, 'Ipad', 1200.00, 'Apple ipad', 2, 'https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcRq-DgWXir0rcSruwpVMeebjtnXp8H0pWV6OeqcAmoFQYEn7Hzvu0JmfqRCr1czNYj7gVDBKLvgZ50&usqp=CAc');
INSERT INTO `product_info` VALUES (9, 'Beer', 3.00, 'FURPHY beer', 2, 'https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcTvg7I3PiQKZKZyR8SbDNcwWuDQ34AOQVJzcQ556o42mIxxfzNoc-aHpa1ZsH4aUZv5dLYMmWehc1I&usqp=CAc');

-- ----------------------------
-- Table structure for product_stock
-- ----------------------------
DROP TABLE IF EXISTS `product_stock`;
CREATE TABLE `product_stock`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `stock` int NOT NULL DEFAULT 0 COMMENT 'product stock',
  `product_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `product_id_index`(`product_id` ASC) USING BTREE,
  CONSTRAINT `product_stock` FOREIGN KEY (`product_id`) REFERENCES `product_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf16 COLLATE = utf16_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_stock
-- ----------------------------
INSERT INTO `product_stock` VALUES (5, 80, 5);
INSERT INTO `product_stock` VALUES (6, 183, 6);
INSERT INTO `product_stock` VALUES (7, 183, 7);
INSERT INTO `product_stock` VALUES (8, 98, 8);
INSERT INTO `product_stock` VALUES (9, 198, 9);

-- ----------------------------
-- Table structure for promo
-- ----------------------------
DROP TABLE IF EXISTS `promo`;
CREATE TABLE `promo`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `promoName` varchar(255) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL,
  `startDate` datetime NOT NULL,
  `productId` int NOT NULL DEFAULT 0,
  `promoPrice` double NOT NULL DEFAULT 0,
  `endDate` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf16 COLLATE = utf16_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of promo
-- ----------------------------
INSERT INTO `promo` VALUES (1, 'Iphone sales', '2023-01-03 22:52:00', 5, 500, '2023-12-01 23:23:17');

-- ----------------------------
-- Table structure for stock_log
-- ----------------------------
DROP TABLE IF EXISTS `stock_log`;
CREATE TABLE `stock_log`  (
  `stock_log_id` varchar(64) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL,
  `product_id` int NOT NULL DEFAULT 0,
  `order_amount` int NOT NULL DEFAULT 0,
  `status` int NOT NULL DEFAULT 0 COMMENT '0 for initial state, 1 for order success, 2 for order fail and rollback',
  `isFlashSale` tinyint NOT NULL DEFAULT 0 COMMENT '0: regular order, 1: flash sale order',
  PRIMARY KEY (`stock_log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_log
-- ----------------------------
INSERT INTO `stock_log` VALUES ('076a94eab6d54388aa118dac0e15cb97', 5, 1, 2, 1);
INSERT INTO `stock_log` VALUES ('12d11f3804564560b9a525df98703494', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('25729a99947b4ca9ba7753107649cfe6', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('3b1bd079a6c945ef8108e7122aceaa95', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('420131f3581a4be6a61e87937dee790e', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('46545ee78eee481bb5ed4b879ee345fc', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('4c8a97aeef364ae185b1d32456bd5f18', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('5174768bd6714e20b62e500183679e13', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('5632ddfabc0e41dcbe592b69df6d5d0e', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('67691fb26e374591abc2a922b2136bd6', 6, 1, 1, 0);
INSERT INTO `stock_log` VALUES ('685ec63e1f4447b6a836146bd13a8663', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('7019d71e72ff4f93bfdf0971769ab27c', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('7822e503594046bea0075cd909ad7f6d', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('7ac5337bf0164b1a9e6edef2ff099179', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('7af9ce3d82f841f1abf8d0c6527889e8', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('83cf7f1d358d4c76b93dcb027cfb4459', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('9959dd8127da4505b5767fe06d041417', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('9cca1c882a09464881a6b12790024728', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('a03643f06a0349eb91e8fe717b65c227', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('a47d3205afad4dd486e77cd9467aa34f', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('c1894aee443a4d8ea81866b5d073e0f9', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('c4258cc6a2cf4c5f96f5acc3c2d8a8d2', 6, 1, 1, 0);
INSERT INTO `stock_log` VALUES ('c4fba100330a4e5e82229c015ba7f08a', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('c69f1f46e01a473aa6e6bc11a9a146e3', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('cbd6e3f4d5a6460bae50eaeb9bd35144', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('de08cbdee52f4b37a0aca6e422bbd4b3', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('e072864ab85047f08a92c840db44bc85', 5, 1, 1, 1);
INSERT INTO `stock_log` VALUES ('e0c4c63b38c64032b018e3614f0d739b', 6, 1, 1, 0);
INSERT INTO `stock_log` VALUES ('ee5d22b62289496b98a8076ad0843670', 5, 1, 1, 1);

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `Id` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(64) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL DEFAULT '',
  `Gender` tinyint NOT NULL DEFAULT 0 COMMENT '1for male . 0 for female',
  `Age` int NOT NULL,
  `Phone` varchar(255) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL DEFAULT '',
  `Register_Mod` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL DEFAULT '' COMMENT 'Method for registration.',
  `Third_party_id` varchar(64) CHARACTER SET utf16 COLLATE utf16_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`Id`) USING BTREE,
  UNIQUE INDEX `telphone_unique_index`(`Phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf16 COLLATE = utf16_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (1, 'Chae fwb', 1, 25, '16103516', 'Phone', '2');
INSERT INTO `user_info` VALUES (13, 'Mufeng Niu', 1, 12, '023465', 'Phone', '');
INSERT INTO `user_info` VALUES (15, 'cahrafa', 1, 12, '1234567', 'Phone', '');
INSERT INTO `user_info` VALUES (16, 'geg', 1, 32, '15521', 'Phone', '');
INSERT INTO `user_info` VALUES (17, 'TEST USER', 1, 1, '1234', 'Phone', '');
INSERT INTO `user_info` VALUES (18, 'Charlie Hustle', 1, 34, '123456', 'Phone', '');

SET FOREIGN_KEY_CHECKS = 1;
