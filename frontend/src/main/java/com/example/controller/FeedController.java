package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FeedController {

    @FXML
    private VBox feedContainer;

    @FXML
    private Label feedTitle;

    @FXML
    public void initialize() {
        // 初始化動態頁面
    }

    /**
     * 加載用戶的動態
     */
    private void loadUserFeeds(String username) {
        // TODO: 從數據庫或API加載實際的動態數據
        // 這裡先用模擬數據

        System.out.println("為用戶 " + username + " 加載動態");
    }

    /**
     * 刷新動態
     */
    @FXML
    private void handleRefresh() {
        System.out.println("刷新動態");
        // 重新加載動態
    }

    /**
     * 點讚動態
     */
    @FXML
    private void handleLike() {
        System.out.println("點讚");
    }

    /**
     * 評論動態
     */
    @FXML
    private void handleComment() {
        System.out.println("評論");
    }

    /**
     * 分享動態
     */
    @FXML
    private void handleShare() {
        System.out.println("分享");
    }
}