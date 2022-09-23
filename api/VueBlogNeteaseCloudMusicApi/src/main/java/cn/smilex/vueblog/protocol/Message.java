package cn.smilex.vueblog.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author YangLuJia
 * @date 2022/9/23 16:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Map<String, Object> content;
}
