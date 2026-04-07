# EduHub 仓库协作指引（给 OpenCode）

## 1) 先认清仓库结构
- 这是前后端分仓目录，不是 monorepo 工具链：`backend/`（Spring Boot + Maven）、`frontend/`（Vue3 + Vite + npm）。
- 根目录没有统一构建脚本；所有安装、构建、测试命令都要在对应子目录执行。

## 2) 最常用命令（按目录执行）
- 前端安装与启动：`npm install`、`npm run dev`（在 `frontend/`）。
- 前端生产构建：`npm run build`（在 `frontend/`）。
- 前端 OpenAPI 生成：`npm run openapi`（在 `frontend/`，生成到 `frontend/src/api/`）。
- 后端编译：`mvn -DskipTests compile`（在 `backend/`）。
- 后端测试：`mvn test`；单测可用 `mvn -Dtest=ClassName#methodName test`（在 `backend/`）。

## 3) 关键约束（来自 `.cursor/rules/eduhub-module-structure.mdc`）
- 新增业务代码优先按模块落位：`user` / `homework` / `exam` / `experiment` / `common`。
- 前端新页面/组件/store 应分别放到：
  - `frontend/src/views/{module}/`
  - `frontend/src/components/{module}/`
  - `frontend/src/stores/{module}/`
- 后端新增 Controller/Service/Mapper 按模块目录放置（`controller/{module}`、`service/{module}`、`mapper/{module}`）。
- `User`（及规则中提到的模板链路）允许保留在根目录层级，避免“为整洁而迁移”导致历史链路破坏。

## 4) 前端路由与 API 的仓库特性
- 路由聚合由 `frontend/src/router/index.ts` 的 `import.meta.glob('./*.ts')` 自动收集；新增路由文件放在 `frontend/src/router/` 即可。
- `frontend/src/router/guards.ts` 会统一改 `document.title`，新路由建议补 `meta.title`。
- `frontend/src/request.ts` 统一读取 `VITE_API_BASE_URL`，默认来自 `.env.development` / `.env.production`（当前均是 `/api`）。

## 5) OpenAPI 与生成代码注意事项
- `frontend/openapi2ts.config.ts` 当前读取 `http://localhost:5000/openapi.json`，不是后端默认 8101 端口；执行 `npm run openapi` 前先确认该地址可访问。
- `frontend/src/api/*` 为生成产物（文件头含 `// @ts-ignore`、`/* eslint-disable */`）；优先通过后端 OpenAPI + `npm run openapi` 更新，避免手改后被覆盖。

## 6) 本地联调易错点
- 后端 `application.yml` 默认：MySQL `jdbc:mysql://localhost:3306/edu_hub`，服务端口 `8101`，`context-path: /api`。
- 前端 Vite 代理目标是 `http://localhost:8200/api` 且会去掉请求前缀 `/api`；若直连后端 8101，请同步检查代理与 `VITE_API_BASE_URL`，避免出现双 `/api` 或路径被错误重写。

## 7) 现状事实（避免误判）
- 当前仓库未发现前端 lint / test 脚本；前端变更至少跑 `npm run build` 做回归验证。
- 当前仓库未发现 CI workflow、husky、pre-commit 配置；不要假设存在提交前自动检查。
