module com.example.javafxdemo {
    // JavaFX 模块
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    // Java 标准库模块
    requires java.desktop;
    requires java.sql;

    // 工具类
    requires org.apache.commons.lang3;

    // 导出你的包，让 JavaFX 和其他模块能够访问
    exports com.example;
    exports com.example.controller;
    exports com.example.util;
    exports com.example.service;
    exports com.example.model;
    exports com.example.menu;
    exports com.example.factory;
    exports com.example.config;
    exports com.example.cache;
    exports com.example.ai;
    exports com.example.exception;
    exports io.swagger.client;
    exports io.swagger.client.api;
    exports io.swagger.client.model;
    exports io.swagger.client.auth;

    // 打开包给 JavaFX FXML 加载器使用（反射）
    opens com.example to javafx.fxml;
    opens com.example.controller to javafx.fxml;
    opens com.example.view to javafx.fxml;
    opens com.example.model;
    opens io.swagger.client.model;
}
