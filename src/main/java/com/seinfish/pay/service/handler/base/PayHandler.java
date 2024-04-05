package com.seinfish.pay.service.handler.base;

import com.seinfish.pay.dao.dto.PayRequestDTO;
import com.seinfish.pay.dao.dto.PayResponseDTO;

/**
 * 支付处理器接口
 */
public interface PayHandler {

  PayResponseDTO pay(PayRequestDTO payRequestDTO);

  void payCallback(Object callbackDTO);

  /**
   * 查询交易并且更新最新数据
   * @param outOrderNo 子订单号
   * @param tradeStatus 当前订单状态
   */
  void tradeQueryAndUpdate(String outOrderNo, Integer tradeStatus);

  /**
   * 支付渠道标识
   */
  String mark();

}
