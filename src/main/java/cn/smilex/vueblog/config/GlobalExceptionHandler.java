package cn.smilex.vueblog.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

/**
 * author smilex
 *
 * @date 2022/9/11/18:53
 * @since 1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public String error(Exception e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return null;
    }

    @ExceptionHandler(JsonProcessingException.class)
    public String error(JsonProcessingException e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return null;
    }
}
