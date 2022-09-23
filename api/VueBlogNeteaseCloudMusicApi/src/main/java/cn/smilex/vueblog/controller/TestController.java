package cn.smilex.vueblog.controller;

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

    @Autowired
    public void setUploadServerService(UploadServerService uploadServerService) {
        this.uploadServerService = uploadServerService;
    }

    @SneakyThrows
    @GetMapping("/test")
    public String sendUrlTest(@RequestParam("url") String url) {
        var content = new HashMap<String, Object>(1);
        content.put("url", url);
        var message = MessageUtil.buildMessage(1, content);
        uploadServerService.sendMessage(message);

        return "test2";
    }
}
