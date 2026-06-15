package com.example.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IconUtil {
    private static final Logger logger = LoggerFactory.getLogger(IconUtil.class);

    // 图标文件路径
    private static final String ICON_PATH = "/icons/";

    // 默认图标大小
    private static final int DEFAULT_ICON_SIZE = 16;

    // 图标缓存，避免重复加载
    private static final Map<String, ImageView> iconCache = new HashMap<>();

    // 支持的图片格式
    private static final String[] SUPPORTED_FORMATS = { ".png", ".jpg", ".jpeg", ".svg", ".gif", ".ico" };

    /**
     * 獲取圖標
     * 默認大小(16px)
     * 
     * @param iconName 圖標名稱
     * @return ImageView
     */
    public static ImageView getIcon(String iconName) {
        return getIcon(iconName, DEFAULT_ICON_SIZE);
    }

    /**
     * 获取图标
     * 
     * @param iconName 图标名称（不含扩展名）
     * @param size     图标大小
     * @return ImageView
     */
    public static ImageView getIcon(String iconName, int size) {
        if (iconName == null && iconName.isEmpty()) {
            return null;
        }
        String cacheKey = iconName + "_" + size;
        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }

        ImageView imageView = loadIcon(iconName, size);
        if (imageView != null) {
            iconCache.put(cacheKey, imageView);
        }
        return imageView;
    }

    /**
     * 加載圖標
     * 
     * @param iconName 圖標名稱
     * @param size     大小(默認16px)
     * @return ImageView
     */
    public static ImageView loadIcon(String iconName, int size) {
        try {
            for (String formate : SUPPORTED_FORMATS) {
                String path = ICON_PATH + iconName + formate;
                InputStream inputStream = IconUtil.class.getResourceAsStream(path);
                if (inputStream != null) {
                    Image image = new Image(inputStream);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(size);
                    imageView.setFitHeight(size);
                    imageView.setPreserveRatio(true);
                    return imageView;
                }
            }
        } catch (Exception e) {
            log.error("iconName 不存在:{}", e);
            throw e;
        }
        return null;
    }

    /**
     * 清空緩存Map
     */
    public static void clearCache() {
        iconCache.clear();
    }
}
