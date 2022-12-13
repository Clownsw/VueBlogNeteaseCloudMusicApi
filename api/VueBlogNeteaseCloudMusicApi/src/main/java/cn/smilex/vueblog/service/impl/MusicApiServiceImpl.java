package cn.smilex.vueblog.service.impl;

import cn.smilex.req.Cookie;
import cn.smilex.req.HttpRequest;
import cn.smilex.req.HttpResponse;
import cn.smilex.req.Requests;
import cn.smilex.vueblog.config.MessageCode;
import cn.smilex.vueblog.config.MusicType;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.model.Music;
import cn.smilex.vueblog.model.MusicDto;
import cn.smilex.vueblog.model.Tuple;
import cn.smilex.vueblog.netty.NettyClient;
import cn.smilex.vueblog.service.MusicApiService;
import cn.smilex.vueblog.service.MusicService;
import cn.smilex.vueblog.util.CommonUtil;
import cn.smilex.vueblog.util.KwDES;
import cn.smilex.vueblog.util.MessageUtil;
import cn.smilex.vueblog.util.impl.HashMapBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.util.internal.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static cn.smilex.req.Requests.requests;

/**
 * @author smilex
 * @date 2022/9/11/18:15
 * @since 1.0
 */
@SuppressWarnings({"unused", "Duplicatess"})
@Slf4j
@Service
public class MusicApiServiceImpl implements MusicApiService {

    private RedisTemplate<String, String> redisTemplate;
    private RequestConfig requestConfig;
    private CommonUtil commonUtil;
    private MusicService musicService;
    private NettyClient nettyClient;

    private static final HashMap<String, String> DEFAULT_REQUEST_HEADER = new HashMap<>();
    private static final HashMap<String, String> KUWO_REQUEST_HEADER = new HashMap<>();

    private static final String KUWO_SEARCH_PATTERN = "\\bhttp://(.*?)\\.mp3\\b";
    private static final String KUWO_SEARCH_API = "http://www.kuwo.cn/api/www/search/searchMusicBykeyWord?key=%s&pn=%d&rn=%d";

    static {
        DEFAULT_REQUEST_HEADER.put("Content-type", "application/x-www-form-urlencoded");
        KUWO_REQUEST_HEADER.put("Accept", "application/json, text/plain, */*");
        KUWO_REQUEST_HEADER.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        KUWO_REQUEST_HEADER.put("Connection", "keep-alive");
        KUWO_REQUEST_HEADER.put("Cookie", "kw_token=OG5MLTOUW8");
        KUWO_REQUEST_HEADER.put("Referer", "http://www.kuwo.cn/search/list?key=why");
        KUWO_REQUEST_HEADER.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
        KUWO_REQUEST_HEADER.put("csrf", "OG5MLTOUW8");
    }


    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    @Lazy
    @Autowired
    public void setCommonUtil(CommonUtil commonUtil) {
        this.commonUtil = commonUtil;
    }

