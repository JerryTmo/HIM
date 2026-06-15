package com.example.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                final String securitySchemeName = "bearerAuth";

                return new OpenAPI()
                                .info(new Info()
                                                .title("JavaFX API 文檔")
                                                .description("JavaFX 桌面應用後端 API")
                                                .version("1.0.0")
                                                .contact(new Contact().name("開發團隊").email("dev@example.com"))
                                                .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                                .servers(List.of(new Server().url("http://localhost:8080").description("本地服務器")))
                                .components(new Components()
                                                // 方式1：使用 additionalProperties 的替代寫法
                                                .addSchemas("MapStringString",
                                                                new ObjectSchema() // 使用 ObjectSchema 替代 Schema<Map>
                                                                                .additionalProperties(
                                                                                                new StringSchema())) // 正確的
                                                                                                                     // Schema
                                                .addSchemas("ServiceResultMapStringString",
                                                                new ObjectSchema()
                                                                                .addProperty("success",
                                                                                                new BooleanSchema()
                                                                                                                .example(true))
                                                                                .addProperty("message",
                                                                                                new StringSchema()
                                                                                                                .example("成功"))
                                                                                .addProperty("code", new IntegerSchema()
                                                                                                .example(200))
                                                                                .addProperty("timestamp",
                                                                                                new IntegerSchema()
                                                                                                                .example(1234567890))
                                                                                .addProperty("data",
                                                                                                new ObjectSchema() // 直接在這裡定義，不用引用
                                                                                                                .additionalProperties(
                                                                                                                                new StringSchema())))
                                                // 添加基礎的 ServiceResult
                                                .addSchemas("ServiceResult", new ObjectSchema()
                                                                .addProperty("success",
                                                                                new BooleanSchema().example(true))
                                                                .addProperty("message",
                                                                                new StringSchema().example("成功"))
                                                                .addProperty("code", new IntegerSchema().example(200))
                                                                .addProperty("timestamp",
                                                                                new IntegerSchema().example(1234567890))
                                                                .addProperty("data", new ObjectSchema()))
                                                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("請輸入 JWT Token（格式：Bearer your-token-here）")))
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .tags(List.of(
                                                new Tag().name("菜單模塊").description("菜單相關操作"),
                                                new Tag().name("認證模塊").description("用戶認證相關操作")));
        }
}