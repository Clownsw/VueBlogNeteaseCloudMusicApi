package cn.smilex.vueblog.util;

import cn.smilex.vueblog.Application;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

/**
 * @author smilex
 * @date 2022/9/25/14:01
 * @since 1.0
 */
@Slf4j
public final class BucketUtil {
    public static boolean getFileExists(String filePath) {
        boolean result = false;
        try {
            Response response = Application.REST_MANAGER
                    .getFileInfo(filePath);
            if (response.isSuccessful() && response.header("x-upyun-file-size") != null) {
                result = true;
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return result;
    }
}
