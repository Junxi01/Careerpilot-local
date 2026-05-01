# PROJECT_CONTEXT — CareerPilot Local 交接说明

> **给 AI / 协作者**：在新 Cursor 对话开始时，请先阅读本文件、`README.md`，再按需查看 `backend/`、`frontend/`、`database/`、`scripts/`、`docs/`。  
> **仓库根目录**：本项目的开发与 Git 根目录为 **`careerpilot-local/`**。上一层桌面文件夹 **`Career Pilot Local/`** 仅工作区入口（可选 `README.md`），Gradle/npm 等命令须在 **`careerpilot-local/`** 或其子目录执行。  
> **接力规则**：项目内已配置 Cursor Rule **`.cursor/rules/careerpilot-project-context-handoff.mdc`**（`alwaysApply: true`）。用户说出 **每日收尾口令** 时，Cursor 应**直接编辑本文件**中「进度 / 下一步 / 最近一次会话交接」等段落，无需再单独下「改文件」指令。

---

## 1. 项目目标（要做什么）

Self-hosted、本地优先的 **AI 求职助手**：用户配置目标公司、**公开**招聘页 URL、地点、岗位/技术关键词；系统从**用户配置的公开职业页**抓取/整理线索、落库、生成面试准备计划；并跟踪投递、面试、准备任务、跟进提醒、周报与备份等。

**产品设计边界（重要）**：

- **不**爬取或依赖 LinkedIn / Indeed / Glassdoor 等聚合站，也**不**处理需要登录的页面；**仅支持用户自行配置的、可公开访问的公司招聘页**。
- AI 调用通过 **外部 API** 完成，密钥与基址来自 **`.env`**，**仓库内不得硬编码密钥**。
- 交付形态：可 **Docker Compose** 本地一键起服务（当前 Compose 仍为占位，见下文）。

---

## 2. 当前技术栈（实际采用）

| 层级 | 选型 | 备注 |
|------|------|------|
| 后端 | Kotlin + Ktor，Gradle | `backend/settings.gradle.kts`，`backend/build.gradle.kts`；入口 `com.careerpilot.ApplicationKt`，`application.conf` 配端口 |
| 前端 | React 18 + TypeScript + Vite 5 | `frontend/package.json`、`frontend/tsconfig.json`、`frontend/vite.config.ts` |
| 数据库 | **计划** MySQL | `database/schema.sql` / `seed.sql` 仍为占位；未接业务表 |
| 脚本 | **计划** Python | `scripts/requirements.txt` 占位；自动化待实现 |
| 部署 | Docker Compose（阶段 1） | **`mysql:8.0` 服务**：持久卷、healthcheck、首次初始化挂载 `database/schema.sql`。后端/前端尚未加入 Compose。 |
| 密钥 | `.env`（不提交） | 模板见 `.env.example` |

**Node 提示**：本前端为 **Vite 5**，一般 **Node 18+** 即可；若改用官方最新 `create-vite` 脚手架，可能要求更高 Node 版本，以本机 `node -v` 为准。

---

## 3. 仓库与路径约定

- **Git / 开发根目录**：`careerpilot-local/`
- **父级工作区**（可选）：`Career Pilot Local/README.md` 仅说明子目录入口；不要把另一在研项目与该目录混在一起提交。
- **忽略规则**：`.gitignore` 已包含 `.env`、`backend/build/`、`frontend/node_modules/` 等。

---

## 4. 当前完成进度（随每日收尾滚动更新；下方「最近一次会话交接」记录最新一次）

### 已完成（脚手架）

- 目录结构：`backend/`、`frontend/`、`scripts/`、`database/`、`docs/`
- 后端：Ktor 应用可运行，已提供基础 API 形态（见下方 Day 5/6）
- 前端：最小页面 `frontend/src/App.tsx`，尚无业务/API 集成（仅展示 `VITE_API_BASE_URL` 占位）
- 配置：`/.env.example`（MySQL/后端端口/前端 `VITE_API_BASE_URL`/AI 占位）
- 文档：`README.md`（产品与技术说明）、`docs/architecture.md`（简要架构意图）

### Day 2 — Docker Compose + MySQL（已完成）

