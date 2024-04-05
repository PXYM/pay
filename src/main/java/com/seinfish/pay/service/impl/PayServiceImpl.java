package com.seinfish.pay.service.impl;

import static com.seinfish.pay.common.constant.PayConstant.ROUNDING_MODE;
import static com.seinfish.pay.common.exception.ErrorCode.PAY_RECORD_CREATE_ERROR;
import static com.seinfish.pay.common.exception.ErrorCode.PAY_RECORD_REPEAT_ERROR;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seinfish.pay.common.enums.PayChannelEnum;
import com.seinfish.pay.common.enums.TradeStatusEnum;
import com.seinfish.pay.common.exception.PayException;
import com.seinfish.pay.dao.dto.TradeQueryRespDTO;
import com.seinfish.pay.dao.entity.PayDO;
import com.seinfish.pay.dao.dto.PayRequestDTO;
import com.seinfish.pay.dao.dto.PayResponseDTO;
import com.seinfish.pay.dao.mapper.PayMapper;
import com.seinfish.pay.service.IPayService;
import com.seinfish.pay.service.handler.base.PayHandler;
import com.seinfish.pay.service.handler.base.PayHandlerSelector;
import com.seinfish.pay.utils.PayUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements IPayService {

    private final PayMapper payMapper;
    private final PayHandlerSelector payHandlerSelector;

    @Override
    public PayResponseDTO pay(PayRequestDTO payRequestDTO){
        String outTradeNo = payRequestDTO.getOutOrderSn();
        LambdaQueryWrapper<PayDO> wrapper = Wrappers.lambdaQuery(PayDO.class)
            .eq(PayDO::getOutOrderSn, outTradeNo)
            .eq(PayDO::getDelFlag, 0);
        PayDO one = payMapper.selectOne(wrapper);
        // 支付已经创建并且处于等待支付状态
        if (ObjectUtil.isNotNull(one)
            && !TradeStatusEnum.WAIT_BUYER_PAY.tradeCode().equals(one.getStatus())){
            throw new PayException(PAY_RECORD_REPEAT_ERROR);
        }
        // 在创建支付记录前请求 防止请求失败去
        PayHandler handler = payHandlerSelector.select(PayChannelEnum.findNameByCode(payRequestDTO.getChannel()));
        PayResponseDTO pay = handler.pay(payRequestDTO);

        if (ObjectUtil.isNull(one)){
            PayDO payDO = new PayDO();
            BeanUtils.copyProperties(payRequestDTO, payDO);
            payDO.setTotalAmount(payRequestDTO.getTotalAmount()
                .multiply(new BigDecimal("100"))
                // 四舍五入
                .setScale(0, ROUNDING_MODE)
                .intValue()
            );
            payDO.setPaySn(PayUtil.paySn());
            payDO.setStatus(TradeStatusEnum.WAIT_BUYER_PAY.tradeCode());
            payDO.setCreateTime(new Date());
            payDO.setUpdateTime(new Date());
            int insert = payMapper.insert(payDO);
            if (insert <= 0){
                log.error("支付单创建失败: {}", JSON.toJSONString(payRequestDTO));
                throw new PayException(PAY_RECORD_CREATE_ERROR);
            }
        }
        return pay;
    }

    @Override
    public TradeQueryRespDTO tradeQuery(String outOrderNo) {
        LambdaQueryWrapper<PayDO> wrapper = Wrappers.lambdaQuery(PayDO.class)
            .eq(PayDO::getOutOrderSn, outOrderNo)
            .eq(PayDO::getDelFlag, 0);
        PayDO one = payMapper.selectOne(wrapper);
        Integer tradeStatus = one.getStatus();
        // 如果交易状态还停留在未支付需要查询第三方服务交易情况
        if (TradeStatusEnum.WAIT_BUYER_PAY.tradeCode().equals(tradeStatus)) {
            PayHandler handler = payHandlerSelector.select(
                PayChannelEnum.findNameByCode(one.getChannel()));
            handler.tradeQueryAndUpdate(outOrderNo, tradeStatus);
        }
        // 加锁防止读缓存
        one = payMapper.selectOne(wrapper.last("for update"));
        return BeanUtil.copyProperties(one, TradeQueryRespDTO.class);
    }
}