    @Autowired
    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }

    @Autowired
    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    /**
     * 网易云音乐搜索
     *
     * @param keyWords 关键字
     * @return json
     */
    @Override
    public String search(String keyWords) {
        return Requests.requests.fast_get(
                requestConfig.getUrl() +
                        "search" +
                        "?keywords=" + keyWords
        ).getBody();
    }

    @SneakyThrows
    @Override
    public String playListDetail(String id) {
        return commonServiceRequest(
                requestConfig.getUrl() +
                        "playlist/detail" +
                        "?id=" + id
        );
    }

    @Override
    public void emailLogin(String email, String passWord) {
        HttpRequest request = HttpRequest.build()
                .setUrl(
                        requestConfig.getUrl() +
                                "login" +
                                "?email=" + email +
                                "&password=" + passWord
                )
                .setMethod(Requests.REQUEST_METHOD.GET);
        HttpResponse response = Requests.requests.request(request);

        List<Cookie> cookies = response.getCookies();
        StringBuilder cookieString = new StringBuilder();
        for (Cookie cookie : cookies) {
            cookieString.append(cookie.getName())
                    .append("=")
                    .append(cookie.getValue())
                    .append(";");
        }
        log.info("{}", cookieString);
        requestConfig.setCookie(cookieString.toString());
    }

    /**
     * 网易云音乐歌词获取
     *
     * @param id 歌曲ID
     * @return json
     */
    @SneakyThrows
    @Override
    public String lyric(String id) {
        return commonServiceRequest(
                requestConfig.getUrl() +
                        "lyric" +
                        "?id=" + id
        );
    }

    /**
     * 新版网易云音乐真链获取
     *
     * @param id    音乐ID
     * @param level 音质级别
     * @return json
     */
    @SneakyThrows
    @Override
    public String newSongUrl(String id, String level) {
        return commonServiceRequest(
                requestConfig.getUrl() +
                        "song/url/v1" +
                        "?id=" + id +
                        "&level=" + level
        );
    }

    /**
     * unknown
     *
     * @param id     音乐ID
     * @param level  音质级别
     * @param limit  分页
     * @param offset 起始
     * @return json
     */
    @SneakyThrows
    @Override
    public String playListTrackAll(String id, String level, Integer limit, Integer offset) {
        return commonServiceRequest(
                requestConfig.getUrl() +
                        "playlist/track/all" +
                        "?id=" + id +
                        "&limit=" + limit +
                        "&offset=" + offset
        );
    }

    /**
     * VueBlog网易云音乐歌词获取
     *
     * @param id 音乐ID
     * @return json
     */
    @SneakyThrows
    @Override
    public String vueBlogLyric(String id) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String cacheValue = valueOperations.get(requestConfig.getRedisLyricCachePrefix() + id);
        if (cacheValue != null) {
            return cacheValue;
        }

        String lyricJson = lyric(id);
        if (lyricJson == null) {
            throw new RuntimeException("not found!");
        }

        JsonNode root = CommonUtil.OBJECT_MAPPER.readTree(lyricJson);
        JsonNode lrc = root.get("lrc");
        String lyric = lrc.get("lyric").asText();
        valueOperations.set(requestConfig.getRedisLyricCachePrefix() + id, lyric, Duration.ofDays(7));
        return lyric;
    }

    /**
     * VueBlog网易云音乐真链获取
     *
     * @param id    音乐ID
     * @param level 音质级别
     * @return json
     */
    @SneakyThrows
    @Override
    public String vueBlogSongUrl(String id, String level, boolean isPlay) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();

        String cacheValue = valueOperations.get(requestConfig.getRedisMusicUrlCachePrefix() + id);
        if (StringUtils.isNotBlank(cacheValue)) {
            if (!commonUtil.musicIsNotFree(MusicType.WYY, id, level) && isPlay) {
                return null;
            }

            return cacheValue;
        }

        cacheValue = musicService.getMusicUrlByMusicId(Long.parseLong(id));
        if (StringUtils.isNotBlank(cacheValue)) {
            if (!commonUtil.musicIsNotFree(MusicType.WYY, id, level) && isPlay) {
                return null;
            }

            valueOperations.set(requestConfig.getRedisMusicUrlCachePrefix() + id, cacheValue);
            return cacheValue;
        }

        String url;

        Tuple<Boolean, String> result = commonUtil.getMusicIsNoteFreeAndUrl(id, level);
        if (!result.getLeft()) {
            MusicDto musicDto = commonUtil.getMusicDtoInRedisNetEaseCloudCacheSetById(Long.parseLong(id));
            if (musicDto != null) {
                url = kuWoSongUrl(musicDto.getKuWoId());
            } else {
                String musicSongDetailJson = songDetail(id);
                JsonNode musicSongDetailRoot = CommonUtil.OBJECT_MAPPER
                        .readTree(musicSongDetailJson);
                JsonNode musicSongsOne = musicSongDetailRoot.get("songs")
                        .get(0);

                String kuWoSearchJson = kuWoSearch(
                        musicSongsOne
                                .get("name")
                                .asText(),
                        1,
                        1
                );
                JsonNode kuWoSearchJsonRoot = CommonUtil.OBJECT_MAPPER.readTree(kuWoSearchJson);
                String kuWoMusicId = kuWoSearchJsonRoot.get("data")
                        .get("list")
                        .get(0)
                        .get("musicrid")
                        .asText();
                String kuWoMusicIdStr = kuWoMusicId.replace("MUSIC_", "");
                url = kuWoSongUrl(kuWoMusicIdStr);

                setOperations.add(
                        requestConfig.getRedisNetEaseCloudCache(),
                        commonUtil.toJsonStr(new MusicDto(
                                musicSongsOne.get("id").asLong(),
                                musicSongsOne.get("name").asText(),
                                kuWoMusicIdStr
                        ))
                );
            }

            if (!StringUtil.isNullOrEmpty(url)) {
                commonUtil.createTask(() -> {
                    try {
                        MessageUtil.buildAndMessageMessage(
                                nettyClient.getChannel(),
                                MessageCode.REQUEST_DOWNLOAD_AND_UPLOAD,
                                new HashMapBuilder<String, Object>(3)
                                        .put("url", url)
                                        .put("musicId", id)
                                        .put("filePath", "/kuwo/" + commonUtil.parseUrlGetFileName(url))
                                        .getMap()
                        );
                    } catch (Exception e) {
                        log.error("", e);
                    }
                });
            }
            commonUtil.createTask(() -> musicService.cacheMusicNotFreeInAll(MusicType.KUWO, id, false));
            if (isPlay) {
                return null;
            }
        } else {
            url = result.getRight();
            if (!StringUtil.isNullOrEmpty(url)) {
                commonUtil.createTask(() -> {
                    try {
                        MessageUtil.buildAndMessageMessage(
                                nettyClient.getChannel(),
                                MessageCode.REQUEST_DOWNLOAD_AND_UPLOAD,
                                new HashMapBuilder<String, Object>(3)
                                        .put("url", url)
                                        .put("musicId", id)
                                        .put("filePath", "/wyy/" + commonUtil.parseUrlGetFileName(url))
                                        .getMap()
                        );
                    } catch (Exception e) {
                        log.error("", e);
                    }
                });
            }
            commonUtil.createTask(() -> musicService.cacheMusicNotFreeInAll(MusicType.WYY, id, false));
        }

        valueOperations.set(requestConfig.getRedisMusicUrlCachePrefix() + id, url, Duration.ofMinutes(3));
        return url;
    }

    /**
     * 酷我音乐真链获取
     *
     * @param id 音乐ID
     * @return json
     */
    @Override
    public String kuWoSongUrl(String id) {
        byte[] msg = String.format(CommonUtil.KW_REQUEST_PARAM_TEMPLATE, id).getBytes(StandardCharsets.UTF_8);
        String param = new String(
                Base64.getEncoder()
                        .encode(
                                KwDES.encrypt(
                                        msg,
                                        msg.length,
                                        KwDES.SECRET_KEY,
                                        KwDES.SECRET_KEY_LENGTH
                                )
                        ),
                StandardCharsets.UTF_8
        );

        HttpResponse httpResponse = requests.request(
                HttpRequest.build()
                        .setMethod(Requests.REQUEST_METHOD.GET)
                        .setUrl(String.format("http://mobi.kuwo.cn/mobi.s?f=kuwo&q=%s", param))
                        .setHeaders(CommonUtil.KW_REQUEST_HEADERS)
        );

        try {
            Map<String, Object> map = responseToMap(httpResponse.getBody());
            String url;

            if ((url = (String) map.get("url")) != null) {
                return url;
            }
        } catch (Exception e) {
            log.error("", e);
        }

        throw new RuntimeException("not found!");
    }

    public static Map<String, Object> responseToMap(String value) {
        Map<String, Object> map = new HashMap<>(0);
        List<String> valueList = new ArrayList<>();

        int begin = 0;
        int end;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c == '\r' && value.charAt(i + 1) == '\n') {
                end = i + 1;
                valueList.add(value.substring(begin, end).trim());
                begin = end;
            }
        }

        valueList.forEach(v -> {
            String[] split = v.split("=");
            map.put(split[0], split[1]);
        });

        valueList.clear();

        return map;
    }

    /**
     * 酷我音乐搜索
     *
     * @param key 关键字
     * @param pn  unknown
     * @param fn  unknown
     * @return json
     */
    @SneakyThrows
    @Override
    public String kuWoSearch(String key, Integer pn, Integer fn) {
        String body = Requests.requests.request(
                HttpRequest.build()
                        .setUrl(String.format(KUWO_SEARCH_API, URLEncoder.encode(key, "UTF8"), pn, fn))
                        .setMethod(Requests.REQUEST_METHOD.GET)
                        .setHeaders(KUWO_REQUEST_HEADER)
        ).getBody();
        JsonNode root = CommonUtil.OBJECT_MAPPER.readTree(body);
        JsonNode success = root.get("success");
        if (success != null && !success.asBoolean()) {
            return kuWoSearch(key, pn, fn);
        }
        return body;
    }

    /**
     * 网易云音乐详情
     *
     * @param id 音乐ID
     * @return json
     */
    @Override
    public String songDetail(String id) {
        return commonServiceRequest(
                requestConfig.getUrl() +
                        "song/detail" +
                        "?ids=" + id
        );
    }

    /**
     * VueBlog播放音乐(因https无法加载http资源, 内置请求响应音频)
     *
     * @param id       音乐ID
     * @param level    音质级别
     * @param response 响应对象
     */
    @SneakyThrows
    @Override
    public void vueBlogPlaySong(String id, String level, HttpServletResponse response) {
        HttpResponse httpResponse = Requests.requests.request(
                HttpRequest.build()
                        .setUrl(vueBlogSongUrl(id, level, false))
                        .setMethod(Requests.REQUEST_METHOD.GET)
                        .setEnableDataByte(true)
        );

        response.addHeader(HttpHeaders.CONTENT_TYPE, "audio/mpeg");

        ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.write(httpResponse.getDataByte());
        servletOutputStream.flush();
        servletOutputStream.close();
    }

    /**
     * VueBlog格式网易云歌单列表
     *
     * @param id     歌单ID
     * @param level  音质级别
     * @param limit  分页
     * @param offset 起始
     * @return 列表
     */
    @SneakyThrows
    @Override
    public ConcurrentLinkedQueue<Music> vueBlogMusicList(String id, String level, Integer limit, Integer offset) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String cacheValue = valueOperations.get(requestConfig.getRedisMusicInfoCachePrefix() + id);
        if (cacheValue != null) {
            return CommonUtil.OBJECT_MAPPER.readValue(cacheValue, new TypeReference<ConcurrentLinkedQueue<Music>>() {
            });
        }

        JsonNode root = CommonUtil.OBJECT_MAPPER.readTree(playListTrackAll(id, level, limit, offset));
        if (root.get("code").asInt() != 200) {
            throw new RuntimeException("获取歌单列表失败!");
        }

        JsonNode songs = root.get("songs");
        if (songs.size() == 0) {
            return new ConcurrentLinkedQueue<>();
        }

        ConcurrentLinkedQueue<Music> musicList = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < songs.size(); i++) {
            JsonNode musicInfo = songs.get(i);
            Long musicId = musicInfo.get("id").asLong();

            StringBuilder arStr = new StringBuilder();
            JsonNode ar = musicInfo.get("ar");
            for (JsonNode arNode : ar) {
                arStr.append(arNode.get("name"))
                        .append(" ");
            }
            int arIndex = arStr.lastIndexOf(" ");
            arStr.replace(arIndex, arIndex + 1, "");

            Music music = new Music();
            music.setName(musicInfo.get("name").asText());
            music.setId(musicId);
            music.setArtist(arStr.toString().replace("\"", ""));
            music.setCover(musicInfo.get("al").get("picUrl").asText());
            music.setLrc(requestConfig.getServerUrl() + "api/vueblog/lyric?id=" + musicId);
            music.setUrl(requestConfig.getServerUrl() + "api/vueblog/song/url?id=" + musicId);

            musicList.add(music);
        }

        valueOperations.set(
                requestConfig.getRedisMusicInfoCachePrefix() + id,
                CommonUtil.OBJECT_MAPPER.writeValueAsString(musicList),
                commonUtil.parseRedisTtlType()
        );
        return musicList;
    }

    @SneakyThrows
    private String commonServiceRequest(String url) {
        String body = Requests.requests.request(
                HttpRequest.build()
                        .setUrl(
                                url + "&cookie=" + requestConfig.getCookie()
                        )
                        .setMethod(Requests.REQUEST_METHOD.POST)
        ).getBody();
        JsonNode root = CommonUtil.OBJECT_MAPPER.readTree(body);
        if (root.get("code").asInt() == 20001) {
            emailLogin(requestConfig.getEmail(), requestConfig.getPassWord());
            return commonServiceRequest(url);
        }
        return body;
    }
}
