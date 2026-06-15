package com.example.cache;

import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 页面缓存管理器
 */
@Slf4j
public class PageCache {

    private static volatile PageCache instance;
    private final Map<String, Node> cache = new ConcurrentHashMap<>();

    private PageCache() {
    }

    public static PageCache getInstance() {
        if (instance == null) {
            synchronized (PageCache.class) {
                if (instance == null) {
                    instance = new PageCache();
                }
            }
        }
        return instance;
    }

    /**
     * 获取或加载页面（支持抛出异常的加载器）
     */
    public Node getOrLoad(String key, ThrowingSupplier<Node> loader) {
        if (cache.containsKey(key)) {
            log.debug("从缓存加载页面: {}", key);
            return cache.get(key);
        }

        log.debug("加载新页面: {}", key);
        try {
            Node page = loader.get();
            if (page != null) {
                cache.put(key, page);
            }
            return page;
        } catch (Exception e) {
            log.error("加载页面失败: {}", key, e);
            throw new RuntimeException("加载页面失败: " + key, e);
        }
    }

    /**
     * 函数式接口，允许抛出异常
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    /**
     * 清除缓存
     */
    public void clear() {
        cache.clear();
        log.info("页面缓存已清空");
    }

    /**
     * 移除指定页面
     */
    public void remove(String key) {
        cache.remove(key);
    }
}