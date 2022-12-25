package cn.smilex.vueblog.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author smilex
 */
@SuppressWarnings("UnusedReturnValue")
public class CommonUtil {
    public static final ObjectMapper OBJECT_MAPPER;
    private static final ExecutorService THREAD_POOL;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        THREAD_POOL = Executors.newCachedThreadPool();
    }

    /**
     * 提交任务到线程池
     *
     * @param runnable 任务
     * @return 结果
     */
    public static Future<?> submit(Runnable runnable) {
        return THREAD_POOL.submit(runnable);
    }
}