- `docker-compose.yml`：`mysql:8.0`，命名卷 `mysql_data`，主机端口 `${DB_PORT:-3306}:3306`，**healthcheck**（`mysqladmin ping`），首次初始化挂载 `database/schema.sql` → `/docker-entrypoint-initdb.d/01-schema.sql`
- `database/schema.sql`：占位表 **users, target_companies, job_leads, applications, interviews, ai_interview_plans, prep_tasks, reminders**（`USE \`careerpilot\``，须与 `MYSQL_DATABASE` / `DB_NAME` 一致）
- `.env.example`：`MYSQL_ROOT_PASSWORD`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`

### Day 3 — 正式数据库 Schema（已完成）

- `database/schema.sql`：9 张表（users/target_companies/job_leads/applications/interviews/ai_interview_plans/prep_tasks/reminders/app_settings），BIGINT 主键、FK、`created_at`/`updated_at`，JSON 列用于关键词/地点等
- 索引满足需求：`target_companies.active`、`job_leads.company_id`、`job_leads.discovered_at`、`applications.status`、`prep_tasks.due_date`、`reminders.due_date`
- `database/seed.sql`：1 个 demo 用户、2 个 target companies、3 条 job leads
- `docs/database-schema.md`：逐表说明与字段约定

### Day 5 — 后端接入 MySQL（已完成）

- 后端已接入 **HikariCP + MySQL JDBC**：`backend/src/main/kotlin/com/careerpilot/db/DatabaseModule.kt`
- `DB_*` 环境变量读取与可选 `DB_JDBC_URL` 覆盖（测试用）
- `GET /health/db`：最小 DB 连通性验证（SELECT 1），失败时返回 `503` 且给出归类后的错误码

### Day 6 — 基础认证（JWT）（已完成）

- 认证接口：
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /api/me`（Bearer JWT）
- BCrypt 密码哈希：`backend/src/main/kotlin/com/careerpilot/auth/PasswordHasher.kt`
- JWT 必要配置：`JWT_SECRET` **必须显式配置**（为空或默认 `change-me...` 将拒绝启动）

### 未实现 / 占位（业务与基础设施）

- **无**真实领域模型 API（CRUD/业务规则）
- **无**后端迁移工具（Flyway/Liquibase）；当前策略为外部初始化（Compose MySQL init 脚本）
- **无** Compose 中的 backend / frontend 服务（目前 Compose 仅 MySQL）
- **无** Python 抓取/定时等脚本逻辑
- **无** AI 提供商集成与 mock 模式的具体实现（`.env.example` 仅预留变量）

### 最近一次会话交接（模板：每次收尾覆写本小节）

- **日期**：2026-05-01
- **本次完成**：文档与代码对齐：确认后端已具备 Day 5（MySQL 连接池 + `/health/db`）与 Day 6（JWT 注册/登录/Me）最小闭环，并更新 `PROJECT_CONTEXT.md` 的进度/缺口/下一步
- **未完成 / 阻塞**：
  - Compose 尚未加入 backend/frontend 服务；`DB_HOST` 在“宿主机跑后端”和“容器内跑后端”两种模式下取值不同（`localhost` vs `mysql`）
  - 领域模型（companies/leads/applications 等）仍未落地 API
- **关键路径 / 涉及文件**：`PROJECT_CONTEXT.md`, `backend/src/main/kotlin/com/careerpilot/Application.kt`, `backend/src/main/kotlin/com/careerpilot/db/DatabaseModule.kt`, `backend/src/main/kotlin/com/careerpilot/auth/*`, `docker-compose.yml`, `.env.example`
- **已运行验证**：代码内已有后端测试覆盖 auth 流程（H2, MySQL mode）；DB 健康检查失败时应返回 `503`
- **给下一对话的一句话**：优先落地第一批领域 API（例如 `target_companies` / `job_leads` 列表与创建），并把 backend 作为 Compose 服务接到 MySQL。

---

## 5. 重要约定（开发时必须遵守）

1. **秘钥**：不得硬编码；使用 `.env` + `.env.example` 占位说明。
2. **数据来源**：仅存取用户配置的公开招聘页；禁止针对 LinkedIn/Indeed/Glassdoor 等站内抓取策略。
3. **架构**：优先清晰分层（API、领域、持久化、集成），避免大泥球。
4. **接口契约**：后端响应宜统一、可序列化类型；前端对 API 响应使用 TypeScript 类型对齐。
5. **脚本**：Python 自动化在有意义时提供 `--dry-run`；不写死密钥。
6. **AI**：同时支持真实 API 与本地 **mock**，便于离线/CI。
7. **测试与验证**：新增功能应有最小测试或可重复的 `curl`/文档步骤。
8. **变更范围**：只改当前任务需要的文件；未要求不要批量「顺手重构」无关模块。

---

## 6. 本地快速验证（确认环境没坏）

**后端测试**（无本地 Gradle 时可用容器）：

```bash
cd careerpilot-local/backend
docker run --rm -v "$PWD":/app -w /app gradle:8.10.2-jdk21 gradle test --no-daemon
```

**前端类型检查**：

```bash
cd careerpilot-local/frontend
npm install
npm run typecheck
```

**手工看后端是否起来**（若已 `gradle run` 且端口与 `application.conf` 一致）：访问 `GET /api/scaffold`。

**MySQL（Docker Compose）**：

