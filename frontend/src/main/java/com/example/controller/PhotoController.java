package com.example.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.converter.LocalDateStringConverter;
import lombok.extern.slf4j.Slf4j;

import com.example.model.Photo;
import com.example.model.PhotoAlbum;
import com.example.service.ApiService;
import com.example.util.DialogManager;

import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.GetInitResponse;
import io.swagger.client.model.InnerPhoneResponse;
import io.swagger.client.model.ServiceResultInteger;
import io.swagger.client.model.ServiceResultListGetInitResponse;
import io.swagger.client.model.ServiceResultListInnerPhoneResponse;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class PhotoController implements Initializable {

    @FXML
    private Button uploadBtn;
    @FXML
    private Button createAlbumBtn;
    @FXML
    private Label photoCountLabel;
    @FXML
    private Label albumCountLabel;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private TextField searchField;
    @FXML
    private Button albumTabBtn;
    @FXML
    private Button gridTabBtn;
    @FXML
    private ScrollPane albumScrollPane;
    @FXML
    private ScrollPane gridScrollPane;
    @FXML
    private FlowPane albumContainer;
    @FXML
    private FlowPane photoContainer;
    @FXML
    private VBox loadingContainer;
    @FXML
    private VBox emptyContainer;
    @FXML
    private Button emptyUploadBtn;

    private DialogManager dialogManager;
    private boolean isAlbumView = true;

    // 数据模型
    private List<PhotoAlbum> albums = new ArrayList<>();
    private List<Photo> allPhotos = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialogManager = DialogManager.getInstance();

        // 初始化 ComboBox 选项
        sortComboBox.getItems().addAll("最新上传", "最旧上传", "按名称排序", "按大小排序");
        sortComboBox.setValue("最新上传");

        setupEventHandlers();
        setupTabSwitch();
        setupSortAndSearch();
        loadSampleData();
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        uploadBtn.setOnAction(e -> handleUploadPhoto());
        emptyUploadBtn.setOnAction(e -> handleUploadPhoto());
        createAlbumBtn.setOnAction(e -> handleCreateAlbum());
    }

    /**
     * 设置标签页切换
     */
    private void setupTabSwitch() {
        albumTabBtn.setOnAction(e -> switchToAlbumView());
        gridTabBtn.setOnAction(e -> switchToGridView());
    }

    /**
     * 切换到相册视图
     */
    private void switchToAlbumView() {
        isAlbumView = true;

        // 更新按钮样式
        albumTabBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #6B73FF; -fx-font-weight: bold; -fx-padding: 10 0; -fx-border-color: #6B73FF; -fx-border-width: 0 0 2 0;");
        gridTabBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-padding: 10 0;");

        // 切换视图
        albumScrollPane.setVisible(true);
        albumScrollPane.setManaged(true);
        gridScrollPane.setVisible(false);
        gridScrollPane.setManaged(false);

        // 刷新显示
        refreshAlbumView();
    }

    /**
     * 切换到网格视图
     */
    private void switchToGridView() {
        isAlbumView = false;

        // 更新按钮样式
        gridTabBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #6B73FF; -fx-font-weight: bold; -fx-padding: 10 0; -fx-border-color: #6B73FF; -fx-border-width: 0 0 2 0;");
        albumTabBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; -fx-padding: 10 0;");

        // 切换视图
        gridScrollPane.setVisible(true);
        gridScrollPane.setManaged(true);
        albumScrollPane.setVisible(false);
        albumScrollPane.setManaged(false);

        // 刷新显示
        refreshGridView();
    }

    /**
     * 设置排序和搜索
     */
    private void setupSortAndSearch() {
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (isAlbumView) {
                refreshAlbumView();
            } else {
                refreshGridView();
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isAlbumView) {
                refreshAlbumView();
            } else {
                refreshGridView();
            }
        });
    }

    /**
     * 加载示例数据
     */
    private void loadSampleData() {
        showLoading(true);
        ApiService.getInstance().callAsync(
                this::getInitData,
                result -> getLoadData(result),
                error -> { showLoading(false); log.error("初始化数据加载失败", error); },
                loading -> {}
        );
        ApiService.getInstance().callAsync(
                this::getPhotoApi,
                result -> handlePhotoSuccess(result),
                error -> { showLoading(false); log.error("照片数据加载失败", error); },
                loading -> {}
        );
    }

    private ServiceResultListGetInitResponse getInitData() throws ApiException {
        DefaultApi api = ApiService.getInstance().getApi(DefaultApi.class);
        return api.getInit1();
    }

    private void getLoadData(ServiceResultListGetInitResponse dto) {
        if (dto == null || dto.getData() == null) {
            return;
        }
        // 清空數據避免重複
        albums.clear();
        List<GetInitResponse> getInitResponse = dto.getData();
        for (GetInitResponse item : getInitResponse) {
            PhotoAlbum photoAlbum = new PhotoAlbum(item.getName(), item.getCoverImageUrl(), item.getPhotoCount());
            albums.add(photoAlbum);
        }
        if (albums.isEmpty()) {
            dialogManager.showInfo("暂无数据");
        }
        updateStatistics();
        refreshAlbumView();
        showLoading(false);
    }

    /**
     * 更新统计信息
     */
    private void updateStatistics() {
        photoCountLabel.setText(allPhotos.size() + "张照片");
        albumCountLabel.setText(albums.size() + "个相册");
    }

    /**
     * 刷新相册视图
     */
    private void refreshAlbumView() {
        albumContainer.getChildren().clear();

        if (albums.isEmpty()) {
            showEmptyState(true);
            return;
        }

        showEmptyState(false);

        for (PhotoAlbum album : albums) {
            VBox albumCard = createAlbumCard(album);
            albumContainer.getChildren().add(albumCard);
        }
    }

    /**
     * 创建相册卡片
     */
    private VBox createAlbumCard(PhotoAlbum album) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        card.setPrefWidth(220);
        card.setPrefHeight(200);
        card.setCursor(javafx.scene.Cursor.HAND);

        // 相册封面区域 - 修改为显示网络图片
        StackPane coverArea = new StackPane();
        coverArea.setPrefHeight(140);
        coverArea.setStyle("-fx-background-radius: 16 16 0 0;");

        // 判断是否有封面图片URL
        if (album.getIcon() != null && !album.getIcon().isEmpty()) {
            // 使用网络图片作为封面
            ImageView coverImageView = new ImageView();
            coverImageView.setFitWidth(220);
            coverImageView.setFitHeight(140);
            coverImageView.setPreserveRatio(false);

            // 异步加载图片
            Image image = new Image(album.getIcon(), true);
            coverImageView.setImage(image);

            // 加载失败时显示图标
            image.errorProperty().addListener((obs, oldErr, newErr) -> {
                if (newErr != null) {
                    Platform.runLater(() -> {
                        coverArea.getChildren().clear();
                        Label iconLabel = new Label(album.getIcon());
                        iconLabel.setStyle("-fx-font-size: 48;");
                        coverArea.setStyle(
                                "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%); " +
                                        "-fx-background-radius: 16 16 0 0;");
                        coverArea.getChildren().add(iconLabel);
                    });
                }
            });

            // 图片加载成功时应用圆角裁剪
            image.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                if (newProgress.doubleValue() >= 1.0) {
                    // 设置圆角裁剪
                    Rectangle clip = new Rectangle(220, 140);
                    clip.setArcWidth(16);
                    clip.setArcHeight(16);
                    coverImageView.setClip(clip);
                }
            });

            coverArea.getChildren().add(coverImageView);
        } else {
            // 没有封面图时使用原来的渐变色+图标
            coverArea.setStyle(
                    "-fx-background-color: linear-gradient(135deg, #667eea 0%, #764ba2 100%); " +
                            "-fx-background-radius: 16 16 0 0;");
            Label iconLabel = new Label(album.getIcon());
            iconLabel.setStyle("-fx-font-size: 48;");
            coverArea.getChildren().add(iconLabel);
        }

        // 信息区域（保持不变）
        VBox infoArea = new VBox(5);
        infoArea.setStyle("-fx-padding: 12; -fx-alignment: CENTER;");

        Label nameLabel = new Label(album.getName());
        nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label countLabel = new Label(album.getPhotoCount() + "张照片");
        countLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999;");

        infoArea.getChildren().addAll(nameLabel, countLabel);

        card.getChildren().addAll(coverArea, infoArea);

        // 点击事件
        card.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                openAlbum(album);
            }
        });

        return card;
    }

    /**
     * 刷新网格视图
     */
    private void refreshGridView() {
        photoContainer.getChildren().clear();

        List<Photo> filteredPhotos = filterAndSortPhotos();
        if (filteredPhotos.isEmpty()) {
            showEmptyState(true);
            return;
        }

        showEmptyState(false);

        for (Photo photo : filteredPhotos) {
            VBox photoCard = createPhotoCard(photo);
            photoContainer.getChildren().add(photoCard);
        }
    }

    /**
     * 创建照片卡片
     */
    private VBox createPhotoCard(Photo photo) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        card.setPrefWidth(180);
        card.setCursor(javafx.scene.Cursor.HAND);

        // 照片预览区域
        StackPane imageArea = new StackPane();
        imageArea.setPrefHeight(160);
        imageArea.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 12 12 0 0;");

        // 尝试加载真实图片
        ImageView imageView = null;
        if (photo.getUrl() != null && !photo.getUrl().isEmpty()) {
            try {
                // 创建ImageView用于显示照片
                imageView = new ImageView();
                imageView.setFitWidth(180);
                imageView.setFitHeight(160);
                imageView.setPreserveRatio(true);

                // 异步加载图片
                Image image = new Image(photo.getUrl(), true);
                imageView.setImage(image);

                // 设置圆角裁剪
                Rectangle clip = new Rectangle(180, 160);
                clip.setArcWidth(12);
                clip.setArcHeight(12);
                imageView.setClip(clip);

                // 图片加载失败时显示占位符
                image.errorProperty().addListener((obs, oldErr, newErr) -> {
                    if (newErr != null) {
                        Platform.runLater(() -> {
                            imageArea.getChildren().clear();
                            Label placeholderLabel = new Label("🖼️");
                            placeholderLabel.setStyle("-fx-font-size: 48; -fx-opacity: 0.5;");
                            imageArea.getChildren().add(placeholderLabel);
                        });
                    }
                });

                imageArea.getChildren().add(imageView);
            } catch (Exception e) {
                // 加载失败，使用占位符
                Label placeholderLabel = new Label("🖼️");
                placeholderLabel.setStyle("-fx-font-size: 48; -fx-opacity: 0.5;");
                imageArea.getChildren().add(placeholderLabel);
            }
        } else {
            // 没有URL，使用占位符
            Label placeholderLabel = new Label("🖼️");
            placeholderLabel.setStyle("-fx-font-size: 48; -fx-opacity: 0.5;");
            imageArea.getChildren().add(placeholderLabel);
        }

        // 信息区域
        VBox infoArea = new VBox(4);
        infoArea.setStyle("-fx-padding: 10;");

        Label nameLabel = new Label(photo.getName());
        nameLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #333;");
        nameLabel.setWrapText(true);

        Label dateLabel = new Label(formatDate(photo.getUploadTime()));
        dateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #999;");

        infoArea.getChildren().addAll(nameLabel, dateLabel);

        card.getChildren().addAll(imageArea, infoArea);

        // 点击事件
        card.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                showPhotoDetail(photo);
            }
        });

        return card;
    }

    /**
     * 过滤和排序照片
     */
    private List<Photo> filterAndSortPhotos() {
        String searchText = searchField.getText().toLowerCase();
        String sortType = sortComboBox.getValue();

        // 獲取網格相冊列表

        List<Photo> result = new ArrayList<>(allPhotos);
        // 过滤
        if (!searchText.isEmpty()) {
            result.removeIf(photo -> !photo.getName().toLowerCase().contains(searchText));
        }
        // 排序
        if (sortType != null) {
            switch (sortType) {
                case "最新上传":
                    result.sort((a, b) -> b.getUploadTime().compareTo(a.getUploadTime()));
                    break;
                case "最旧上传":
                    result.sort((a, b) -> a.getUploadTime().compareTo(b.getUploadTime()));
                    break;
                case "按名称排序":
                    result.sort((a, b) -> a.getName().compareTo(b.getName()));
                    break;
                case "按大小排序":
                    result.sort((a, b) -> Long.compare(b.getSize(), a.getSize()));
                    break;
            }
        }
        return result;
    }

    private ServiceResultListInnerPhoneResponse getPhotoApi() throws ApiException {
        DefaultApi api = ApiService.getInstance().getApi(DefaultApi.class);
        return api.getInit();
    }

    private void handlePhotoSuccess(ServiceResultListInnerPhoneResponse dto) {
        if (dto == null || dto.getData() == null) {
            showLoading(false);
            return;
        }
        List<Photo> convertedPhotos = dto.getData().stream()
                .filter(Objects::nonNull)
                .map(this::convertToPhoto)
                .collect(Collectors.toList());

        allPhotos.clear(); // 清空旧数据
        allPhotos.addAll(convertedPhotos);

        Platform.runLater(() -> {
            updateStatistics();
            refreshGridView(); // 刷新视图
            showLoading(false);
        });
    }

    private Photo convertToPhoto(InnerPhoneResponse item) {
        Photo photo = new Photo();
        photo.setId(item.getId());
        photo.setAlbumId(item.getAlbumId());
        photo.setName(item.getTitle());
        photo.setUploadTime(LocalDateTime.of(2026, 4, 17, 14, 43, 45, 679610000)); // 写死的时间
        photo.setSize(20);
        photo.setUrl(item.getUrl());
        return photo;
    }

    /**
     * 处理上传照片
     */
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择照片");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));

        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files != null && !files.isEmpty()) {
            uploadPhotos(files);
        }
    }

    /**
     * 上传照片
     */
    private void uploadPhotos(List<File> files) {
        showLoading(true);
        try {
            ApiService.getInstance().callAsync(
                    () -> updateFiles(files),
                    this::handleUpdateSuccess,
                    this::handleUpdateError,
                    this::showLoading);
        } catch (Exception e) {
            log.error("加载菜单失败", e);
            dialogManager.showError(500, "加载菜单失败，使用默认菜单");
        }
    }

    private ServiceResultInteger updateFiles(List<File> files) throws ApiException {
        DefaultApi api = ApiService.getInstance().getApi(DefaultApi.class);
        return api.insertPhone(files);
    }

    private void handleUpdateSuccess(ServiceResultInteger serviceResultInteger) {

    }

    private void handleUpdateError(Throwable error) {

    }

    /**
     * 创建相册
     */
    private void handleCreateAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("创建相册");
        dialog.setHeaderText("📁 创建新相册");
        dialog.setContentText("相册名称:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                PhotoAlbum newAlbum = new PhotoAlbum(name, "📁", 0);
                albums.add(newAlbum);
                updateStatistics();
                refreshAlbumView();
                dialogManager.showSuccess("相册 '" + name + "' 创建成功");
            }
        });
    }

    /**
     * 打开相册
     */
    private void openAlbum(PhotoAlbum album) {
        dialogManager.showInfo("打开相册: " + album.getName());
        // 这里可以实现打开相册的具体逻辑
    }

    /**
     * 显示照片详情
     */
    private void showPhotoDetail(Photo photo) {
        dialogManager.showInfo("查看照片: " + photo.getName());
        // 这里可以实现照片详情弹窗
    }

    /**
     * 格式化日期
     */
    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dateTime.format(formatter);
    }

    /**
     * 显示/隐藏加载状态
     */
    private void showLoading(boolean show) {
        Platform.runLater(() -> {
            loadingContainer.setVisible(show);
            loadingContainer.setManaged(show);
            albumScrollPane.setVisible(!show && isAlbumView);
            gridScrollPane.setVisible(!show && !isAlbumView);
        });
    }

    /**
     * 显示/隐藏空状态
     */
    private void showEmptyState(boolean show) {
        Platform.runLater(() -> {
            emptyContainer.setVisible(show);
            emptyContainer.setManaged(show);
            albumScrollPane.setVisible(!show && isAlbumView);
            gridScrollPane.setVisible(!show && !isAlbumView);
        });
    }
}