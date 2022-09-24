package cn.smilex.vueblog.service;

import cn.smilex.vueblog.config.MusicType;
import cn.smilex.vueblog.pojo.Music;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author smilex
 * @date 2022/9/24/14:26
 * @since 1.0
 */
public interface MusicService extends IService<Music> {
    /**
     * 根据音乐ID查询是否为免费歌曲
     *
     * @param musicId 音乐ID
     * @return 是否为免费歌曲
     */
    Boolean getMusicNotFreeFiledByMusicId(Long musicId);

    long getMusicCountByMusicId(Long musicId);

    /**
     * 将指定音乐是否免费缓存到Redis中
     *
     * @param musicId 音乐ID
     * @param status  是否免费
     */
    void cacheMusicNotFreeInRedis(String musicId, Boolean status);

    /**
     * 将指定音乐是否免费缓存到MySql中
     *
     * @param musicType 音乐类型
     * @param musicId   音乐ID
     * @param status    是否免费
     * @return 是否成功
     */
    boolean cacheMusicNotFreeInMySql(MusicType musicType, String musicId, Boolean status);

    /**
     * 将指定音乐是否免费缓存到Redis和MySql中
     *
     * @param musicType 音乐类型
     * @param musicId   音乐ID
     * @param status    是否免费
     */
    boolean cacheMusicNotFreeInAll(MusicType musicType, String musicId, Boolean status);
}
