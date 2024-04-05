package com.seinfish.pay.dao.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * alipay 回调接收对象
 */
@Data
public class WeChatPayCallbackDTO {

  /**
   * 支付渠道
   */
  private Integer channel;

  /**
   * 支付状态
   */
  @JsonAlias("trade_status")
  private String tradeStatus;

  /**
   * 支付凭证号
   */
  @JsonAlias("trade_no")
  private String tradeNo;

  /**
   * 子订单号
   */
  @JsonAlias("out_trade_no")
  private String outTradeNo;

  /**
   * 买家付款时间
   */
  @JsonAlias("gmt_payment")
  private Date gmtPayment;

  /**
   * 买家付款金额
   */
  @JsonAlias("buyer_pay_amount")
  private BigDecimal buyerPayAmount;

}
