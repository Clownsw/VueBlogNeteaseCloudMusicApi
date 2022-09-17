package cn.smilex.vueblog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author smilex
 * @date 2022/9/17/16:30
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<?> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<?> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<?> error(String message) {
        return new Result<>(500, message, null);
    }
}
