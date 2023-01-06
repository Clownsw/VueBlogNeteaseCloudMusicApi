package cn.smilex.vueblog.schedule;

import cn.smilex.vueblog.netty.NettyClient;
import cn.smilex.vueblog.pojo.Music;
import cn.smilex.vueblog.service.MusicApiService;
import cn.smilex.vueblog.service.MusicService;
import cn.smilex.vueblog.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author smilex
 */
@Component
public class MusicInfoSynchronized {
    private NettyClient nettyClient;
    private MusicService musicService;
    private MusicApiService musicApiService;

    @Autowired
    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Autowired
    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }

    @Autowired
    public void setMusicApiService(MusicApiService musicApiService) {
        this.musicApiService = musicApiService;
    }

    /**
     * 同步数据库中的音乐url
     */
    @Scheduled(cron = "0 0/2 * * * ?")
    public void synchronizedDataBaseMusicUrl() {
        List<Music> musicList = musicService.selectMusicUrlNullList();
        if (!musicList.isEmpty()) {
            musicList.forEach(music -> {
                final String url;
                try {
                    url = musicApiService.vueBlogSongUrl(music.getId().toString(), "", false);
                } catch (Exception ignore) {
                    return;
                }
                MessageUtil.sendMessageToServer(
                        nettyClient.getChannel(),
                        url,
                        String.valueOf(music.getId()),
                        music.getMusicType() == 0 ? "/wyy/" : "/kuwo/"
                );
            });
        }
    }
}
