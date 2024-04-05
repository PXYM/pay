package com.seinfish.pay.service;

import com.seinfish.pay.dao.dto.PayRequestDTO;
import com.seinfish.pay.dao.dto.PayResponseDTO;
import com.seinfish.pay.dao.dto.TradeQueryRespDTO;

/**
 * 统一支付接口
 */
public interface IPayService {

    /**
     * 支付
     * @param payRequestDTO 支付请求参数
     * @return 请求支付结果 alipay为表单代码
     */
    PayResponseDTO pay(PayRequestDTO payRequestDTO);

    TradeQueryRespDTO tradeQuery(String outOrderNo);
}
