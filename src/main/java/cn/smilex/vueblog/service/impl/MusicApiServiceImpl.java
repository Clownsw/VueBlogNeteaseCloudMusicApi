package cn.smilex.vueblog.service.impl;

import cn.smilex.req.Cookie;
import cn.smilex.req.HttpRequest;
import cn.smilex.req.HttpResponse;
import cn.smilex.req.Requests;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.model.Music;
import cn.smilex.vueblog.service.MusicApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.smilex.vueblog.config.RedisTtlType.*;

/**
 * @author smilex
 * @date 2022/9/11/18:15
 * @since 1.0
 */
@Slf4j
@Service
public class MusicApiServiceImpl implements MusicApiService {

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(2);
    private RequestConfig requestConfig;

    private RedisTemplate<String, String> redisTemplate;

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

    @Override
    public String playListDetail(String id) throws JsonProcessingException {
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

    @Override
    public String lyric(String id) throws JsonProcessingException {
        return commonServiceRequest(
                requestConfig.getUrl() +
                        "lyric" +
                        "?id=" + id
        );
    }

    @Override
    public String newSongUrl(String id, String level) throws JsonProcessingException {
        return commonServiceRequest(
                requestConfig.getUrl() +
                        "song/url/v1" +
                        "?id=" + id +
                        "&level=" + level
        );
    }

    @Override
    public String playListTrackAll(String id, String level, Integer limit, Integer offset) throws JsonProcessingException {
        return commonServiceRequest(
                requestConfig.getUrl() +
                        "playlist/track/all" +
                        "?id=" + id +
                        "&limit=" + limit +
                        "&offset=" + offset
        );
    }

    @Override
    public String vueBlogLyric(String id) throws JsonProcessingException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String cacheValue = valueOperations.get(requestConfig.getRedisLyricCachePrefix() + id);
        if (cacheValue != null) return cacheValue;

        String lyricJson = lyric(id);
        if (lyricJson == null) throw new NullPointerException("");

        JsonNode root = new ObjectMapper().readTree(lyricJson);
        JsonNode lrc = root.get("lrc");
        String lyric = lrc.get("lyric").asText();
        valueOperations.set(requestConfig.getRedisLyricCachePrefix() + id, lyric, Duration.ofDays(7));
        return lyric;
    }

    @Override
    public ConcurrentLinkedQueue<Music> vueBlogMusicList(String id, String level, Integer limit, Integer offset) throws Exception {
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
        var idParamList = new ConcurrentLinkedQueue<String>();
        var runnableList = new LinkedList<Callable<Object>>();

        StringBuilder paramsString = new StringBuilder();

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

            musicList.add(music);

            paramsString.append(musicId)
                    .append(",");
            if ((i + 1) % requestConfig.getSplitCount() == 0 || (i + 1) == songs.size()) {
                int index = paramsString.lastIndexOf(",");
                if (index != -1) paramsString.replace(index, index + 1, "");
                idParamList.add(paramsString.toString());
                paramsString = new StringBuilder();
            }
        }

        for (String idParam : idParamList) {
            runnableList.add(() -> {
                String responseJson = newSongUrl(idParam, level);
                JsonNode idParamRoot = new ObjectMapper().readTree(responseJson);
                JsonNode idParamRootDataList = idParamRoot.get("data");
                for (JsonNode idParamRootData : idParamRootDataList) {
                    Music music = getMusicInListById(idParamRootData.get("id").asLong(), musicList);
                    if (music != null) {
                        music.setUrl(idParamRootData.get("url").asText());
                    }
                }
                return null;
            });
        }
        THREAD_POOL.invokeAll(runnableList);

        valueOperations.set(
                requestConfig.getRedisMusicInfoCachePrefix() + id,
                new ObjectMapper().writeValueAsString(musicList),
                parseRedisTtlType()
        );
        return musicList;
    }

    private Duration parseRedisTtlType() {
        String redisTtlType = requestConfig.getRedisTtlType();
        if (redisTtlType == null || redisTtlType.isBlank()) throw new NullPointerException();

        Duration ttl;

        switch (redisTtlType.toLowerCase()) {
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
                throw new NullPointerException();
            }
        }

        return ttl;
    }

    private Music getMusicInListById(Long id, Collection<Music> list) {
        for (Music music : list) {
            if (music.getId().equals(id)) return music;
        }
        return null;
    }

    private String commonServiceRequest(String url) throws JsonProcessingException {
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
