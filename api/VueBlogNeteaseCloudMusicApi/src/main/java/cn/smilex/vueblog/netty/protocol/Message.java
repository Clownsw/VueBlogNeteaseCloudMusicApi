package cn.smilex.vueblog.netty.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author smilex
 * @date 2022/9/23 16:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Integer actionType;
    private Map<String, Object> content;
}
