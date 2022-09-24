package cn.smilex.vueblog.controller;

import cn.smilex.vueblog.service.MusicService;
import cn.smilex.vueblog.service.UploadServerService;
import cn.smilex.vueblog.util.MessageUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author smilex
 * @date 2022/9/23 16:09
 */
@RestController
public class TestController {
    private UploadServerService uploadServerService;
    private MusicService musicService;

    @Autowired
    public void setUploadServerService(UploadServerService uploadServerService) {
        this.uploadServerService = uploadServerService;
    }

    @Autowired
    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }

    @SneakyThrows
    @GetMapping("/test")
    public String sendUrlTest(@RequestParam("url") String url) {
        var index = url.lastIndexOf("/") + 1;
        if (index != url.length()) {
            var fileName = url.substring(index);
            var filePath = "/wyy/" + fileName;
            System.out.println(fileName);
            var content = new HashMap<String, Object>(2);
            content.put("url", url);
            content.put("filePath", filePath);
            var message = MessageUtil.buildMessage(1, content);
            uploadServerService.sendMessage(message);
        }

        return "test2";
    }
}
