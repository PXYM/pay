package com.seinfish.pay.dao.dto;

import java.math.BigDecimal;
import lombok.Data;

/**
 * 支付请求实体
 */
@Data
public class PayRequestDTO {
  /**
   * 交易环境，H5、小程序、网站等
   */
  private Integer tradeType;
  /**
   * 订单号
   */
  private String orderSn;

  /**
   * 子订单号
   */
  private String outOrderSn;

  /**
   * 支付渠道
   */
  private Integer channel;

  /**
   * 商户订单号
   */
  private String orderRequestId;

  /**
   * 订单总金额
   * 单位为元，精确到小数点后两位，取值范围：[0.01,100000000]
   */
  private BigDecimal totalAmount;

  /**
   * 订单标题
   * 注意：不可使用特殊字符，如 /，=，& 等
   */
  private String subject;
}
