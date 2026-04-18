# AGENTS.md — EduHub 仓库指南

## 仓库概览

EduHub 是教学协同平台，采用**前后端分仓目录**（非 monorepo），所有构建/开发/测试命令必须在对应子目录下执行。

- **后端**：`backend/` — Spring Boot 3.2.3 + MyBatis-Plus + Maven，Java 17
- **前端**：`frontend/` — Vue 3 + Vite + TypeScript + Ant Design Vue + Pinia

---

## 构建 / 开发 / 测试命令

### 后端（在 `backend/` 下执行）

| 任务 | 命令 |
|------|------|
| 编译（跳过测试） | `mvn -DskipTests compile` |
| 运行全部测试 | `mvn test` |
| 运行单个测试类 | `mvn -Dtest=ClassName test` |
| 运行单个测试方法 | `mvn -Dtest=ClassName#methodName test` |
| 打包 | `mvn package -DskipTests` |
| 启动服务 | `mvn spring-boot:run`（默认端口 8101，上下文路径 `/api`） |

### 前端（在 `frontend/` 下执行）

| 任务 | 命令 |
|------|------|
| 安装依赖 | `npm install` |
| 开发服务器 | `npm run dev`（Vite 默认端口 5173） |
| 生产构建 | `npm run build` ← **用此命令验证变更；项目未配置 linter** |
| 生成 API 客户端 | `npm run openapi`（读取 `http://localhost:5000/openapi.json`，执行前确认目标可访问） |

前端无 lint、test 或 CI 流水线，变更后务必执行 `npm run build` 验证。

---

## 模块体系（强制）

所有新增业务代码必须放入对应模块目录。模块：`user`、`homework`、`exam`、`experiment`、`common`。

### 后端模块路径
- Controller：`controller/{module}/`
- Service 接口：`service/{module}/`
- Service 实现：`service/{module}/impl/`
- Mapper：`mapper/{module}/`
- Entity：`model/entity/{module}/`
- DTO：`model/dto/{module}/`
- VO：`model/vo/{module}/`

### 前端模块路径
- 视图：`src/views/{module}/`
- 组件：`src/components/{module}/`
- Store：`src/stores/{module}/`

**例外**：根包下的 `User`/`Post`/`PostThumb`/`PostFavour` 模板链路允许保留，不得擅自迁移。

---

## 后端代码风格

### 响应包装
所有 Controller 方法返回 `BaseResponse<T>`，通过 `ResultUtils.success(data)` 包装；异常通过 `BusinessException` 抛出：
```java
return ResultUtils.success(service.doSomething(...));
throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称不能为空");
ThrowUtils.throwIf(condition, ErrorCode.NOT_FOUND_ERROR);
```

### 依赖注入
使用 `@Resource`（JSR-250），**禁止** `@Autowired`。

### 实体规范
- `@TableName` + `@Data`（Lombok）+ `implements Serializable`
- 自增主键用 `@TableId(type = IdType.AUTO)`
- 蛇形列名用 `@TableField` 显式映射
- 实体前缀表示领域：`Edu*`（核心业务）、`Auth*`（认证）、`Res*`（记录）、`Rel*`（关联）、`Sys*`（系统）

### DTO 命名
- 新增：`{Entity}AddRequest`
- 查询：`{Entity}QueryRequest`（继承 `PageRequest`）
- 更新：`{Entity}UpdateRequest`
- 删除：使用通用 `DeleteRequest`（含 `id` 字段）

### VO 规范
- 命名：`{Entity}VO`
- 必须包含 `static XxxVO objToVo(Xxx entity)` 转换方法，内部使用 `BeanUtils.copyProperties`

### Service 四方法契约
每个 `XxxService` 必须实现：
1. `void validXxx(Xxx entity, boolean add)` — 校验
2. `QueryWrapper<Xxx> getQueryWrapper(XxxQueryRequest queryRequest)` — 查询构建
3. `XxxVO getXxxVO(Xxx entity, HttpServletRequest request)` — 实体转 VO
4. `Page<XxxVO> getXxxVOPage(Page<Xxx> entityPage, HttpServletRequest request)` — 分页 VO

### 校验方式
手动空值检查 + `BusinessException` / `ThrowUtils.throwIf()`。**禁止**使用 Bean Validation 注解（`@NotNull` 等）。

### 鉴权
基于 Session（配置 Redis 后自动切换）。Controller 使用 `@AuthCheck(mustRole = "...")` 注解。

### 常量
以接口形式定义（`CommonConstant`、`UserConstant`），不用枚举或类。

---

## 前端代码风格

