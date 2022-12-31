package cn.smilex.vueblog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author smilex
 */
@Slf4j
@RestController
public class HttpErrorHandler implements ErrorController {
    private final static String ERROR_PATH = "/error";

    /**
     * Supports the HTML Error View
     *
     * @param request request object
     * @return info
     */
    @RequestMapping(value = ERROR_PATH)
    public String errorHtml(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == 401) {
            return "{ \"code\": \"401\"}";
        } else if (statusCode == 404) {
            return "{ \"code\": \"404\"}";
        } else if (statusCode == 403) {
            return "{ \"code\": \"403\"}";
        } else {
            return "{ \"code\": \"500\"}";
        }
    }
}
