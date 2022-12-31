package cn.smilex.vueblog.handler;

import cn.smilex.req.HttpRequest;
import cn.smilex.req.HttpResponse;
import cn.smilex.req.Requests;
import cn.smilex.vueblog.pojo.UploadResult;
import cn.smilex.vueblog.protocol.Message;
import cn.smilex.vueblog.util.CommonUtil;
import cn.smilex.vueblog.util.MessageUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static cn.smilex.vueblog.config.MessageCode.*;

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
                Map<String, Object> content = message.getContent();

                final String url = (String) content.get("url");
                final String musicId = (String) content.get("musicId");
                String filePath = (String) content.get("filePath");
                if (filePath.lastIndexOf(".") == -1) {
                    filePath += ".mp3";
                }

                if (CommonUtil.UPYUN_SERVICE.existsFile(filePath)) {
                    MessageUtil.buildAndSendResponseMessage(
                            ctx.channel(),
                            filePath,
                            musicId
                    );
                } else {
                    HttpResponse httpResponse = Requests.requests
                            .request(
                                    HttpRequest.build()
                                            .setUrl(url)
                                            .setMethod(Requests.REQUEST_METHOD.GET)
                                            .setEnableDataByte(true)
                            );

                    try {
                        UploadResult uploadResult = CommonUtil.UPYUN_SERVICE.uploadFile(filePath, httpResponse.getDataByte());
                        if (uploadResult.isError()) {
                            log.error("upload error: {}", uploadResult.getMessage());
                        } else {
                            MessageUtil.buildAndSendResponseMessage(
                                    ctx.channel(),
                                    filePath,
                                    musicId
                            );
                        }
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
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
