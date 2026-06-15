module com.example.javafxdemo {
    // JavaFX 模块
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    // Java 标准库模块
    requires java.desktop;
    requires java.sql;
    requires java.net.http;
    requires java.management;

    // Jackson JSON 处理模块 (named modules)
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    // SLF4J + Logback (named modules in recent versions)
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;

    // 工具类
    requires org.apache.commons.lang3;

    // Jakarta annotations (used by Swagger codegen)
    requires jakarta.annotation;
    requires jakarta.ws.rs;

    // OkHttp/Gson are automatic modules from the classpath
    requires okhttp3;
    requires okio;
    requires com.google.gson;

    // JDK internals used by some dependencies (e.g. Unsafe)
    requires static jdk.unsupported;

    // 导出包 - 让外部代码（包括测试）可以访问
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

    // Swagger codegen 包
    exports io.swagger.client;
    exports io.swagger.client.api;
    exports io.swagger.client.model;
    exports io.swagger.client.auth;

    // ===== 打开包给 JavaFX FXML 加载器使用（反射） =====
    opens com.example to javafx.fxml;
    opens com.example.controller to javafx.fxml;
    opens com.example.view to javafx.fxml;
    opens com.example.model to javafx.fxml;
    opens com.example.menu to javafx.fxml;
    opens com.example.factory to javafx.fxml;
    opens com.example.service to javafx.fxml;

    // ===== 打开包给 Jackson 用于 JSON 序列化/反序列化 =====
    opens com.example.service to com.fasterxml.jackson.databind;
    opens com.example.model to com.fasterxml.jackson.databind;
    opens com.example.menu to com.fasterxml.jackson.databind;

    // ===== 打开 Swagger codegen 模型给 Jackson 和 Gson =====
    opens io.swagger.client.model to com.fasterxml.jackson.databind, com.google.gson;
    opens io.swagger.client to com.fasterxml.jackson.databind, com.google.gson;

    // ===== 打开给 Gson（Swagger codegen 默认使用 Gson） =====
    opens com.example.service to com.google.gson;
    opens com.example.model to com.google.gson;
}
