## 支付系统

----

### 数据表设计
```mysql
CREATE TABLE `t_pay` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `pay_sn` varchar(64) DEFAULT NULL COMMENT '支付流水号',
    `order_sn` varchar(64) DEFAULT NULL COMMENT '订单号',
    `out_order_sn` varchar(64) DEFAULT NULL COMMENT '子订单号',
    `trade_no` varchar(256) DEFAULT NULL COMMENT '三方交易凭证号',
    `order_request_id` varchar(64) DEFAULT NULL COMMENT '商户请求标识',
    `channel` varchar(64) DEFAULT NULL COMMENT '支付渠道',
    `trade_type` varchar(64) DEFAULT NULL COMMENT '支付环境',
    `subject` varchar(512) DEFAULT NULL COMMENT '订单标题',
    `total_amount` int DEFAULT NULL COMMENT '交易总金额',
    `pay_amount` int DEFAULT NULL COMMENT '支付金额',
    `gmt_payment` datetime DEFAULT NULL COMMENT '付款时间',
    `status` varchar(32) DEFAULT NULL COMMENT '支付状态',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '修改时间',
    `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标记 0：未删除 1：删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `id` (`id`),
    KEY `idx_pay_sn` (`pay_sn`) USING BTREE,
    KEY `idx_out_order_sn` (`out_order_sn`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付表';
```

