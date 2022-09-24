package cn.smilex.vueblog.service.impl;

import cn.smilex.vueblog.dao.MusicDao;
import cn.smilex.vueblog.pojo.Music;
import cn.smilex.vueblog.service.MusicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author smilex
 * @date 2022/9/24/14:27
 * @since 1.0
 */
@Service
public class MusicServiceImpl extends ServiceImpl<MusicDao, Music> implements MusicService {
}
