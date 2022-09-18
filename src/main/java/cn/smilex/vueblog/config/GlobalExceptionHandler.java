package cn.smilex.vueblog.config;

import cn.smilex.vueblog.model.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

/**
 * @author smilex
 * @date 2022/9/11/18:53
 * @since 1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @SneakyThrows
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public String error(Exception e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return new ObjectMapper().writeValueAsString(Result.error("unknown error"));
    }

    @SneakyThrows
    @ResponseBody
    @ExceptionHandler(JsonProcessingException.class)
    public String error(JsonProcessingException e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return new ObjectMapper().writeValueAsString(Result.error("system inner error"));
    }
}
