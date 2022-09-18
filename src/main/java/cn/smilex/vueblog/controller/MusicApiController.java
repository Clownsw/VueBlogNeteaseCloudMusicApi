package cn.smilex.vueblog.controller;

import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.service.MusicApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * author smilex
 *
 * @date 2022/9/11/18:13
 * @since 1.0
 */
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
    public String playListDetail(@RequestParam(value = "id") String id) throws JsonProcessingException {
        return musicApiService.playListDetail(id);
    }

    @GetMapping("/playlist/track/all")
    public String playListTrackAll(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "offset", required = false, defaultValue = "1") Integer offset
    ) throws JsonProcessingException {
        return musicApiService.playListTrackAll(id, level == null ? requestConfig.getDefaultMusicLevel() : level, limit, offset);
    }

    @GetMapping("/lyric")
    public String lyric(@RequestParam(value = "id") String id) throws JsonProcessingException {
        return musicApiService.lyric(id);
    }

    @GetMapping("/song/url/v1")
    public String newSongUrl(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "level", required = false) String level
    ) throws JsonProcessingException {
        return musicApiService.newSongUrl(id, level == null ? requestConfig.getDefaultMusicLevel() : level);
    }

    @GetMapping("/vueblog/playlist/detail")
    public String vueBlogMusicList(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset
    ) throws Exception {
        return new ObjectMapper().writeValueAsString(
                musicApiService.vueBlogMusicList(
                        id,
                        level == null ? requestConfig.getDefaultMusicLevel() : level,
                        limit,
                        offset
                )
        );
    }

    @GetMapping("/vueblog/lyric")
    public String vueBlogLyric(@RequestParam(value = "id") String id) throws JsonProcessingException {
        return musicApiService.vueBlogLyric(id);
    }

    @GetMapping("/vueblog/song/url")
    public ModelAndView vueBlogSongUrl(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "level", required = false) String level
    ) throws JsonProcessingException {
        return new ModelAndView(
                "redirect:" + musicApiService.vueBlogSongUrl(
                        id,
                        level == null ? requestConfig.getDefaultMusicLevel() : level
                )
        );
    }
}
