package cn.smilex.vueblog.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author smilex
 * @date 2022/9/23/22:57
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonConfig {
    private String bucketName;
    private String userName;
    private String password;
    private String url;
    private Integer uploadTimeOut;
}
