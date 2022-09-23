package cn.smilex.vueblog.netty.handler;

import cn.smilex.vueblog.netty.protocol.Message;
import lombok.extern.slf4j.Slf4j;

import static cn.smilex.vueblog.config.ResponseCode.*;

/**
 * @author smilex
 * @date 2022/9/23/23:31
 * @since 1.0
 */
@Slf4j
public class Distribution {
    public static void run(Message message) {
        switch (message.getActionType()) {
            case RESPONSE_UPLOAD_RESULT: {
                break;
            }

            case DEFAULT_REQUEST_OR_RESPONSE:
            case REQUEST_DOWNLOAD_AND_UPLOAD:
            default: {
            }
        }
    }
}
