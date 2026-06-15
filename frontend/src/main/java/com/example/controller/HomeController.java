package com.example.controller;

import com.example.App;
import com.example.cache.MenuCache;
import com.example.factory.ContentLoaderFactory;
import com.example.factory.MenuItemFactory;
import com.example.menu.AppPage;
import com.example.service.SystemApiService;
import com.example.service.SystemApiService.MenuDTO;
import com.example.util.DialogManager;
import com.example.util.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class HomeController {

    @FXML
    private Label usernameLabel;

    @FXML
    private VBox menuContainer;

    @FXML
    private VBox shortcutContainer;

    @FXML
    private ScrollPane menuScrollPane;

    @FXML
    private StackPane contentArea;

    private DialogManager dialogManager;
    private MenuItemFactory menuItemFactory;
    private ContentLoaderFactory contentLoaderFactory;

    @FXML
    public void initialize() {
        dialogManager = DialogManager.getInstance();
        menuItemFactory = new MenuItemFactory();
        setupUserInfo();
        loadDynamicMenus();
    }

    private void setupUserInfo() {
        String username = UserSession.getUsername();
        if (username != null && !username.isEmpty()) {
            usernameLabel.setText(username);
        }
    }

    /**
     * 從後端動態加載菜單
     */
    private void loadDynamicMenus() {
        SystemApiService.getInstance().getMenus(
                this::onMenusLoaded,
                error -> {
                    log.error("加載菜單失敗: {}", error.getMessage());
                    // 如果菜單加載失敗，使用緩存或靜態備用
                    List<MenuDTO> cachedMenus = MenuCache.getInstance().getMenuTree();
                    if (cachedMenus != null && !cachedMenus.isEmpty()) {
                        renderMenus(cachedMenus);
                    } else {
                        log.warn("菜單加載失敗，且無緩存數據");
                    }
                }
        );
    }

    /**
     * 菜單加載成功後渲染
     */
    private void onMenusLoaded(List<MenuDTO> menus) {
        Platform.runLater(() -> {
            // 緩存菜單
            MenuCache.getInstance().cacheMenuTree(menus);

            // 初始化內容加載器
            contentLoaderFactory = new ContentLoaderFactory(this);

            // 渲染菜單
            renderMenus(menus);
        });
    }

    /**
     * 渲染菜單樹到側邊欄
     */
    private void renderMenus(List<MenuDTO> menus) {
        menuContainer.getChildren().clear();

        for (MenuDTO menu : menus) {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                // 有子菜單 -> 渲染為分類標題 + 子菜單按鈕
                Node categoryLabel = menuItemFactory.createCategoryLabel(menu);
                menuContainer.getChildren().add(categoryLabel);

                for (MenuDTO child : menu.getChildren()) {
                    Button menuButton = menuItemFactory.createMenuItem(child, 0);
                    menuButton.setOnAction(e -> handleMenuClick(child));
                    menuContainer.getChildren().add(menuButton);
                }
            } else {
                // 無子菜單 -> 直接渲染為按鈕
                Button menuButton = menuItemFactory.createMenuItem(menu, 0);
                menuButton.setOnAction(e -> handleMenuClick(menu));
                menuContainer.getChildren().add(menuButton);
            }
        }

        // 更新快捷功能區域（可選：只顯示部分核心菜單）
        updateShortcuts(menus);
    }

    /**
     * 處理菜單點擊 - 根據路由加載內容
     */
    private void handleMenuClick(MenuDTO menu) {
        if (contentLoaderFactory == null) {
            contentLoaderFactory = new ContentLoaderFactory(this);
        }

        Node content = contentLoaderFactory.loadContent(menu);
        if (content != null) {
            // 更新快捷按鈕的激活樣式
            updateActiveMenuStyle(menu.getId());

            // 這裡需要設置內容到主區域
            // 內容會通過 ContentLoaderFactory.loadContent() 返回並由 App 或回調處理
            // 目前 ContentLoaderFactory 返回 Node，需由 HomeController 將內容放入 content area
        }
    }

    /**
     * 更新菜單激活樣式
     */
    private void updateActiveMenuStyle(String menuId) {
        for (Node node : menuContainer.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                MenuDTO menuData = (MenuDTO) btn.getUserData();
                if (menuData != null && menuData.getId().equals(menuId)) {
                    btn.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; " +
                            "-fx-background-radius: 8; -fx-alignment: CENTER_LEFT; " +
                            "-fx-padding: 12 18; -fx-font-size: 14; -fx-cursor: hand; -fx-font-weight: 500;");
                } else {
                    btn.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #666; " +
                            "-fx-background-radius: 8; -fx-alignment: CENTER_LEFT; " +
                            "-fx-padding: 12 18; -fx-font-size: 14; -fx-cursor: hand;");
                }
            }
        }
    }

    /**
     * 更新快捷功能區域
     */
    private void updateShortcuts(List<MenuDTO> menus) {
        if (shortcutContainer == null) return;

        shortcutContainer.getChildren().clear();
        // 只取前幾個菜單作為快捷入口
        menus.stream()
                .limit(5)
                .forEach(menu -> {
                    if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                        // 取每個分類的第一個子菜單
                        MenuDTO firstChild = menu.getChildren().get(0);
                        addShortcutButton(firstChild);
                    } else {
                        addShortcutButton(menu);
                    }
                });
    }

    private void addShortcutButton(MenuDTO menu) {
        Button shortcutBtn = new Button(menu.getTitle());
        shortcutBtn.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; " +
                "-fx-background-radius: 8; -fx-alignment: CENTER_LEFT; " +
                "-fx-padding: 12 18; -fx-font-size: 14; -fx-cursor: hand; -fx-font-weight: 500;");
        shortcutBtn.setUserData(menu);
        shortcutBtn.setOnAction(e -> handleMenuClick(menu));
        shortcutContainer.getChildren().add(shortcutBtn);
    }

    /**
     * 獲取內容區域（由 ContentLoaderFactory 調用）
     */
    public StackPane getContentArea() {
        return contentArea;
    }

    @FXML
    private void handleLogout() {
        if (dialogManager.showConfirm("确认退出", "确定要退出系统吗？")) {
            UserSession.logout();
            App.navigateTo(AppPage.LOGIN);
        }
    }
}
