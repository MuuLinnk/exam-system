# 在线考试系统

Spring Boot + Vue 3 的在线考试平台，支持学生端考试和教师端管理。

## 功能

**学生端**
- 注册 / 登录 / 找回密码
- 查看考试列表，限时答题
- 支持单选题、多选题、判断题、填空题、简答题
- 自动判分（客观题）+ 教师批改（简答题）
- 错题本（含解析）
- 成绩统计

**教师端**
- 概览面板（学生数、考试数、平均分、待批改数）
- 题库管理（按科目/题型筛选、增删改、添加解析）
- 试卷管理（配置各题型抽题数量）
- 学生管理（按班级筛选、重置密码）
- 成绩总览（按科目/班级/状态筛选）
- 简答题批改

## 技术栈

| 层面 | 技术 |
|------|------|
| 后端 | Spring Boot 3.4、Spring Data JPA、BCrypt |
| 数据库 | MySQL 8.0 |
| 前端 | Vue 3、Element Plus、Vue Router 4、Axios |
| 构建 | Maven、Vite |

## 快速启动

### 环境要求

- JDK 17+
- MySQL 8.0
- Node.js 18+

### 1. 创建数据库

```sql
CREATE DATABASE exam_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 配置数据库连接

修改 `exam-backend/src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/exam_system?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 3. 启动后端

```bash
cd exam-backend
mvn clean package -DskipTests
java -jar target/exam-backend-1.0.jar
```

后端运行在 `http://localhost:8080`，首次启动自动建表并初始化数据。

### 4. 启动前端

```bash
cd exam-frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`。

### 5. 访问

- 学生端：`http://localhost:5173/login`
- 教师端：`http://localhost:5173/teacher/login`

## 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 学生 | 2024001 | 123456 |
| 学生 | 2024002 | 123456 |
| 教师 | admin | 123456 |

## 题型说明

| 类型 | 标识 | 判分方式 |
|------|------|----------|
| 单选题 | CHOICE | 自动判分 |
| 多选题 | MULTI_CHOICE | 自动判分（集合比较） |
| 判断题 | JUDGE | 自动判分 |
| 填空题 | BLANK | 自动判分（忽略空格） |
| 简答题 | ESSAY | 教师手动批改 |

## 项目结构

```
exam-system/
├── exam-backend/               # Spring Boot 后端
│   └── src/main/java/com/exam/
│       ├── entity/             # 实体类
│       ├── repository/         # 数据访问
│       ├── service/            # 业务逻辑
│       ├── controller/         # API 控制器
│       ├── dto/                # 请求/响应对象
│       └── config/             # 配置（CORS、数据初始化）
├── exam-frontend/              # Vue 3 前端
│   └── src/
│       ├── views/              # 页面
│       │   └── teacher/        # 教师端页面
│       ├── components/         # 题型组件
│       ├── layouts/            # 布局
│       ├── router/             # 路由
│       └── api/                # API 封装
└── 开发文档.md                  # 详细设计文档
```
