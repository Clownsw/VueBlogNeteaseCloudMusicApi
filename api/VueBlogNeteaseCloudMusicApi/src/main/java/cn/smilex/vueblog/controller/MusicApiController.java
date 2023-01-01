package cn.smilex.vueblog.controller;

import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.service.MusicApiService;
import cn.smilex.vueblog.util.CommonUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * @author smilex
 * @date 2022/9/11/18:13
 * @since 1.0
 */
@CrossOrigin
@RequestMapping("/api")
@RestController
public class MusicApiController {

    private RequestConfig requestConfig;
    private MusicApiService musicApiService;

    @Autowired
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    @Autowired
    public void setMusicApiService(MusicApiService musicApiService) {
        this.musicApiService = musicApiService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyWords", required = false, defaultValue = "海阔天空") String keyWords) {
        return musicApiService.search(keyWords);
    }

    @GetMapping("/playlist/detail")
    public String playListDetail(@RequestParam(value = "id") String id) {
        return musicApiService.playListDetail(id);
    }

    @GetMapping("/playlist/track/all")
    public String playListTrackAll(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "offset", required = false, defaultValue = "1") Integer offset
    ) {
        return musicApiService.playListTrackAll(id, level == null ? requestConfig.getDefaultMusicLevel() : level, limit, offset);
    }

    @GetMapping("/lyric")
    public String lyric(@RequestParam(value = "id") String id) {
        return musicApiService.lyric(id);
    }

    @GetMapping("/song/url/v1")
    public String newSongUrl(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "level", required = false) String level
    ) {
        return musicApiService.newSongUrl(id, level == null ? requestConfig.getDefaultMusicLevel() : level);
    }

    @GetMapping("/song/detail")
    public String songDetail(@RequestParam(value = "id") String id) {
        return musicApiService.songDetail(id);
    }

    @GetMapping("/vueblog/playlist/detail")
    public String vueBlogMusicList(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset
    ) throws Exception {
        return CommonUtil.OBJECT_MAPPER.writeValueAsString(
                musicApiService.vueBlogMusicList(
                        id,
                        level == null ? requestConfig.getDefaultMusicLevel() : level,
                        limit,
                        offset
                )
        );
    }

    @GetMapping("/vueblog/lyric")
    public String vueBlogLyric(@RequestParam(value = "id") String id) {
        return musicApiService.vueBlogLyric(id);
    }

    @SneakyThrows
    @GetMapping("/vueblog/song/url")
    public ModelAndView vueBlogSongUrl(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "level", required = false) String level
    ) {
        final String finalLevel = level == null ? requestConfig.getDefaultMusicLevel() : level;
        String result = musicApiService.vueBlogSongUrl(
                id,
                finalLevel,
                true
        );

        return new ModelAndView(
                "redirect:" + (result == null ? requestConfig.getServerUrl() + "api/vueblog/play/song?id=" + id + "&level=" + finalLevel : result)
        );
    }

    @GetMapping("/vueblog/play/song")
    public void vueBlogPlaySong(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "level", required = false) String level,
            HttpServletResponse response
    ) {
        musicApiService.vueBlogPlaySong(id, level, response);
    }

    @GetMapping("/kuwo/search")
    public String kuWoSearch(
            @RequestParam(value = "key", required = false, defaultValue = "Why") String key,
            @RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn,
            @RequestParam(value = "fn", required = false, defaultValue = "30") Integer fn
    ) {
        return musicApiService.kuWoSearch(key, pn, fn);
    }

    @GetMapping("/kuwo/song/url")
    public String kuWoSongUrl(@RequestParam(value = "id") String id) {
        return musicApiService.kuWoSongUrl(id);
    }
}
