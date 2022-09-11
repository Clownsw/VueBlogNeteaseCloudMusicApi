package cn.smilex.vueblog.service.impl;

import cn.smilex.req.Cookie;
import cn.smilex.req.HttpRequest;
import cn.smilex.req.HttpResponse;
import cn.smilex.req.Requests;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.model.Music;
import cn.smilex.vueblog.service.MusicApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author smilex
 *
 * @date 2022/9/11/18:15
 * @since 1.0
 */
@Slf4j
@Service
public class MusicApiServiceImpl implements MusicApiService {

    private static final ExecutorService THREAD_POOL = Executors.newVirtualThreadPerTaskExecutor();
    private RequestConfig requestConfig;

    @Autowired
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
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
        String body = Requests.requests.request(
                HttpRequest.build()
                        .setUrl(
                                requestConfig.getUrl() +
                                        "playlist/detail" +
                                        "?id=" + id +
                                        "&cookie=" + requestConfig.getCookie()
                        )
                        .setMethod(Requests.REQUEST_METHOD.POST)
        ).getBody();

        JsonNode root = new ObjectMapper().readTree(body);
        if (root.get("code").asInt() == 20001) {
            emailLogin(requestConfig.getEmail(), requestConfig.getPassWord());
            return playListDetail(id);
        }
        return body;
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
        String body = Requests.requests.request(
                HttpRequest.build()
                        .setUrl(
                                requestConfig.getUrl() +
                                        "lyric" +
                                        "?id=" + id +
                                        "&cookie=" + requestConfig.getCookie()
                        )
                        .setMethod(Requests.REQUEST_METHOD.POST)
        ).getBody();
        JsonNode root = new ObjectMapper().readTree(body);
        if (root.get("code").asInt() == 20001) {
            emailLogin(requestConfig.getEmail(), requestConfig.getPassWord());
            return lyric(id);
        }
        return body;
    }

    @Override
    public String newSongUrl(String id) throws JsonProcessingException {
        String body = Requests.requests.request(
                HttpRequest.build()
                        .setUrl(
                                requestConfig.getUrl() +
                                        "song/url/v1" +
                                        "?id=" + id +
                                        "&level=lossless" +
                                        "&cookie=" + requestConfig.getCookie()
                        )
                        .setMethod(Requests.REQUEST_METHOD.POST)
        ).getBody();
        JsonNode root = new ObjectMapper().readTree(body);
        if (root.get("code").asInt() == 20001) {
            emailLogin(requestConfig.getEmail(), requestConfig.getPassWord());
            return newSongUrl(id);
        }
        return body;
    }

    @Override
    public ConcurrentLinkedQueue<Music> vueBlogMusicList(String id) throws Exception {

        JsonNode root = new ObjectMapper().readTree(playListDetail(id));
        if (root.get("code").asInt() != 200) {
            throw new RuntimeException("获取歌单列表失败!");
        }

        JsonNode tracks = root.get("playlist")
                .get("tracks");
        if (tracks.size() == 0) {
            return new ConcurrentLinkedQueue<>();
        }

        var musicList = new ConcurrentLinkedQueue<Music>();
        var runnableList = new LinkedList<Callable<Object>>();

        for (JsonNode musicInfo : tracks) {
            runnableList.add(() -> {
                try {
                    String musicId = String.valueOf(musicInfo.get("id").asInt());

                    Music music = new Music();
                    music.setName(musicInfo.get("name").asText());

                    StringBuilder arStr = new StringBuilder();
                    JsonNode ar = musicInfo.get("ar");
                    for (JsonNode arNode : ar) {
                        arStr.append(arNode.get("name"))
                                .append(" ");
                    }
                    int index = arStr.lastIndexOf(" ");
                    arStr.replace(index, index + 1, "");
                    music.setArtist(arStr.toString().replace("\"", ""));

                    music.setCover(musicInfo.get("al").get("picUrl").asText());

                    JsonNode urlRoot = new ObjectMapper().readTree(newSongUrl(musicId));
                    music.setUrl(urlRoot.get("data").get(0).get("url").asText());

                    String lyric = lyric(musicId);
                    JsonNode lyricRoot = new ObjectMapper().readTree(lyric);
                    music.setLrc(lyricRoot.get("lrc").get("lyric").asText());

                    musicList.add(music);
                } catch (Exception e) {
                    log.error(Arrays.toString(e.getStackTrace()));
                }
                return null;
            });
        }
        THREAD_POOL.invokeAll(runnableList);

        return musicList;
    }
}
