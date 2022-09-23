package cn.smilex.vueblog.handler;

import cn.smilex.req.HttpRequest;
import cn.smilex.req.HttpResponse;
import cn.smilex.req.Requests;
import cn.smilex.vueblog.Application;
import cn.smilex.vueblog.protocol.Message;
import com.upyun.RestManager;
import com.upyun.UpYunUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @author smilex
 * @date 2022/9/23/23:31
 * @since 1.0
 */
@Slf4j
public class Distribution {
    public static void run(Message message) {
        switch (message.getActionType()) {
            case 1: {
                final String url = (String) message.getContent()
                        .get("url");
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
                                Application.REST_MANAGER
                                        .writeFile("/1.mp3", httpResponse.getDataByte(), params);
                            } catch (Exception e) {
                                log.error("", e);
                            }
                        });
                break;
            }

            default: {
                break;
            }
        }
    }
}
