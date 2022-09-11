package cn.smilex.vueblog.controller;

import cn.smilex.vueblog.service.MusicApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * author smilex
 *
 * @date 2022/9/11/18:13
 * @since 1.0
 */
@RequestMapping("/api")
@RestController
public class MusicApiController {
    private MusicApiService musicApiService;

    @Autowired
    public void setMusicApiService(MusicApiService musicApiService) {
        this.musicApiService = musicApiService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyWords", required = false, defaultValue = "海阔天空") String keyWords) {
        return musicApiService.search(keyWords);
    }
}
