package com.seinfish.pay.common.web;

import static com.seinfish.pay.common.exception.ErrorCode.PAY_SERVER_ERROR;

import com.seinfish.pay.common.exception.PayException;
import java.util.Optional;

public final class Results {

    /**
     * 构造成功响应
     */
    public static Result<Void> success() {
        return new Result<Void>()
                .setCode(Result.SUCCESS_CODE);
    }

    /**
     * 构造带返回数据的成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(Result.SUCCESS_CODE)
                .setData(data);
    }

    /**
     * 构建服务端失败响应
     */
    protected static Result<Void> failure() {
        return new Result<Void>()
                .setCode(PAY_SERVER_ERROR.code())
                .setMessage(PAY_SERVER_ERROR.message());
    }

    /**
     * 通过 {@link PayException} 构建失败响应
     */
    protected static Result<Void> failure(PayException exception) {
        String errorCode = Optional.ofNullable(exception.errorCode)
                .orElse(PAY_SERVER_ERROR.code());
        String errorMessage = Optional.ofNullable(exception.errorCode)
                .orElse(PAY_SERVER_ERROR.message());
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }

    /**
     * 通过 errorCode、errorMessage 构建失败响应
     */
    protected static Result<Void> failure(String errorCode, String errorMessage) {
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }
}