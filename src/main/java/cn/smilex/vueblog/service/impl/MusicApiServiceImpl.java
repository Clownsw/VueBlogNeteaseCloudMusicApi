package cn.smilex.vueblog.service.impl;

import cn.smilex.req.Requests;
import cn.smilex.vueblog.config.RequestConfig;
import cn.smilex.vueblog.service.MusicApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author smilex
 *
 * @date 2022/9/11/18:15
 * @since 1.0
 */
@Service
public class MusicApiServiceImpl implements MusicApiService {
    private RequestConfig requestConfig;

    @Autowired
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    @Override
    public String search(String keyWords) {
        return Requests.requests.fast_get(
                requestConfig.getUrl() +
                        "search" +
                        "?keywords=" +
                        keyWords
        );
    }
}
