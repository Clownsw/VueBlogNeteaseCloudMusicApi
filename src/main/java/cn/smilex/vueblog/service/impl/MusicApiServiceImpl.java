package cn.smilex.vueblog.service.impl;

import cn.smilex.req.Cookie;
import cn.smilex.req.HttpRequest;
import cn.smilex.req.HttpResponse;
import cn.smilex.req.Requests;
import cn.smilex.vueblog.config.RedisTtlType;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.model.Music;
import cn.smilex.vueblog.service.MusicApiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author smilex
 * @date 2022/9/11/18:15
 * @since 1.0
 */
@Slf4j
@Service
public class MusicApiServiceImpl implements MusicApiService {

    private RequestConfig requestConfig;

    private RedisTemplate<String, String> redisTemplate;

    private static final HashMap<String, String> DEFAULT_REQUEST_HEADER = new HashMap<>();
    private static final HashMap<String, String> KUWO_REQUEST_HEADER = new HashMap<>();

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
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String search(String keyWords) {
        return Requests.requests.fast_get(
                requestConfig.getUrl() +
                        "search" +
                        "?keywords=" + keyWords
        );
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

    @SneakyThrows
    @Override
    public String lyric(String id) {
        return commonServiceRequest(
                requestConfig.getUrl() +
                        "lyric" +
                        "?id=" + id
        );
    }

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

    @SneakyThrows
    @Override
    public String vueBlogLyric(String id) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String cacheValue = valueOperations.get(requestConfig.getRedisLyricCachePrefix() + id);
        if (cacheValue != null) return cacheValue;

        String lyricJson = lyric(id);
        if (lyricJson == null) throw new RuntimeException("not found!");

        JsonNode root = new ObjectMapper().readTree(lyricJson);
        JsonNode lrc = root.get("lrc");
        String lyric = lrc.get("lyric").asText();
        valueOperations.set(requestConfig.getRedisLyricCachePrefix() + id, lyric, Duration.ofDays(7));
        return lyric;
    }

    @SneakyThrows
    @Override
    public String vueBlogSongUrl(String id, String level) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String cacheValue = valueOperations.get(requestConfig.getRedisMusicUrlCachePrefix() + id);
        if (cacheValue != null) return cacheValue;

        String url;
        String songJson = newSongUrl(id, level);
        JsonNode root = new ObjectMapper().readTree(songJson);
        JsonNode temp = root.get("data")
                .get(0);
        if (!temp.get("freeTrialInfo").isNull()) {
            String kuWoSearchJson = kuWoSearch(
                    new ObjectMapper()
                            .readTree(songDetail(id))
                            .get("songs")
                            .get(0)
                            .get("name")
                            .asText(),
                    1,
                    1
            );
            JsonNode kuWoSearchJsonRoot = new ObjectMapper().readTree(kuWoSearchJson);
            String kuWoMusicId = kuWoSearchJsonRoot.get("data")
                    .get("list")
                    .get(0)
                    .get("musicrid")
                    .asText();
            url = kuWoSongUrl(kuWoMusicId.replace("MUSIC_", ""));
        } else {
            url = temp.get("url").asText();
        }

        valueOperations.set(requestConfig.getRedisMusicUrlCachePrefix() + id, url, Duration.ofMinutes(3));
        return url;
    }

    @Override
    public String kuWoSongUrl(String id) {
        HttpRequest request = HttpRequest.build()
                .setUrl("https://peng3.com/vip/kuwo/")
                .setHeaders(DEFAULT_REQUEST_HEADER)
                .setMethod(Requests.REQUEST_METHOD.POST)
                .setBody("id=" + id + "&class=mp3");
        HttpResponse response = Requests.requests.request(request);
        String body = response.getBody();
        if (body.contains("解析成功")) {
            Pattern compile = Pattern.compile("\\bhttp://(.*?)\\.mp3\\b");
            Matcher matcher = compile.matcher(body);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        throw new RuntimeException("not found!");
    }

    @SneakyThrows
    @Override
    public String kuWoSearch(String key, Integer pn, Integer fn) {
        String body = Requests.requests.request(
                HttpRequest.build()
                        .setUrl(String.format(KUWO_SEARCH_API, URLEncoder.encode(key, StandardCharsets.UTF_8), pn, fn))
                        .setMethod(Requests.REQUEST_METHOD.GET)
                        .setHeaders(KUWO_REQUEST_HEADER)
        ).getBody();
        JsonNode root = new ObjectMapper().readTree(body);
        JsonNode success = root.get("success");
        if (success != null && !success.asBoolean()) {
            return kuWoSearch(key, pn, fn);
        }
        return body;
    }

    @Override
    public String songDetail(String id) {
        return commonServiceRequest(
                requestConfig.getUrl() +
                        "song/detail" +
                        "?ids=" + id
        );
    }

    @SneakyThrows
    @Override
    public ConcurrentLinkedQueue<Music> vueBlogMusicList(String id, String level, Integer limit, Integer offset) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String cacheValue = valueOperations.get(requestConfig.getRedisMusicInfoCachePrefix() + id);
        if (cacheValue != null) {
            return new ObjectMapper().readValue(cacheValue, new TypeReference<>() {
            });
        }

        JsonNode root = new ObjectMapper().readTree(playListTrackAll(id, level, limit, offset));
        if (root.get("code").asInt() != 200) throw new RuntimeException("获取歌单列表失败!");

        JsonNode songs = root.get("songs");
        if (songs.size() == 0) return new ConcurrentLinkedQueue<>();

        var musicList = new ConcurrentLinkedQueue<Music>();

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
                new ObjectMapper().writeValueAsString(musicList),
                parseRedisTtlType()
        );
        return musicList;
    }

    private Duration parseRedisTtlType() {
        RedisTtlType redisTtlType = requestConfig.getRedisTtlType();
        Duration ttl;

        switch (redisTtlType) {
            case NANOS: {
                ttl = Duration.ofNanos(requestConfig.getRedisTtl());
                break;
            }

            case MILLLIS: {
                ttl = Duration.ofMillis(requestConfig.getRedisTtl());
                break;
            }

            case MINUTES: {
                ttl = Duration.ofMinutes(requestConfig.getRedisTtl());
                break;
            }

            case SECONDS: {
                ttl = Duration.ofSeconds(requestConfig.getRedisTtl());
                break;
            }

            case HOURS: {
                ttl = Duration.ofHours(requestConfig.getRedisTtl());
                break;
            }

            case DAYS: {
                ttl = Duration.ofDays(requestConfig.getRedisTtl());
                break;
            }

            default: {
                throw new RuntimeException("unknown redis ttl type");
            }
        }

        return ttl;
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
        JsonNode root = new ObjectMapper().readTree(body);
        if (root.get("code").asInt() == 20001) {
            emailLogin(requestConfig.getEmail(), requestConfig.getPassWord());
            return commonServiceRequest(url);
        }
        return body;
    }
}
