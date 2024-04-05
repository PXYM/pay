package com.seinfish.pay.dao.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * alipay 回调接收对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeQueryRespDTO {

  /**
   * 支付流水号
   */
  private String paySn;

  /**
   * 订单号
   */
  private String orderSn;

  /**
   * 子订单号 在完整的订单中拆分的子订单，如购物车购买多个商品
   */
  private String outOrderSn;

  /**
   * 支付渠道
   */
  private Integer channel;

  /**
   * 支付环境
   */
  private Integer tradeType;

  /**
   * 订单标题
   */
  private String subject;

  /**
   * 交易凭证号 由第三方支付系统（支付宝）的唯一编号
   */
  private String tradeNo;

  /**
   * 商户订单号
   * 由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复
   */
  private String orderRequestId;

  /**
   * 交易总金额
   */
  private Integer totalAmount;

  /**
   * 付款时间
   */
  private Date gmtPayment;

  /**
   * 支付金额
   */
  private Integer payAmount;

  /**
   * 支付状态
   */
  private Integer status;

  /**
   * 创建时间
   */
  @TableField(fill = FieldFill.INSERT)
  private Date createTime;

  /**
   * 修改时间
   */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Date updateTime;


}