```bash
cd careerpilot-local
cp .env.example .env
docker compose up -d
docker compose exec mysql mysql -u careerpilot -pcareerpilot_password careerpilot -e "SHOW TABLES;"
```

---

## 7. 建议的下一步任务（可按优先级推进）

以下顺序可按产品节奏调整，供新对话直接 pick：

1. **后端接 MySQL**：JDBC/Hikari/Exposed 或 JDBI；读 `DB_*`；健康检查 + 最小迁移策略。
2. **Compose 扩展（可选）**：加入 `backend`（依赖 `mysql` healthy），后续再加 `frontend`。
3. **API**：首条业务接口（如 `target_companies` 或 `job_leads` 列表）；统一 API 响应类型。
4. **前端**：`VITE_API_BASE_URL`、fetch 封装、与后端类型对齐的首屏。
5. **Python**：公开页拉取/解析占位 CLI，`--dry-run`，明确禁止域名列表。
6. **AI**：抽象 `AI_MODE=mock|real`，mock 返回固定结构，real 调环境变量中的 HTTP 端点。

---

## 8. Cursor 会话接力：每日收尾时如何「自动」更新本文件

### 8.1 现实限制（必读）

Cursor **无法在后台默默监视**你每天何时写完代码；所谓「自动化」在项目里的落地方式是：

- **仓库规则** `.cursor/rules/careerpilot-project-context-handoff.mdc` 已开启 `alwaysApply: true`，模型在会话里会看到「收尾时要改 `PROJECT_CONTEXT.md`」的义务。
- 你在 **同一天收工时必须发一条口令**（或粘贴下方收尾指令）。推荐 **固定用同一句**：**「今天的任务结束了」**。  
→ 随后在**同一会话或新会话**开头说一次 **「请先读 PROJECT_CONTEXT.md」**（见 §9），即可衔接。

若你希望更强约束，可把「今天的任务结束了」粘在 Composer/Agent **独立一条消息**末尾发送，以减少模型漏执行文件更新的概率。

### 8.2 收尾口令示例（任选其一）

- 「今天的任务结束了」
- 「今日收工」「结束今天」「今天先到这里」「EOD」「更新交接文档」「更新 PROJECT_CONTEXT」

### 8.3 收尾时请 Cursor 执行的「命令」（复制到对话框即可）

下面这些不是 Shell 指令，是给 **Cursor Agent / Chat** 的自然语言指令，用于触发对 **本 Markdown 文件的更新**：

```text
今天的任务结束了。请严格执行 .cursor/rules/careerpilot-project-context-handoff.mdc 与 PROJECT_CONTEXT.md §8：
1) 根据今天实际改动与仓库现状更新 PROJECT_CONTEXT.md：§4 进度列表、§7 下一步清单、§4 小节「最近一次会话交接」；
2) 不要编造未完成的功能；不清楚处标「待核对」；
3) 若改了对外行为，检查 README/.env.example 是否需一并说明（只做必要的最小补充）。
```

规则文件：**`careerpilot-local/.cursor/rules/careerpilot-project-context-handoff.mdc`**（已向 Cursor 设为 `alwaysApply: true`）。

### 8.4 收尾时 Assistant 必须在 `PROJECT_CONTEXT.md` 内更新的块

| 更新块 | 内容 |
|--------|------|
| §4 上文列表 | 「已完成 / 未实现」与真实代码一致；删减已完成占位描述 |
| §4「最近一次会话交接」 | **覆写**：日期 + 摘要 + 阻塞 + 关键路径 + 验证情况 + 一句话接力 |
| §7 | 下一步：勾掉已完成，追加明日事项；保留优先级 |
| 其他 | 仅在栈/部署/密钥约定变动时改写 §2、§5、§6 |

### 8.5 不推荐的做法

- 依赖大脑记忆而不改文档 → 新开对话极易丢上下文。
- 把 `.env` 真值贴进文档 → **禁止**，只能写占位名或变量名。
- 在另一台机器/分支未 pulled 前就覆写整块 §4 → 应先 `git pull` 或写明「基于分支 X、commit Y」（可选）。

---

## 9. 新对话推荐开场白（复制给 Cursor）

```
请阅读 careerpilot-local/PROJECT_CONTEXT.md、careerpilot-local/README.md，
并浏览与当前任务相关的目录（例如 backend/ 或 frontend/）。
在遵守 PROJECT_CONTEXT 中「重要约定」的前提下，我们要实现：<你的具体任务>。
```

同一对话内需更新文档时：**「请先按 §8 更新 PROJECT_CONTEXT.md，然后再继续。」**

---

## 10. 文档维护（长期）

- 重大架构、栈或里程碑变化时，**同步更新**本文件与 `README.md`。
- 若某功能已落地，将「当前完成进度」与「下一步任务」相应改版；**每天至少通过 §8 收尾一次**，避免与新对话脱节。
