package cn.smilex.vueblog.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author smilex
 * @date 2022/9/24/14:36
 * @since 1.0
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createDateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "modifyDateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "modifyDateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
