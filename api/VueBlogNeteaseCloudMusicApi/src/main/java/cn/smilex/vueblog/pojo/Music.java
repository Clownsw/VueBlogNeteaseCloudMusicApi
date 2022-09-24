package cn.smilex.vueblog.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author smilex
 * @date 2022/9/24/14:25
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Music {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("music_type")
    private Short musicType;

    @TableField("music_id")
    private Long musicId;

    @TableField("music_name")
    private String musicName;

    @TableField("music_url")
    private String musicUrl;

    @TableField("not_free")
    private Boolean notFree;

    @TableField(value = "create_date_time", fill = FieldFill.INSERT)
    private LocalDateTime createDateTime;

    @TableField(value = "modify_date_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifyDateTime;

    public Music(Short musicType, Long musicId, String musicUrl) {
        this.musicType = musicType;
        this.musicId = musicId;
        this.musicUrl = musicUrl;
    }

    public Music(Short musicType, Long musicId, String musicName, String musicUrl, Boolean notFree) {
        this.musicType = musicType;
        this.musicId = musicId;
        this.musicName = musicName;
        this.musicUrl = musicUrl;
        this.notFree = notFree;
    }
}
