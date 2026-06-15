package com.example.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import com.example.service.SystemApiService.MenuDTO;
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
     * 加载内容并设置到主内容区域
     */
    public Node loadContent(MenuDTO menu) {
        // 检查缓存
        if (contentCache.containsKey(menu.getId())) {
            Node cached = contentCache.get(menu.getId());
            setContentToArea(cached);
            return cached;
        }

        // 根据路由加载
        Node content = loadByRoute(menu);

        // 缓存内容
        if (content != null) {
            contentCache.put(menu.getId(), content);
            setContentToArea(content);
        }

        return content;
    }

    /**
     * 将加载的内容设置到主页面的内容区域
     */
    private void setContentToArea(Node content) {
        controller.getContentArea().getChildren().clear();
        controller.getContentArea().getChildren().add(content);
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
        if (route.contains("patient"))
            return createSimpleContent("患者管理", "这里是患者管理页面");
        if (route.contains("doctor") || route.contains("schedule"))
            return createSimpleContent("医生排班", "这里是医生排班页面");
        if (route.contains("medical") || route.contains("record"))
            return createSimpleContent("病历管理", "这里是病历管理页面");
        if (route.contains("medicine"))
            return createSimpleContent("药品管理", "这里是药品管理页面");
        if (route.contains("appointment"))
            return createSimpleContent("预约管理", "这里是预约管理页面");
        if (route.contains("dynamic"))
            return createSimpleContent("动态", "这里是动态页面");
        if (route.contains("album") || route.contains("photo"))
            return createSimpleContent("相册", "这里是相册页面");
        if (route.contains("journal") || route.contains("log"))
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
