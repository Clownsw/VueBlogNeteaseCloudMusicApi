package cn.smilex.vueblog.service;

import cn.smilex.vueblog.model.Music;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author smilex
 * @date 2022/9/11/18:15
 * @since 1.0
 */
public interface MusicApiService {
    /**
     * 网易云音乐搜索
     *
     * @param keyWords 关键字
     * @return json
     */
    String search(String keyWords);

    /**
     * 网易云音乐邮箱登录
     *
     * @param email    邮箱
     * @param passWord 密码
     */
    void emailLogin(String email, String passWord);

    /**
     * 网易云歌单详情
     *
     * @param id 歌单ID
     * @return json
     */
    String playListDetail(String id);

    /**
     * VueBlog格式网易云歌单列表
     *
     * @param id     歌单ID
     * @param level  音质级别
     * @param limit  分页
     * @param offset 起始
     * @return 列表
     */
    ConcurrentLinkedQueue<Music> vueBlogMusicList(String id, String level, Integer limit, Integer offset);

    /**
     * 网易云音乐歌词获取
     *
     * @param id 歌曲ID
     * @return json
     */
    String lyric(String id);

    /**
     * 新版网易云音乐真链获取
     *
     * @param id    音乐ID
     * @param level 音质级别
     * @return json
     */
    String newSongUrl(String id, String level);

    /**
     * unknown
     *
     * @param id     音乐ID
     * @param level  音质级别
     * @param limit  分页
     * @param offset 起始
     * @return json
     */
    String playListTrackAll(String id, String level, Integer limit, Integer offset);

    /**
     * VueBlog网易云音乐歌词获取
     *
     * @param id 音乐ID
     * @return json
     */
    String vueBlogLyric(String id);

    /**
     * VueBlog网易云音乐真链获取
     *
     * @param id    音乐ID
     * @param level 音质级别
     * @return json
     */
    String vueBlogSongUrl(String id, String level, boolean isPlay);

    /**
     * 酷我音乐真链获取
     *
     * @param id 音乐ID
     * @return json
     */
    String kuWoSongUrl(String id);

    /**
     * 酷我音乐搜索
     *
     * @param key 关键字
     * @param pn  unknown
     * @param fn  unknown
     * @return json
     */
    String kuWoSearch(String key, Integer pn, Integer fn);

    /**
     * 网易云音乐详情
     *
     * @param id 音乐ID
     * @return json
     */
    String songDetail(String id);

    void vueBlogPlaySong(String id, String level, HttpServletResponse response);
}
