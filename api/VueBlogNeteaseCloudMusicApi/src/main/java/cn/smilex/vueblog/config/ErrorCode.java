package cn.smilex.vueblog.config;

import lombok.Getter;

/**
 * @author smilex
 * @date 2022/9/23 16:19
 */
@Getter
public enum ErrorCode {
    CONNECTION_EXCEPTION_CLOSE(1, "与服务端连接异常断开!");
    final Integer errorCode;
    final String errorMessage;

    ErrorCode(Integer errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
