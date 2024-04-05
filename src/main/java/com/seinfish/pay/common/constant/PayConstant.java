package com.seinfish.pay.common.constant;

import java.math.RoundingMode;

/**
 * 支付常量
 */
public class PayConstant {

    public static final String PAY_SN_INCR_PRE = "pay:pay-sn:incr:";
    /**
     * 订单自增位数
     */
    public static final int PAY_SN_INCR_BIT = 6;
    /**
     * 金额取整模式
     */
    public static final RoundingMode ROUNDING_MODE = java.math.RoundingMode.HALF_UP;
}
