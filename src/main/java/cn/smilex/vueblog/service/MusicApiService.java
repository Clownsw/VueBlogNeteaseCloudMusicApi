package cn.smilex.vueblog.service;

import cn.smilex.vueblog.model.Music;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author smilex
 * @date 2022/9/11/18:15
 * @since 1.0
 */
public interface MusicApiService {
    String search(String keyWords);

    void emailLogin(String email, String passWord);

    String playListDetail(String id) throws JsonProcessingException;

    ConcurrentLinkedQueue<Music> vueBlogMusicList(String id, String level, Integer limit, Integer offset) throws Exception;

    String lyric(String id) throws JsonProcessingException;

    String newSongUrl(String id, String level) throws JsonProcessingException;

    String playListTrackAll(String id, String level, Integer limit, Integer offset) throws JsonProcessingException;

    String vueBlogLyric(String id) throws JsonProcessingException;

    String vueBlogSongUrl(String id, String level) throws JsonProcessingException;
}
