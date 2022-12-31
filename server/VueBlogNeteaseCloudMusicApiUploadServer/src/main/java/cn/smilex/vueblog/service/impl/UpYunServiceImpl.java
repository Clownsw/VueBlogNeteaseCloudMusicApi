package cn.smilex.vueblog.service.impl;

import cn.smilex.vueblog.Application;
import cn.smilex.vueblog.pojo.UploadResult;
import cn.smilex.vueblog.service.RemoteService;
import cn.smilex.vueblog.util.CommonUtil;
import cn.smilex.vueblog.util.impl.HashMapBuilder;
import com.upyun.RestManager;
import com.upyun.UpYunUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

/**
 * @author smilex
 */
@Slf4j
public class UpYunServiceImpl implements RemoteService {

    /**
     * 上传文件
     *
     * @param filePath 文件路径
     * @param data     数据
     * @throws Exception unknown exception
     */
    @Override
    public UploadResult uploadFile(String filePath, byte[] data) throws Exception {
        Response response = Application.REST_MANAGER
                .writeFile(
                        filePath,
                        data,
                        new HashMapBuilder<String, String>(1)
                                .put(RestManager.PARAMS.CONTENT_MD5.getValue(), UpYunUtils.md5(data))
                                .getMap()
                );
        if (response.isSuccessful()) {
            return UploadResult.empty();
        } else {
            String errorMsg = CommonUtil.OBJECT_MAPPER.readTree(
                    response.body().string()
            ).get("msg")
                    .asText();
            return UploadResult.error(errorMsg);
        }
    }

    /**
     * 文件是否存在
     *
     * @param filePath 文件路径
     * @return 是否存在
     */
    @Override
    public boolean existsFile(String filePath) {
        try {
            return Application.REST_MANAGER.getFileInfo(filePath).code() == 200;
        } catch (Exception ignore) {
        }
        return false;
    }
}
