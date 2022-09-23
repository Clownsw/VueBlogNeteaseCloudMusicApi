package cn.smilex.vueblog.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author YangLuJia
 * @date 2022/9/23 16:31
 */
public class ProtocolFrameHandler extends LengthFieldBasedFrameDecoder {
    public ProtocolFrameHandler() {
        super(Integer.MAX_VALUE, 0, 4, 0, 0);
    }
}
