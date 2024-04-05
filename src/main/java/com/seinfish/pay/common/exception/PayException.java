package com.seinfish.pay.common.exception;


/**
 * 支付异常
 */
public class PayException extends RuntimeException{
  public final String errorCode;

  public final String errorMessage;

  public PayException(IErrorCode errorCode) {
    super(errorCode.message());
    this.errorCode = errorCode.code();
    this.errorMessage = errorCode.message();
  }
}
