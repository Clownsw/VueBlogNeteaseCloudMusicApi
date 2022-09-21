package cn.smilex.vueblog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author smilex
 * @date 2022/9/11/19:01
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Music {
    private Long id;
    private String name;
    private String artist;
    private String url;
    private String cover;
    private String lrc;
}
