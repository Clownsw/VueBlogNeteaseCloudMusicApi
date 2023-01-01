package cn.smilex.vueblog.service.impl;

import cn.smilex.vueblog.config.MusicType;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.dao.MusicDao;
import cn.smilex.vueblog.pojo.Music;
import cn.smilex.vueblog.service.MusicService;
import cn.smilex.vueblog.util.CommonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author smilex
 * @date 2022/9/24/14:27
 * @since 1.0
 */
@Service
public class MusicServiceImpl extends ServiceImpl<MusicDao, Music> implements MusicService {
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

    /**
     * 根据音乐ID查询是否为免费歌曲
     *
     * @param musicId 音乐ID
     * @return 是否为免费歌曲
     */
    @Override
    public Boolean getMusicNotFreeFiledByMusicId(Long musicId) {
        QueryWrapper<Music> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("music_type", MusicType.WYY.getType())
                .eq("music_id", musicId)
                .select("not_free");

        Music music = getOne(queryWrapper);
        if (music == null) {
            return null;
        }

        return music.getNotFree();
    }

    /**
     * 通过音乐ID获取数量
     *
     * @param musicId 音乐ID
     * @return 数量
     */
    @Override
    public long getMusicCountByMusicId(Long musicId) {
        return count(
                new QueryWrapper<Music>()
                        .eq("music_id", musicId)
        );
    }

    /**
     * 通过音乐ID获取URL
     *
     * @param musicId 音乐ID
     * @return URL
     */
    @Override
    public String getMusicUrlByMusicId(Long musicId) {
        Music music = getOne(
                new QueryWrapper<Music>()
                        .eq("music_id", musicId)
        );
        return music == null ? null : music.getMusicUrl();
    }

    /**
     * 将指定音乐是否免费缓存到Redis中
     *
     * @param musicId 音乐ID
     * @param status  是否免费
     */
    @Override
    public void cacheMusicNotFreeInRedis(String musicId, Boolean status) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(requestConfig.getRedisNetEaseCloudStatusCache() + musicId, status.toString());
    }

    /**
     * 将指定音乐是否免费缓存到MySql中
     *
     * @param musicType 音乐类型
     * @param musicId   音乐ID
     * @param status    是否免费
     * @return 是否成功
     */
    @Override
    public boolean cacheMusicNotFreeInMySql(MusicType musicType, String musicId, Boolean status) {
        Long musicIdTmp = Long.parseLong(musicId);

        if (getMusicCountByMusicId(musicIdTmp) > 0) {
            return true;
        }

        return this.baseMapper.insert(
                new Music(
                        musicType.getType(),
                        musicIdTmp,
                        CommonUtil.EMPTY_STRING,
                        CommonUtil.EMPTY_STRING,
                        status
                )
        ) > 0;
    }

    /**
     * 将指定音乐是否免费缓存到Redis和MySql中
     *
     * @param musicType 音乐类型
     * @param musicId   音乐ID
     * @param status    是否免费
     * @return 是否成功
     */
    @Override
    public boolean cacheMusicNotFreeInAll(MusicType musicType, String musicId, Boolean status) {
        cacheMusicNotFreeInRedis(musicId, status);
        return cacheMusicNotFreeInMySql(musicType, musicId, status);
    }

    /**
     * 查询 `music_url` 为空的音乐集合
     *
     * @return 音乐集合
     */
    @Override
    public List<Music> selectMusicUrlNullList() {
        return this.getBaseMapper()
                .selectMusicUrlNullList();
    }
}
