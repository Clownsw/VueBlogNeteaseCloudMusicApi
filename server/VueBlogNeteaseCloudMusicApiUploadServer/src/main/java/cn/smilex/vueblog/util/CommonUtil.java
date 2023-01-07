package cn.smilex.vueblog.util;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.SM4;
import cn.smilex.vueblog.concurrent.CounterThreadFactory;
import cn.smilex.vueblog.service.RemoteService;
import cn.smilex.vueblog.service.impl.UpYunServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author smilex
 */
@SuppressWarnings({"UnusedReturnValue", "unused", "SpellCheckingInspection"})
public class CommonUtil {
    public static final String EMPTY_STRING = "";
    public static final ObjectMapper OBJECT_MAPPER;
    private static final ExecutorService THREAD_POOL;
    private static final SM4 SM_4 = new SM4(
            Mode.ECB,
            Padding.ZeroPadding,
            new byte[]{
                    0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x10,
                    0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x10
            }
    );
    public static final RemoteService UPYUN_SERVICE = new UpYunServiceImpl();

    static {
        OBJECT_MAPPER = new ObjectMapper();
        THREAD_POOL = Executors.newCachedThreadPool(new CounterThreadFactory("common-thread-pool"));
    }

    /**
     * 创建一个任务到公共线程池
     *
     * @param runnable 任务
     * @return Future
     */
    public static Future<?> submit(Runnable runnable) {
        return THREAD_POOL.submit(runnable);
    }

    /**
     * SM4 加密data
     *
     * @param data data
     * @return encrypt data
     */
    public static byte[] encrypt(byte[] data) {
        return SM_4.encrypt(data);
    }

    /**
     * SM4 解密data
     *
     * @param data data
     * @return decrypt data
     */
    public static byte[] decrypt(byte[] data) {
        return SM_4.decrypt(data);
    }

    /**
     * 遍历Map根据Value找Key
     *
     * @param map   map
     * @param value value
     * @param <K>   key type
     * @param <V>   value type
     * @return key
     */
    public static <K, V> K traverseMapGetKeyByValue(Map<K, V> map, Object value) {
        if (value == null || map.size() == 0) {
            return null;
        }

        for (K k : map.keySet()) {
            if (value.equals(map.get(k))) {
                return k;
            }
        }

        return null;
    }
}
