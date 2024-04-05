package com.seinfish.pay.common.enums;

import static com.seinfish.pay.common.exception.ErrorCode.PAY_CHANNEL_ERROR;

import com.seinfish.pay.common.exception.PayException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支付渠道枚举
 */
@RequiredArgsConstructor
public enum PayChannelEnum {

    /**
     * 支付宝
     */
    ALI_PAY(0, "ALI_PAY", "支付宝"),
    /**
     * 微信
     */
    WECHAT_PAY(1, "WCHAT_PAY", "微信");
    @Getter
    private final Integer code;

    @Getter
    private final String name;

    @Getter
    private final String value;

    public static String findNameByCode(Integer code){
        PayChannelEnum[] values = PayChannelEnum.values();
        for (PayChannelEnum value : values) {
            if (value.getCode().equals(code)){
                return value.getName();
            }
        }
        throw new PayException(PAY_CHANNEL_ERROR);
    }
}
