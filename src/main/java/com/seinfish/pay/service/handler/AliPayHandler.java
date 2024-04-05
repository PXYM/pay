package com.seinfish.pay.service.handler;

import static com.seinfish.pay.common.constant.PayConstant.ROUNDING_MODE;
import static com.seinfish.pay.common.exception.ErrorCode.ALIPAY_CALLBACK_UPDATE_ERROR;
import static com.seinfish.pay.common.exception.ErrorCode.ALIPAY_REMOTE_ERROR;
import static com.seinfish.pay.common.exception.ErrorCode.ALIPAY_TRADE_QUERY_UPDATE_ERROR;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seinfish.pay.common.enums.PayChannelEnum;
import com.seinfish.pay.common.enums.TradeStatusEnum;
import com.seinfish.pay.common.exception.PayException;
import com.seinfish.pay.config.AliPayProperties;
import com.seinfish.pay.dao.dto.AliPayCallbackDTO;
import com.seinfish.pay.dao.dto.PayRequestDTO;
import com.seinfish.pay.dao.dto.PayResponseDTO;
import com.seinfish.pay.dao.entity.PayDO;
import com.seinfish.pay.dao.mapper.PayMapper;
import com.seinfish.pay.service.handler.base.PayHandler;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 阿里支付处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliPayHandler implements PayHandler {

  private final AliPayProperties aliPayProperties;
  private final PayMapper payMapper;
  private AlipayClient alipayClient;

  @SneakyThrows(value = AlipayApiException.class)
  @PostConstruct
  public void initAliPayClient() {
    AlipayConfig alipayConfig = new AlipayConfig();
    BeanUtils.copyProperties(aliPayProperties, alipayConfig);
    alipayClient = new DefaultAlipayClient(alipayConfig);
  }


  @Override
  public PayResponseDTO pay(PayRequestDTO payRequestDTO) {
    AlipayTradePagePayModel model = new AlipayTradePagePayModel();
    model.setOutTradeNo(payRequestDTO.getOutOrderSn());
    model.setTotalAmount(payRequestDTO.getTotalAmount().toString());
    model.setSubject(payRequestDTO.getSubject());
    model.setProductCode("FAST_INSTANT_TRADE_PAY");
    AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
    request.setNotifyUrl(aliPayProperties.getNotifyUrl());
    request.setReturnUrl(aliPayProperties.getReturnUrl());
    request.setBizModel(model);
    try {
      AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
      log.info("支付宝支付请求结果: {} ，订单号：{}，子订单号：{}，订单请求号：{}，订单金额：{} ",
          response.isSuccess(),
          payRequestDTO.getOrderSn(),
          payRequestDTO.getOutOrderSn(),
          payRequestDTO.getOrderRequestId(),
          payRequestDTO.getTotalAmount());
      if (!response.isSuccess()) {
        throw new PayException(ALIPAY_REMOTE_ERROR);
      }
      return new PayResponseDTO(
          StrUtil.replace(StrUtil.replace(response.getBody(), "\"", "'"), "\n", ""));
    } catch (AlipayApiException ex) {
      throw new PayException(ALIPAY_REMOTE_ERROR);
    }
  }

  @Override
  public void payCallback(Object callbackDTO) {
    AliPayCallbackDTO payCallbackDTO = (AliPayCallbackDTO) callbackDTO;
    String outTradeNo = payCallbackDTO.getOutTradeNo();
    LambdaQueryWrapper<PayDO> wrapper = Wrappers.lambdaQuery(PayDO.class)
        .eq(PayDO::getOutOrderSn, outTradeNo)
        .eq(PayDO::getDelFlag, 0);
    PayDO payDO = new PayDO();
    payDO.setUpdateTime(new Date());
    Integer buyerPayAmount = payCallbackDTO.getBuyerPayAmount()
        .multiply(new BigDecimal("100"))
        .setScale(0, ROUNDING_MODE)
        .intValue();
    payDO.setPayAmount(buyerPayAmount);
    payDO.setGmtPayment(payCallbackDTO.getGmtPayment());
    payDO.setTradeNo(payCallbackDTO.getTradeNo());
    payDO.setStatus(TradeStatusEnum.queryActualTradeStatusCode(payCallbackDTO.getTradeStatus()));
    int update = payMapper.update(payDO, wrapper);
    if (update <= 0) {
      log.error("修改支付单支付结果失败，支付单信息：{}", JSON.toJSONString(payDO));
      throw new PayException(ALIPAY_CALLBACK_UPDATE_ERROR);
    }
    if (TradeStatusEnum.TRADE_SUCCESS.tradeCode().equals(payDO.getStatus())) {
      // 支付成功通知
    }
  }

  @Override
  public void tradeQueryAndUpdate(String outOrderNo, Integer tradeStatus) {
    AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
    AlipayTradeQueryModel model = new AlipayTradeQueryModel();
    model.setQueryOptions(List.of("trade_settle_info"));
    model.setOutTradeNo(outOrderNo);
    request.setBizModel(model);
    AlipayTradeQueryResponse response;
    try {
      response = alipayClient.execute(request);
      log.info("支付宝查询订单: {} 查询支付结果: {} 支付状态: {}",
          outOrderNo,
          response.isSuccess(),
          response.getTradeStatus());
    } catch (AlipayApiException e) {
      throw new PayException(ALIPAY_REMOTE_ERROR);
    }
    if (ObjectUtil.isNull(response) || !response.isSuccess()) {
      throw new PayException(ALIPAY_TRADE_QUERY_UPDATE_ERROR);
    }
    Integer actualTradeStatusCode = TradeStatusEnum.queryActualTradeStatusCode(response.getTradeStatus());
    if (NumberUtil.equals(actualTradeStatusCode, tradeStatus)) {
      // 支付状态未改变
      return;
    }
    LambdaQueryWrapper<PayDO> wrapper = Wrappers.lambdaQuery(PayDO.class)
        .eq(PayDO::getOutOrderSn, outOrderNo)
        .eq(PayDO::getDelFlag, 0);
    PayDO payDO = new PayDO();
    payDO.setUpdateTime(new Date());
    payDO.setTradeNo(response.getTradeNo());
    payDO.setStatus(actualTradeStatusCode);
    payDO.setPayAmount(
        new BigDecimal(response.getPayAmount())
        .multiply(new BigDecimal("100"))
        .setScale(0, ROUNDING_MODE)
        .intValue());
    // TODO 主动查询无法获取用户支付时间
    int update = payMapper.update(payDO, wrapper);
    if (update <= 0) {
      throw new PayException(ALIPAY_TRADE_QUERY_UPDATE_ERROR);
    }
  }

  @Override
  public String mark() {
    return PayChannelEnum.ALI_PAY.getName();
  }
}
