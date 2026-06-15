package com.example;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import com.example.menu.AppPage;

public class App extends Application {

    private static Stage primaryStage;
    private static Scene primaryScene;

    // 使用 EnumMap 作為頁面配置
    private static final Map<AppPage, FXMLLoader> loaderCache = new EnumMap<>(AppPage.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // 導航到登錄頁
        navigateTo(AppPage.LOGIN);
    }

    /**
     * 統一的頁面導航方法（使用枚舉）
     */
    public static void navigateTo(AppPage page) {
        try {
            // 加载新的FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource(page.getFxmlPath()));
            Parent root = loader.load();

            // 创建新的Scene，使用枚举中定义的尺寸和颜色
            Scene newScene = new Scene(root, page.getWidth(), page.getHeight());
            newScene.setFill(page.getFillColor());

            // 更新引用
            primaryScene = newScene;

            // 缓存 loader（可选）
            loaderCache.put(page, loader);

            // 关键修复：检查是否需要改变窗口样式
            StageStyle currentStyle = primaryStage.getStyle();
            StageStyle newStyle = page.getStageStyle();

            // 如果需要改变样式
            if (currentStyle != newStyle) {
                // 隐藏当前窗口
                primaryStage.hide();

                // 创建新的Stage（因为样式不能改变，只能创建新的）
                Stage newStage = new Stage();
                newStage.initStyle(newStyle);
                newStage.setScene(newScene);
                newStage.setTitle(page.getDescription());

                // 如果页面可拖动，启用拖动功能
                if (page.isDraggable()) {
                    enableWindowDrag(root, newStage);
                }

                // 显示新窗口
                newStage.show();

                // 关闭旧窗口并更新引用
                primaryStage.close();
                primaryStage = newStage;
            } else {
                // 样式相同，直接切换场景
                primaryStage.setScene(newScene);
                primaryStage.setTitle(page.getDescription());

                // 如果页面可拖动，启用拖动功能
                if (page.isDraggable()) {
                    enableWindowDrag(root, primaryStage);
                }

                // 如果窗口没有显示，则显示
                if (!primaryStage.isShowing()) {
                    primaryStage.show();
                }
            }

            // 添加淡入动画效果
            addFadeAnimation(root);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("无法加载页面: " + page.getFxmlPath());
        }
    }

    /**
     * 字符串版本的导航方法（用于向后兼容）
     */
    public static void navigateTo(String fxmlName, String title) {
        try {
            // 根据fxmlName查找对应的枚举
            String nameWithoutExt = fxmlName.replace(".fxml", "");
            AppPage page = AppPage.fromFxml(nameWithoutExt);
            navigateTo(page);
        } catch (IllegalArgumentException e) {
            // 如果找不到对应的枚举，使用默认方式加载
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/example/view/" + fxmlName));
                Parent root = loader.load();
                Scene newScene = new Scene(root);
                primaryScene = newScene;
                primaryStage.setScene(newScene);
                primaryStage.setTitle(title);
                primaryStage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 添加淡入动画效果
     */
    private static void addFadeAnimation(Parent root) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * 向後兼容的方法（支持字符串參數）
     */
    public static void setRoot(String fxmlName) throws IOException {
        AppPage page = AppPage.fromFxml(fxmlName);
        navigateTo(page);
    }

    /**
     * 实现窗口拖动功能（按住任意位置拖动）
     */
    private static void enableWindowDrag(Parent root, Stage stage) {
        final double[] xOffset = new double[1];
        final double[] yOffset = new double[1];

        root.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        });
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * 獲取 FXMLLoader（用於需要控制器的場景）
     */
    public static FXMLLoader getLoader(String fxmlName) {
        return new FXMLLoader(App.class.getResource("/com/example/view/" + fxmlName + ".fxml"));
    }

    /**
     * 獲取 FXMLLoader（枚舉版本）
     */
    public static FXMLLoader getLoader(AppPage page) {
        return new FXMLLoader(App.class.getResource(page.getFxmlPath()));
    }

    /**
     * 獲取控制器（用於頁面間傳遞數據）
     */
    public static <T> T getController(AppPage page) {
        FXMLLoader loader = loaderCache.get(page);
        if (loader != null) {
            return loader.getController();
        }
        return null;
    }

    /**
     * 清除頁面快取（用於重新加載頁面）
     */
    public static void clearCache() {
        loaderCache.clear();
        System.out.println("頁面快取已清除");
    }

    /**
     * 清除指定頁面的快取
     */
    public static void clearCache(AppPage page) {
        loaderCache.remove(page);
        System.out.println("已清除頁面快取: " + page);
    }

    /**
     * 获取当前Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * 获取当前Scene
     */
    public static Scene getPrimaryScene() {
        return primaryScene;
    }
}
