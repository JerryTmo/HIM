package com.example.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import io.swagger.client.model.MenuDTO;
import com.example.controller.HomeController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 内容加载器工厂
 */
@Slf4j
@RequiredArgsConstructor
public class ContentLoaderFactory {

    private final HomeController controller;
    private final Map<String, Node> contentCache = new HashMap<>();

    /**
     * 加载内容
     */
    public Node loadContent(MenuDTO menu) {
        // 检查缓存
        if (contentCache.containsKey(menu.getId())) {
            return contentCache.get(menu.getId());
        }

        // 根据路由加载
        Node content = loadByRoute(menu);

        // 缓存内容
        if (content != null) {
            contentCache.put(menu.getId(), content);
        }

        return content;
    }

    /**
     * 根据路由加载内容
     */
    private Node loadByRoute(MenuDTO menu) {
        String route = StringUtils.substringAfterLast(menu.getRoute(), "/");
        log.debug("route:{}", route);

        if (route == null || route.isEmpty()) {
            return createDefaultContent(menu.getTitle());
        }

        try {
            // 修正：正确加载 FXML 文件
            String fxmlPath;
            if (route.startsWith("/")) {
                fxmlPath = route;
            } else {
                fxmlPath = "/com/example/view/" + route + ".fxml";
            }

            // 获取资源 URL
            java.net.URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                log.warn("FXML 文件不存在: {}", fxmlPath);
                return loadFallbackContent(route, menu.getTitle());
            }

            // 正确创建 FXMLLoader 并设置位置
            FXMLLoader loader = new FXMLLoader(resource);
            return loader.load();

        } catch (IOException e) {
            log.error("加载FXML失败: {}", route, e);
            return loadFallbackContent(route, menu.getTitle());
        }
    }

    /**
     * 加载备用内容
     */
    private Node loadFallbackContent(String route, String title) {
        if (route.contains("dynamic"))
            return createSimpleContent("动态", "这里是动态页面");
        if (route.contains("album"))
            return createSimpleContent("相册", "这里是相册页面");
        if (route.contains("journal"))
            return createSimpleContent("日志", "这里是日志页面");
        if (route.contains("mindmap"))
            return createSimpleContent("思维导图", "这里是思维导图页面");
        if (route.contains("settings"))
            return createSimpleContent("设置", "这里是设置页面");
        return createDefaultContent(title);
    }

    /**
     * 创建简单内容页面
     */
    private Node createSimpleContent(String title, String message) {
        VBox content = new VBox(20);
        content.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        content.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");

        content.getChildren().addAll(titleLabel, messageLabel);
        return content;
    }

    /**
     * 创建默认内容页面
     */
    private Node createDefaultContent(String title) {
        VBox content = new VBox(20);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setStyle("-fx-padding: 40;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label messageLabel = new Label("这是 " + title + " 页面，内容正在开发中...");
        messageLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #999;");

        content.getChildren().addAll(titleLabel, messageLabel);
        return content;
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        contentCache.clear();
    }
}