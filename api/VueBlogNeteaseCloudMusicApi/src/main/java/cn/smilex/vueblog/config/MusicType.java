package cn.smilex.vueblog.config;

import lombok.Getter;

/**
 * @author smilex
 * @date 2022/9/24/15:28
 * @since 1.0
 */
@Getter
public enum MusicType {
    WYY((short) 1),
    KUWO((short) 2);

    final Short type;

    MusicType(Short type) {
        this.type = type;
    }
}
