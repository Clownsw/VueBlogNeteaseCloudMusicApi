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
import org.jctools.maps.NonBlockingHashMap;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.smilex.vueblog.config.MessageCode.*;

/**
 * @author smilex
 * @date 2022/9/23/23:31
 * @since 1.0
 */
@Slf4j
public class Distribution {
    private static final NonBlockingHashMap<String, LocalDateTime> MUSIC_LAST_SEE_MAP = new NonBlockingHashMap<>();
    private static final long MUSIC_LAST_SEE_CACHE_MILLIS = 1000 * 60 * 5;

    /**
     * 检查MusicLastSeeMap, 删除超过MUSIC_LAST_SEE_CACHE_MILLIS
     */
    public static void checkMusicLastSeeMap() {
        final long currentTime = System.currentTimeMillis();
        List<LocalDateTime> lastSeeList = MUSIC_LAST_SEE_MAP.keySet()
                .stream()
                .map(MUSIC_LAST_SEE_MAP::get)
                .filter(v -> currentTime - v.toEpochSecond(ZoneOffset.of("+8")) >= MUSIC_LAST_SEE_CACHE_MILLIS)
                .collect(Collectors.toList());

        if (lastSeeList.size() > 0) {
            lastSeeList.forEach(v -> {
                String key;
                if ((key = CommonUtil.traverseMapGetKeyByValue(MUSIC_LAST_SEE_MAP, v)) != null) {
                    try {
                        MUSIC_LAST_SEE_MAP.remove(key);
                    } catch (Exception ignore) {
                    }
                }
            });
        }
    }

    public static LocalDateTime getAndUpdateMusicStatus(String musicId) {
        return MUSIC_LAST_SEE_MAP.put(musicId, LocalDateTime.now());
    }

    public static void run(ChannelHandlerContext ctx, Message message) {
        switch (message.getActionType()) {
            case REQUEST_DOWNLOAD_AND_UPLOAD: {
                Map<String, Object> content = message.getContent();

                final String url = (String) content.get("url");
                final String musicId = (String) content.get("musicId");
                final LocalDateTime musicLastSeeTime = getAndUpdateMusicStatus(musicId);

                CommonUtil.submit(Distribution::checkMusicLastSeeMap);

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
                    if (musicLastSeeTime != null) {
                        return;
                    }

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
