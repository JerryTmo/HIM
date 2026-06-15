package com.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import javax.imageio.ImageIO;
import javafx.stage.FileChooser;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class MindMapViewerController implements Initializable {

    @FXML
    private Canvas mindMapCanvas;
    @FXML
    private Label mapTitleLabel;
    @FXML
    private Label zoomLevelLabel;
    @FXML
    private Label nodeCountLabel;
    @FXML
    private Label lastModifiedLabel;
    @FXML
    private ColorPicker colorPicker;

    private MindMapData mindMapData;
    private String currentLayout = "tree";
    private double zoomLevel = 1.0;
    private double panX = 0;
    private double panY = 0;
    private double lastMouseX, lastMouseY;
    private boolean isDragging = false;
    private Color primaryColor = Color.rgb(108, 92, 231);

    // 节点位置缓存
    private Map<MindNode, double[]> nodePositions = new HashMap<>();
    private List<MindNode> allNodes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupCanvasInteraction();
        setupColorPicker();
        updateLastModified();
    }

    public void setMindMapData(MindMapData data, String layout, String theme) {
        this.mindMapData = data;
        this.allNodes.clear();
        flattenNodes(data.rootNode, allNodes);

        // 解析布局
        if (layout.contains("树形"))
            currentLayout = "tree";
        else if (layout.contains("辐射"))
            currentLayout = "radial";
        else if (layout.contains("组织"))
            currentLayout = "org";

        // 解析主题颜色
        if (theme.contains("绿色"))
            primaryColor = Color.rgb(0, 184, 148);
        else if (theme.contains("蓝色"))
            primaryColor = Color.rgb(9, 132, 227);
        else if (theme.contains("红色"))
            primaryColor = Color.rgb(231, 76, 60);
        else if (theme.contains("暗夜"))
            primaryColor = Color.rgb(178, 190, 195);

        // 更新UI
        mapTitleLabel.setText(data.rootNode.name);
        nodeCountLabel.setText("节点: " + allNodes.size());
        colorPicker.setValue(primaryColor);

        // 延迟渲染以确保Canvas已初始化
        new Thread(() -> {
            try {
                Thread.sleep(100);
                javafx.application.Platform.runLater(this::renderMindMap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupCanvasInteraction() {
        mindMapCanvas.setOnScroll(event -> {
            if (event.getDeltaY() > 0)
                zoomIn();
            else
                zoomOut();
        });

        mindMapCanvas.setOnMousePressed(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            isDragging = true;
        });

        mindMapCanvas.setOnMouseDragged(event -> {
            if (isDragging) {
                panX += event.getX() - lastMouseX;
                panY += event.getY() - lastMouseY;
                lastMouseX = event.getX();
                lastMouseY = event.getY();
                renderMindMap();
            }
        });

        mindMapCanvas.setOnMouseReleased(event -> isDragging = false);

        // 双击重置视图
        mindMapCanvas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                resetView();
            }
        });
    }

    private void setupColorPicker() {
        colorPicker.setOnAction(event -> {
            primaryColor = colorPicker.getValue();
            renderMindMap();
        });
    }

    private void renderMindMap() {
        if (mindMapData == null)
            return;

        GraphicsContext gc = mindMapCanvas.getGraphicsContext2D();
        double width = mindMapCanvas.getWidth();
        double height = mindMapCanvas.getHeight();

        gc.clearRect(0, 0, width, height);
        drawBackground(gc, width, height);

        gc.save();
        gc.translate(width / 2 + panX, height / 2 + panY);
        gc.scale(zoomLevel, zoomLevel);

        nodePositions.clear();

        switch (currentLayout) {
            case "radial":
                drawRadialLayout(gc, mindMapData.rootNode);
                break;
            case "org":
                drawOrgLayout(gc, mindMapData.rootNode, width);
                break;
            default:
                drawTreeLayout(gc, mindMapData.rootNode, 0, 0);
        }

        gc.restore();
    }

    private void drawBackground(GraphicsContext gc, double width, double height) {
        // 背景色
        gc.setFill(Color.rgb(248, 249, 250));
        gc.fillRect(0, 0, width, height);

        // 网格
        gc.setStroke(Color.rgb(220, 220, 220, 0.3));
        gc.setLineWidth(0.5);
        double gridSize = 40 * zoomLevel;
        for (double x = panX % gridSize; x < width; x += gridSize) {
            gc.strokeLine(x, 0, x, height);
        }
        for (double y = panY % gridSize; y < height; y += gridSize) {
            gc.strokeLine(0, y, width, y);
        }
    }

    private void drawTreeLayout(GraphicsContext gc, MindNode node, double x, double y) {
        double nodeWidth = 130;
        double nodeHeight = 45;
        double hSpacing = 170;
        double vSpacing = 65;

        Color nodeColor = getNodeColor(node.level);
        drawNodeBox(gc, node, x, y, nodeWidth, nodeHeight, nodeColor);
        nodePositions.put(node, new double[] { x, y, nodeWidth, nodeHeight });

        double totalHeight = (node.children.size() - 1) * vSpacing;
        double startY = y - totalHeight / 2;

        for (int i = 0; i < node.children.size(); i++) {
            MindNode child = node.children.get(i);
            double childX = x + hSpacing;
            double childY = startY + i * vSpacing;

            drawBezierLine(gc, x + nodeWidth, y + nodeHeight / 2,
                    childX, childY + nodeHeight / 2, nodeColor);
            drawTreeLayout(gc, child, childX, childY);
        }
    }

    private void drawRadialLayout(GraphicsContext gc, MindNode node) {
        double centerX = -65;
        double centerY = -25;
        double width = 130;
        double height = 50;

        Color nodeColor = getNodeColor(node.level);
        drawNodeBox(gc, node, centerX, centerY, width, height, nodeColor);
        nodePositions.put(node, new double[] { centerX, centerY, width, height });

        if (node.children.isEmpty())
            return;

        int count = node.children.size();
        double radius = 200;
        double angleStep = 2 * Math.PI / count;

        for (int i = 0; i < count; i++) {
            double angle = i * angleStep - Math.PI / 2;
            double childX = radius * Math.cos(angle) - 60;
            double childY = radius * Math.sin(angle) - 20;

            gc.setStroke(nodeColor);
            gc.setLineWidth(2);
            gc.strokeLine(0, 0, childX + 60, childY + 20);

            Color childColor = getNodeColor(node.children.get(i).level);
            drawNodeBox(gc, node.children.get(i), childX, childY, 120, 40, childColor);
            nodePositions.put(node.children.get(i), new double[] { childX, childY, 120, 40 });
        }
    }

    private void drawOrgLayout(GraphicsContext gc, MindNode node, double canvasWidth) {
        drawOrgTreeLayout(gc, node, -65, -250, canvasWidth);
    }

    private void drawOrgTreeLayout(GraphicsContext gc, MindNode node, double x, double y, double canvasWidth) {
        double nodeWidth = 140;
        double nodeHeight = 45;
        double vSpacing = 80;

        Color nodeColor = getNodeColor(node.level);
        drawNodeBox(gc, node, x - nodeWidth / 2, y, nodeWidth, nodeHeight, nodeColor);
        nodePositions.put(node, new double[] { x - nodeWidth / 2, y, nodeWidth, nodeHeight });

        if (node.children.isEmpty())
            return;

        double totalWidth = node.children.size() * (nodeWidth + 30) - 30;
        double startX = x - totalWidth / 2;
        double childY = y + vSpacing;

        for (MindNode child : node.children) {
            double childX = startX + nodeWidth / 2;
            drawOrthogonalLine(gc, x, y + nodeHeight, childX, childY, nodeColor);
            drawOrgTreeLayout(gc, child, childX, childY, canvasWidth);
            startX += nodeWidth + 30;
        }
    }

    private void drawNodeBox(GraphicsContext gc, MindNode node, double x, double y,
            double width, double height, Color color) {
        // 阴影
        gc.setFill(Color.rgb(0, 0, 0, 0.08));
        gc.fillRoundRect(x + 3, y + 3, width, height, 12, 12);

        // 渐变背景
        LinearGradient gradient = new LinearGradient(x, y, x, y + height, false, CycleMethod.NO_CYCLE,
                new Stop(0, color.deriveColor(0, 1, 1.3, 1)),
                new Stop(1, color));
        gc.setFill(gradient);
        gc.fillRoundRect(x, y, width, height, 12, 12);

        // 边框
        gc.setStroke(color.darker());
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, width, height, 12, 12);

        // 文字
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Microsoft YaHei", 13));
        gc.setTextAlign(TextAlignment.CENTER);

        String display = node.name.length() > 12 ? node.name.substring(0, 10) + ".." : node.name;
        gc.fillText(display, x + width / 2, y + height / 2 + 5);
    }

    private void drawBezierLine(GraphicsContext gc, double x1, double y1, double x2, double y2, Color color) {
        gc.setStroke(color.deriveColor(0, 1, 1, 0.5));
        gc.setLineWidth(2);
        double cx = (x1 + x2) / 2;
        gc.beginPath();
        gc.moveTo(x1, y1);
        gc.bezierCurveTo(cx, y1, cx, y2, x2, y2);
        gc.stroke();
    }

    private void drawOrthogonalLine(GraphicsContext gc, double x1, double y1, double x2, double y2, Color color) {
        gc.setStroke(color.deriveColor(0, 1, 1, 0.5));
        gc.setLineWidth(2);
        double midY = (y1 + y2) / 2;
        gc.beginPath();
        gc.moveTo(x1, y1);
        gc.lineTo(x1, midY);
        gc.lineTo(x2, midY);
        gc.lineTo(x2, y2);
        gc.stroke();
    }

    private Color getNodeColor(int level) {
        Color[] colors = {
                primaryColor,
                Color.rgb(0, 184, 148),
                Color.rgb(253, 121, 168),
                Color.rgb(9, 132, 227),
                Color.rgb(253, 203, 110),
        };
        return colors[Math.min(level, colors.length - 1)];
    }

    private void flattenNodes(MindNode node, List<MindNode> list) {
        list.add(node);
        for (MindNode child : node.children) {
            flattenNodes(child, list);
        }
    }

    // ===== 工具栏方法 =====

    @FXML
    private void switchToTreeLayout() {
        currentLayout = "tree";
        renderMindMap();
    }

    @FXML
    private void switchToRadialLayout() {
        currentLayout = "radial";
        renderMindMap();
    }

    @FXML
    private void switchToOrgLayout() {
        currentLayout = "org";
        renderMindMap();
    }

    @FXML
    private void zoomIn() {
        zoomLevel = Math.min(zoomLevel * 1.2, 3.0);
        zoomLevelLabel.setText((int) (zoomLevel * 100) + "%");
        renderMindMap();
    }

    @FXML
    private void zoomOut() {
        zoomLevel = Math.max(zoomLevel / 1.2, 0.3);
        zoomLevelLabel.setText((int) (zoomLevel * 100) + "%");
        renderMindMap();
    }

    @FXML
    private void resetView() {
        zoomLevel = 1.0;
        panX = 0;
        panY = 0;
        zoomLevelLabel.setText("100%");
        renderMindMap();
    }

    @FXML
    private void fitToScreen() {
        resetView();
    }

    @FXML
    private void refreshView() {
        renderMindMap();
    }

    @FXML
    private void exportAsImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导出为PNG");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG图片", "*.png"));
        fileChooser.setInitialFileName("思维导图_" + System.currentTimeMillis() + ".png");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                BufferedImage image = new BufferedImage(
                        (int) mindMapCanvas.getWidth(), (int) mindMapCanvas.getHeight(),
                        BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D g2d = image.createGraphics();
                // g2d.drawImage(SwingFXUtils.fromFXImage(
                // mindMapCanvas.snapshot(null, null), null), 0, 0, null);
                g2d.dispose();
                ImageIO.write(image, "png", file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void exportAsPDF() {
        // PDF导出功能（需要额外库支持）
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("导出PDF");
        alert.setHeaderText(null);
        alert.setContentText("PDF导出功能需要iText库支持，请稍后尝试或导出为PNG格式。");
        alert.showAndWait();
    }

    @FXML
    private void shareMindMap() {
        // 分享功能
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("分享");
        alert.setHeaderText(null);
        alert.setContentText("分享功能开发中，您可以先导出为图片分享。");
        alert.showAndWait();
    }

    private void updateLastModified() {
        lastModifiedLabel.setText("最后修改: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }
}