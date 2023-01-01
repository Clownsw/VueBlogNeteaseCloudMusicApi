package cn.smilex.vueblog.dao;

import cn.smilex.vueblog.pojo.Music;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author smilex
 * @date 2022/9/24/14:24
 * @since 1.0
 */
@org.apache.ibatis.annotations.Mapper
public interface MusicDao extends BaseMapper<Music> {

    /**
     * 查询 `music_url` 为空的音乐集合
     *
     * @return 音乐集合
     */
    @Select("SELECT * FROM music WHERE ISNULL(music_url) OR music_url = ''")
    List<Music> selectMusicUrlNullList();
}
