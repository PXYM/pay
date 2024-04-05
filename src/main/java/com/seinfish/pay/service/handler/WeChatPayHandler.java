package com.seinfish.pay.service.handler;

import com.seinfish.pay.common.enums.PayChannelEnum;
import com.seinfish.pay.dao.dto.PayRequestDTO;
import com.seinfish.pay.dao.dto.PayResponseDTO;
import com.seinfish.pay.service.handler.base.PayHandler;
import org.springframework.stereotype.Service;

/**
 * 微信支付处理
 */
@Service
public class WeChatPayHandler implements PayHandler {

  @Override
  public PayResponseDTO pay(PayRequestDTO payRequestDTO) {
    return null;
  }

  @Override
  public void payCallback(Object callbackDTO) {

  }

  @Override
  public void tradeQueryAndUpdate(String outTradeNo, Integer tradeStatus) {

  }

  @Override
  public String mark() {
    return PayChannelEnum.WECHAT_PAY.getName();
  }
}