### 视觉与样式规范（商务风格，强制）
- **风格定位**：专业商务风（Professional Business Style）。界面保持克制、清晰、高对比度；色调以纯色、中性色（深灰、浅蓝、纯白）构建层次感。
- **禁用渐变色**：全局禁止使用 CSS 渐变（`linear-gradient`、`radial-gradient`），所有背景、按钮、卡片均使用纯色填充。
- **禁用 Emoji**：全局禁止在界面文本和图标中使用 Emoji 表情符号。
- **图标优先级**：优先使用 `@ant-design/icons-vue`（主）或 `@fortawesome/vue-fontawesome`（辅）。如需自定义图标，必须在 `src/components/icons/` 下创建独立 SVG 图标组件（如 `IconCustomLogo.vue`），通过 props 控制尺寸与颜色；**严禁**在业务页面中内联 `<svg>` 标签硬编码矢量图形。
- **色彩系统**：使用 `theme.css` 定义的 CSS 自定义属性（`--color-primary`、`--color-bg-*`、`--color-text-*` 等），不在组件中硬编码色值。

### Vue 组件
- 统一使用 `<script setup lang="ts">` + Composition API。
- Props 定义：`interface Props` + `withDefaults(defineProps<Props>(), { ... })`。
- CSS 类名：BEM 命名 `block__element--modifier`，样式作用域 `scoped`，属性值取自 `theme.css`。

### 状态管理（Pinia）
- 使用 setup 函数风格：`defineStore('module-role', () => { ... })`。
- Store 命名：`use{Module}{Role}Store`（如 `useExamStudentStore`）。
- 每个模块包含：`types.ts`（领域类型）、`repository.ts`（API 适配层）、store 文件、`fixtures.ts`。

### API 分层架构
```
api/*.ts（自动生成，禁止手动编辑）
  → stores/{module}/repository.ts（解包 + 防御性解析 + 类型映射）
    → stores/{module}/{store}.ts（状态管理）
      → views/{module}/*.vue（UI）
```
- `api/` 目录由 `npm run openapi` 生成，禁止手动修改。
- Repository 的 `unwrap()` 二次校验响应码并提供中文错误信息。
- 防御性辅助函数（`toNumber`、`toStringValue`、`toOptionalString`、`toBooleanValue`）处理可能畸变的后端数据。

### 路由系统
- 路由文件放在 `src/router/`，通过 `import.meta.glob('./*.ts')` 自动发现。
- 学生端路由：默认导出 `RouteRecordRaw[]`，包裹 `BasicLayout`。
- 管理端路由：命名导出 `adminXxxRoutes`，扁平 `RouteRecordRaw[]`（作为 `/admin` 的子路由）。
- 必须设置 `meta.title`（`guards.ts` 据此设置页面标题）。

### TypeScript 类型
- 自动生成类型：`API.*` 命名空间位于 `api/typings.d.ts`（所有字段均为 optional）。
- 领域类型：严格接口定义在 `stores/*/types.ts`（必填字段、联合类型）。
- 视图中**禁止**直接使用原始 `API.*` 类型，必须经过 repository 层映射。

### 路径别名
- `@` → `./src`（在 `vite.config.ts` 和 `tsconfig.json` 中均已配置）。

### 组件库
- UI 组件库：Ant Design Vue，模板中使用 `a-*` 组件。
- 图标库：`@ant-design/icons-vue`（主要）、`@fortawesome/vue-fontawesome`（次要）。
- 自定义图标：在 `src/components/icons/` 下创建 `IconXxx.vue` 组件，通过 props 控制尺寸和颜色；业务页面中禁止内联 SVG。

---

## 代理与环境

- 后端默认：MySQL `localhost:3306/edu_hub`，端口 `8101`，上下文路径 `/api`。
- 前端 Vite 代理：去掉 `/api` 前缀后转发到 `http://localhost:8101/api`。
- 环境变量：`.env.development` / `.env.production`（均设置 `VITE_API_BASE_URL=/api`）。
- 代理时注意：避免双重 `/api` 前缀或路径被错误重写。

---

## 提交前自检

1. 新增文件路径在正确的模块目录下。
2. Java `package` 声明与目录路径一致。
3. 业务 Service 包含四个标准方法。
4. 后端编译通过：`mvn -DskipTests compile`（在 `backend/` 下）。
5. 前端构建通过：`npm run build`（在 `frontend/` 下）。
6. 未手动编辑 `frontend/src/api/*`——如需更新请通过 `npm run openapi` 重新生成。
7. 前端界面无 Emoji、无渐变色，图标使用图标库或自定义 SVG 组件。