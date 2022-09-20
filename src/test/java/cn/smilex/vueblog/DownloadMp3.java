package cn.smilex.vueblog;

import cn.smilex.req.HttpRequest;
import cn.smilex.req.HttpResponse;
import cn.smilex.req.Requests;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author smilex
 * @date 2022/9/20/21:16
 * @since 1.0
 */
public class DownloadMp3 {
    @Test
    public void context() {
        HttpResponse response = Requests.requests.request(
                HttpRequest.build()
                        .setUrl("http://39.137.67.84/lo.sycdn.kuwo.cn/78cb3d1d0ff22004a55b852e5fcf43e8/6329bcf7/resource/n3/11/0/923269084.mp3")
                        .setMethod(Requests.REQUEST_METHOD.GET)
                        .setEnableDataByte(true)
        );
        byte[] dataByte = response.getDataByte();
        if (dataByte != null) {
            try (FileOutputStream fos = new FileOutputStream("D:\\test.mp3")) {
                fos.write(dataByte);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("写入成功");
        }
    }
}
