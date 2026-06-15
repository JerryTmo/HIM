# AIMindMap 后端服务文档

## 📋 项目概述

AIMindMap 后端服务是基于 Spring Boot 构建的 RESTful API 服务，为 JavaFX 桌面客户端提供 AI 思维导图生成、用户管理、数据持久化等功能。

### 核心功能
- 🤖 **AI 智能生成**：集成大语言模型 API，自动生成思维导图结构
- 👤 **用户管理**：JWT 认证、用户注册登录、个人空间
- 💾 **数据持久化**：思维导图的存储、版本管理、分享功能
- 🔌 **插件化设计**：支持多种 AI 服务商（OpenAI、Claude、国产大模型）
- 📊 **数据分析**：用户行为统计、导图热度排行

## 🛠️ 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17+ | 核心语言 |
| Spring Boot | 2.7.14 | 后端框架 |
| Spring Security | 5.7.x | 安全认证 |
| Spring Data JPA | 2.7.x | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 7.0+ | 缓存、会话管理 |
| JWT | 0.11.5 | Token 认证 |
| Maven | 3.8+ | 项目构建 |
| Swagger/OpenAPI | 3.0 | API 文档 |
| Lombok | 1.18.x | 代码简化 |
| MapStruct | 1.5.x | 对象映射 |

## 📁 项目结构
