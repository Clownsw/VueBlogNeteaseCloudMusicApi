package cn.smilex.vueblog.service;

import cn.smilex.vueblog.pojo.UploadResult;

/**
 * @author smilex
 */
public interface RemoteService {

    /**
     * 上传文件
     *
     * @param filePath 文件路径
     * @param data     数据
     * @throws Exception unknown exception
     */
    UploadResult uploadFile(String filePath, byte[] data) throws Exception;

    /**
     * 文件是否存在
     *
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean existsFile(String filePath);
}
