package com.scuop.userauthservice.util;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalUtil {

    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal
            .withInitial(() -> new HashMap<>(10));

    public static Map<String, Object> getThreadLocal() {
        return threadLocal.get();
    }

    public static Object get(String key) {
        Map<String, Object> map = threadLocal.get();
        return map.get(key);
    }

    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        map.put(key, value);
    }

    public static void set(Map<String, Object> keyValueMap) {
        Map<String, Object> map = threadLocal.get();
        map.putAll(keyValueMap);
    }

    public static void remove() {
        threadLocal.remove();
    }

    @SuppressWarnings("unchecked")
    public static <T> T remove(String key) {
        Map<String, Object> map = threadLocal.get();
        return (T) map.remove(key);
    }

}