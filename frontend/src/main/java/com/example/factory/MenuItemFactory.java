package com.example.factory;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import io.swagger.client.model.MenuDTO;
import com.example.util.IconUtil;

/**
 * 菜单项工厂
 */
public class MenuItemFactory {

    private static final int ICON_SIZE = 14;
    private static final int CATEGORY_ICON_SIZE = 14;

    /**
     * 创建分类标签
     */
    public Node createCategoryLabel(MenuDTO category) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setSpacing(8);
        container.setStyle("-fx-padding: 10 0 5 5;");

        // 加载图标
        Node icon = loadIcon(category, CATEGORY_ICON_SIZE);
        if (icon != null) {
            container.getChildren().add(icon);
        }

        // 标题
        Label title = new Label(category.getTitle());
        title.setStyle("-fx-font-size: 12; -fx-text-fill: #999; -fx-font-weight: bold;");
        container.getChildren().add(title);

        return container;
    }

    /**
     * 创建菜单项按钮（支持图标）
     */
    public Button createMenuItem(MenuDTO menu, int level) {
        int indent = 15 + level * 20;

        // 创建带图标和文字的容器
        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(8);

        // ⭐ 关键：加载图标
        Node icon = loadIcon(menu, ICON_SIZE);
        if (icon != null) {
            content.getChildren().add(icon);
        }

        // 标题文字
        Label titleLabel = new Label(menu.getTitle());
        titleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        content.getChildren().add(titleLabel);

        // 创建按钮并设置图形内容
        Button button = new Button();
        button.setGraphic(content);
        button.setUserData(menu);
        applyMenuItemStyle(button, indent);
        setupTooltip(button, menu);

        return button;
    }

    /**
     * 加载图标（统一方法）
     */
    private Node loadIcon(MenuDTO menu, int size) {
        String iconName = menu.getIcon();

        // 1. 优先使用数据库配置的图标
        if (iconName != null && !iconName.isEmpty()) {
            Node icon = IconUtil.getIcon(iconName, size);
            if (icon != null) {
                return icon;
            }
        }

        // 2. 如果没有自定义图标，尝试根据标题获取默认Emoji
        String defaultEmoji = getDefaultEmoji(menu.getTitle());
        if (defaultEmoji != null) {
            Label emojiLabel = new Label(defaultEmoji);
            emojiLabel.setStyle("-fx-font-size: " + size + "px;");
            return emojiLabel;
        }

        return null;
    }

    /**
     * 获取默认Emoji图标
     */
    private String getDefaultEmoji(String title) {
        if (title == null)
            return null;

        if (title.contains("动态") || title.contains("朋友圈"))
            return "📱";
        if (title.contains("相册") || title.contains("照片"))
            return "📸";
        if (title.contains("日志") || title.contains("日记"))
            return "✍️";
        if (title.contains("思维导图"))
            return "🧠";
        if (title.contains("生成"))
            return "⚡";
        if (title.contains("模板"))
            return "📊";
        if (title.contains("设置"))
            return "⚙️";
        if (title.contains("帮助"))
            return "❓";
        if (title.contains("朋友"))
            return "👥";
        if (title.contains("消息"))
            return "💬";
        if (title.contains("社交"))
            return "👥";
        if (title.contains("AI"))
            return "🤖";
        if (title.contains("文件"))
            return "📄";
        if (title.contains("图片"))
            return "🖼️";
        if (title.contains("视频"))
            return "🎬";
        if (title.contains("音乐"))
            return "🎵";

        return null;
    }

    /**
     * 应用菜单项样式
     */
    private void applyMenuItemStyle(Button button, int indent) {
        String baseStyle = String.format(
                "-fx-background-color: transparent; " +
                        "-fx-alignment: CENTER_LEFT; " +
                        "-fx-padding: 8 15 8 %d; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand;",
                indent);

        button.setStyle(baseStyle);

        // 悬停效果
        button.setOnMouseEntered(e -> {
            if (!button.getStyle().contains("#e8f0ff")) {
                button.setStyle("-fx-background-color: #f5f5f5; " + baseStyle);
            }
        });

        button.setOnMouseExited(e -> {
            if (!button.getStyle().contains("#e8f0ff")) {
                button.setStyle(baseStyle);
            }
        });
    }

    /**
     * 设置工具提示
     */
    private void setupTooltip(Button button, MenuDTO menu) {
        if (menu.getRoute() != null && !menu.getRoute().isEmpty()) {
            Tooltip tooltip = new Tooltip("路由: " + menu.getRoute());
            Tooltip.install(button, tooltip);
        }
    }
}