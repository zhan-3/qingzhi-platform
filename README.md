# 青知共享平台

面向校内师生的学习资源共享系统。师生可以上传、管理和收藏资料，管理员负责用户管理与资源审核。

本项目主要用于实践 Spring Boot 分层开发、MyBatis、MySQL、JWT 鉴权、文件存储、Excel 批量导入和逻辑软删除。

## 功能

### 用户与权限

- 学生、教师注册和登录
- BCrypt 密码哈希
- JWT + Interceptor 身份认证
- 学生、教师、管理员角色控制
- 登录失败计数与临时锁定
- 管理员用户管理及密码重置
- 用户软删除，删除后旧 JWT 立即失效

### 资源与收藏

- 资源发布、修改、删除和分页查询
- 待审核、已通过、已拒绝状态流转
- 管理员审核及拒绝理由
- 用户只能修改和删除自己的资源
- 收藏、取消收藏和分页查询
- 用户、资源、收藏关联软删除

### 文件与导入

- 单文件和批量上传
- 文件大小及 MIME 类型校验
- UUID 存储名
- MD5 内容去重
- 数据库失败时清理磁盘文件
- 受权限保护的 PDF/图片文件流预览
- EasyExcel 批量导入学生和教师
- 逐行错误报告及事务回滚

### 工程能力

- Swagger/OpenAPI 接口文档
- AOP 接口日志和密码脱敏
- PageHelper 分页
- 全局异常处理
- Vue 3 管理与资源页面
- Maven 单元测试

## 技术栈

### 后端

- Java 17+
- Spring Boot 4.1
- Spring MVC / AOP / Validation
- MyBatis 4.0
- MySQL 8
- JJWT 0.13
- EasyExcel 4.0
- PageHelper 2.1
- Springdoc OpenAPI

### 前端

- Vue 3
- TypeScript
- Vite
- Element Plus
- Axios

## 项目结构

```text
qingzhi-platform/
├─ src/main/java/com/zhan/qingzhiplatform/
│  ├─ config/          # MVC、Swagger、管理员初始化
│  ├─ controller/      # HTTP接口
│  ├─ interceptor/     # 登录与管理员鉴权
│  ├─ service/         # 业务逻辑和事务边界
│  ├─ mapper/          # MyBatis接口
│  ├─ pojo/            # DTO、Entity、统一响应
│  ├─ exception/       # 业务异常和全局异常处理
│  ├─ aop/             # 接口日志
│  └─ util/            # JWT、BCrypt、MD5工具
├─ src/main/resources/
│  ├─ db/
│  │  ├─ qingzhi_db.sql             # 全新数据库初始化
│  │  └─ soft-delete-migration.sql  # 旧数据库一次性升级
│  ├─ static/          # 构建后的前端静态资源
│  └─ application*.yaml
├─ src/test/           # 单元测试
├─ frontend/           # Vue前端源码
├─ uploads/            # 本地上传目录
└─ .env.example        # 后端环境变量示例
```

## 数据设计

核心表：

| 表 | 用途 |
|---|---|
| `users` | 学生、教师、管理员 |
| `login_logs` | 登录成功/失败记录 |
| `files` | 物理文件元数据 |
| `resources` | 资源信息和审核状态 |
| `favorites` | 用户与资源收藏关系 |

项目明确采用**逻辑外键**，不使用 MySQL `FOREIGN KEY`。引用完整性由以下机制共同保证：

- Service 写入前验证关联对象；
- 跨表修改使用事务；
- 用户、资源、收藏采用软删除；
- 用户名、收藏关系等使用唯一索引；
- 关联字段使用普通索引；
- 集成测试和数据巡检负责发现遗漏。

软删除记录通过 `deleted_at` 标记，正常查询统一过滤 `deleted_at IS NULL`。已软删除的用户名不会被重新注册，已取消收藏的记录在再次收藏时恢复。

## 环境要求

- JDK 17 或更高版本
- Maven 3.9+
- MySQL 8
- Node.js 22（仅前端开发需要）

## 后端启动

### 1. 配置环境变量

```bash
cp .env.example .env
```

编辑 `.env`：

```properties
DB_URL=jdbc:mysql://localhost:3306/qingzhi_db
DB_USERNAME=root
DB_PASSWORD=your-password
JWT_SECRET=replace-with-at-least-32-random-characters
JWT_EXPIRATION_MS=43200000
```

不要提交真实 `.env`。JWT密钥至少使用32个随机字符。

### 2. 初始化数据库

全新数据库执行：

```bash
mysql -u root -p < src/main/resources/db/qingzhi_db.sql
```

如果已有数据库仍是旧结构，只执行一次：

```bash
mysql -u root -p qingzhi_db < src/main/resources/db/soft-delete-migration.sql
```

两份脚本不要重复执行。项目尚未集成 Flyway，旧数据库不会自动升级。

### 3. 启动服务

```bash
mvn spring-boot:run
```

默认地址：

```text
http://localhost:8080/api
```

Swagger UI：

```text
http://localhost:8080/api/swagger-ui/index.html
```

首次启动时，如果数据库中不存在管理员，系统会创建演示账号：

```text
用户名：Admin
密码：Admin2026
```

该默认密码仅用于本地演示，正式部署必须更换。

## 前端开发

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

如果 Vite 与后端分别运行，需要将前端 `.env` 设置为完整后端地址：

```properties
VITE_API_BASE_URL=http://localhost:8080/api
```

生产构建：

```bash
npm run build
```

如需由 Spring Boot 同源托管，可将 `frontend/dist/` 内容复制到：

```text
src/main/resources/static/
```

## 测试

```bash
mvn test
```

当前覆盖的关键场景包括：

- JWT只解析一次并写入请求上下文
- 软删除用户的旧token失效
- 不允许收藏不存在或未通过资源
- 并发重复收藏返回友好错误
- 恢复已软删除收藏
- 资源删除同步软删除收藏
- 用户删除同步软删除资源与收藏
- 管理员账号禁止删除
- 审核通过清空旧拒绝理由
- 文件上传失败补偿与删除权限

## API认证

除登录、注册、静态资源和Swagger外，请求需要携带：

```http
Authorization: Bearer <jwt>
```

JWT载荷包含 `username`、`userId` 和 `role`，不包含密码。

## 文件预览边界

当前后端提供受JWT保护的文件流预览接口。PDF和图片可由前端通过携带JWT的 Axios 请求获取 Blob 后展示。

PPT/Word 的 Microsoft Office Online 预览尚未形成正式闭环，因为第三方服务器无法携带用户JWT，并且需要公网可访问URL。正式实现建议使用私有OSS预签名URL；本地演示可使用ngrok配合短期应用签名URL。

## 已知限制

- 没有使用物理外键，应用层必须持续维护逻辑引用完整性；
- MD5字段尚未设置唯一索引，并发上传相同新文件仍可能重复；
- 文件名与物理文件记录绑定，秒传时可能复用首次上传者的文件名；
- Excel大批量导入存在逐行查重和长事务成本；
- 文件上传频率限制尚未实现；
- CORS开发配置较宽松，正式环境应限制允许来源；
- Office文档公网预览尚未完成。

## 安全说明

本项目用于学习和演示，不应未经加固直接用于生产环境。正式部署至少需要：

- 修改默认管理员密码；
- 使用环境变量或密钥管理服务保存JWT密钥；
- 使用HTTPS；
- 限制CORS来源；
- 增加上传限流和病毒扫描；
- 使用私有对象存储及短期签名URL；
- 增加数据库迁移工具和集成测试。

## License

本仓库暂未声明开源许可证。未经许可，不代表允许复制、分发或用于商业用途。
