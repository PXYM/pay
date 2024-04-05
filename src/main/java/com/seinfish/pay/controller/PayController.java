package com.seinfish.pay.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.seinfish.pay.common.enums.PayChannelEnum;
import com.seinfish.pay.common.web.Result;
import com.seinfish.pay.common.web.Results;
import com.seinfish.pay.dao.dto.AliPayCallbackDTO;
import com.seinfish.pay.dao.dto.PayRequestDTO;
import com.seinfish.pay.dao.dto.PayResponseDTO;
import com.seinfish.pay.dao.dto.TradeQueryRespDTO;
import com.seinfish.pay.service.IPayService;
import com.seinfish.pay.service.handler.base.PayHandler;
import com.seinfish.pay.service.handler.base.PayHandlerSelector;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class PayController {

  private final IPayService payService;
  private final PayHandlerSelector payHandlerSelector;

  /**
   * 统一支付接口
   */
  @PostMapping("/pay/create")
  public Result<PayResponseDTO> pay(@RequestBody PayRequestDTO requestDTO) {
    return Results.success(payService.pay(requestDTO));
  }

  /**
   * 交易订单查询
   */
  @GetMapping("/pay/trade")
  public Result<TradeQueryRespDTO> tradeQuery(String outOrderNo){
    return Results.success(payService.tradeQuery(outOrderNo));
  }

  /**
   * alipay 支付回调接口
   */
  @PostMapping("/pay/alipay/callback")
  public void alipayCallback(@RequestParam Map<String, Object> requestParam) {
    AliPayCallbackDTO payCallbackDTO = BeanUtil.mapToBean(
        requestParam,
        AliPayCallbackDTO.class,
        true,
        CopyOptions.create());
    payCallbackDTO.setChannel(PayChannelEnum.ALI_PAY.getCode());
    PayHandler payHandler = payHandlerSelector.select(
        PayChannelEnum.findNameByCode(payCallbackDTO.getChannel()));
    payHandler.payCallback(payCallbackDTO);
  }

  /**
   * wechat 支付回调接口
   */
  @PostMapping("/pay/wechat/callback")
  public void wechatCallback() {
  }
}
