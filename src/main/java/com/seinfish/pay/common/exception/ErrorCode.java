package com.seinfish.pay.common.exception;

/**
 * 基础错误码定义
 */
public enum ErrorCode implements IErrorCode {
    PAY_SERVER_ERROR("P10000","系统执行出错"),
    PAY_SERVER_LOAD_PAY_HANDLER_ERROR("P10001","系统加载支付处理器异常"),
    PAY_SN_CREATE_ERROR("P00001", "订单号创建异常"),
    PAY_CHANNEL_ERROR("P00101", "支付渠道异常"),
    TRADE_STATUS_FIND_ERROR("P00201","未找到支付状态异常"),
    PAY_RECORD_CREATE_ERROR("P00301","支付单创建异常"),
    PAY_RECORD_REPEAT_ERROR("P00302","支付单已存在"),
    ALIPAY_REMOTE_ERROR("P00401","调用支付宝系统调用异常"),
    ALIPAY_CALLBACK_UPDATE_ERROR("P00402","支付宝订单回调信息更新异常"),
    ALIPAY_TRADE_QUERY_UPDATE_ERROR("P00403","支付宝订单查询信息更新异常");
    private final String code;

    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
