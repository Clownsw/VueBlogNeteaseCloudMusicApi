package cn.smilex.vueblog.service;

import cn.smilex.vueblog.model.Music;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author smilex
 * @date 2022/9/11/18:15
 * @since 1.0
 */
public interface MusicApiService {
    String search(String keyWords);

    void emailLogin(String email, String passWord);

    String playListDetail(String id);

    ConcurrentLinkedQueue<Music> vueBlogMusicList(String id, String level, Integer limit, Integer offset);

    String lyric(String id);

    String newSongUrl(String id, String level);

    String playListTrackAll(String id, String level, Integer limit, Integer offset);

    String vueBlogLyric(String id);

    String vueBlogSongUrl(String id, String level);

    String kuWoSongUrl(String id);

    String kuWoSearch(String key, Integer pn, Integer fn);

    String songDetail(String id);
}
