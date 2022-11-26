package cn.smilex.vueblog.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author smilex
 */
public class CommonUtil {
    public static final ObjectMapper OBJECT_MAPPER;
    public static final ExecutorService THREAD_POOL;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        THREAD_POOL = Executors.newCachedThreadPool();
    }
}
