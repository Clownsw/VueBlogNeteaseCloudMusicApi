package cn.smilex.vueblog.pojo;

import cn.smilex.vueblog.util.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author smilex
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResult {
    private static final UploadResult EMPTY_UPLOAD_RESULT = new UploadResult(
            CommonUtil.EMPTY_STRING,
            false
    );

    private String message;
    private boolean isError;

    public static UploadResult empty() {
        return EMPTY_UPLOAD_RESULT;
    }

    public static UploadResult error(String errorMsg) {
        return new UploadResult(errorMsg, true);
    }
}
