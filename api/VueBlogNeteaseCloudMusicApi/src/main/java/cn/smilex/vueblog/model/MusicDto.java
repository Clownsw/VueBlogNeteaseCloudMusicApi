package cn.smilex.vueblog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author smilex
 * @date 2022/9/19/21:05
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicDto {
    private Long id;
    private String musicName;
    private String kuWoId;
}
