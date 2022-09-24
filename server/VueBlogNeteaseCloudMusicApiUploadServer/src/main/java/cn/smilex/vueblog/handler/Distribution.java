package cn.smilex.vueblog.handler;

import cn.smilex.req.HttpRequest;
import cn.smilex.req.HttpResponse;
import cn.smilex.req.Requests;
import cn.smilex.vueblog.Application;
import cn.smilex.vueblog.protocol.Message;
import cn.smilex.vueblog.util.MessageUtil;
import com.upyun.RestManager;
import com.upyun.UpYunUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.util.HashMap;

import static cn.smilex.vueblog.config.ResponseCode.*;

/**
 * @author smilex
 * @date 2022/9/23/23:31
 * @since 1.0
 */
@Slf4j
public class Distribution {
    public static void run(ChannelHandlerContext ctx, Message message) {
        switch (message.getActionType()) {
            case REQUEST_DOWNLOAD_AND_UPLOAD: {
                var content = message.getContent();
                final String url = (String) content.get("url");
                final String filePath = (String) content.get("filePath");
                Thread.ofVirtual()
                        .start(() -> {
                            HttpResponse httpResponse = Requests.requests
                                    .request(
                                            HttpRequest.build()
                                                    .setUrl(url)
                                                    .setMethod(Requests.REQUEST_METHOD.GET)
                                                    .setEnableDataByte(true)
                                    );

                            var params = new HashMap<String, String>(1);
                            params.put(RestManager.PARAMS.CONTENT_MD5.getValue(), UpYunUtils.md5(httpResponse.getDataByte()));
                            try {
                                Response response = Application.REST_MANAGER
                                        .writeFile(filePath, httpResponse.getDataByte(), params);

                                var responseMessageContent = new HashMap<String, Object>(1);
                                assert response.body() != null;
                                responseMessageContent.put("body", response.body().string());
                                ctx.writeAndFlush(MessageUtil.buildMessage(responseMessageContent));
                            } catch (Exception e) {
                                log.error("", e);
                            }
                        });
                break;
            }

            case DEFAULT_REQUEST_OR_RESPONSE:
            case RESPONSE_UPLOAD_RESULT:
            default: {
                break;
            }
        }
    }
}
