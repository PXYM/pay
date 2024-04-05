package com.seinfish.pay.utils;

import cn.hutool.core.date.DateUtil;
import com.seinfish.pay.common.exception.PayException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;


import java.util.Date;

import static com.seinfish.pay.common.constant.PayConstant.PAY_SN_INCR_BIT;
import static com.seinfish.pay.common.constant.PayConstant.PAY_SN_INCR_PRE;
import static com.seinfish.pay.common.exception.ErrorCode.PAY_SN_CREATE_ERROR;

/**
 * 支付工具类
 */
@Slf4j
public final class PayUtil {

    private static StringRedisTemplate stringRedisTemplate;

    static void setStringRedisTemplate(StringRedisTemplate template){
        stringRedisTemplate = template;
    }

    /**
     * return paySn 20240403171851000001
     */
    public static String paySn() {
        int maxRetry = 10;
        int count = 0;
        do {
            String paySN = DateUtil.format(new Date(), "yyyyMMddHHmmss");
            String key = PAY_SN_INCR_PRE + paySN;
            Long incrVal = stringRedisTemplate.opsForValue().increment(key);
            stringRedisTemplate.expire(key, 5000L, TimeUnit.SECONDS);
            String strIncrVal = String.valueOf(incrVal);
            if (strIncrVal.length() < 6) {
                // 补全长度
                int lackLen = PAY_SN_INCR_BIT - strIncrVal.length();
                return paySN + "0".repeat(lackLen) + strIncrVal;
            }
            try {
                // 一秒内超过最大值 则过一秒后获取
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                log.error("支付流水号生成失败 {}", e.getMessage());
                throw new PayException(PAY_SN_CREATE_ERROR);
            }
            if (count++ > maxRetry){
                throw new PayException(PAY_SN_CREATE_ERROR);
            }
        } while (true);
    }
}
