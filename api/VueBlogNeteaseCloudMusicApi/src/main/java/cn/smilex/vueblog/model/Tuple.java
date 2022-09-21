package cn.smilex.vueblog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author smilex
 * @date 2022/9/20/21:57
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tuple<T, K> {
    private T left;
    private K right;
}
