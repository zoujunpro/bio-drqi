# bio-drqi-ai 模块说明

`bio-drqi-ai` 已按未来服务边界拆成聚合 Maven 模块。当前仍然只启动一个 `bio-drqi-ai-app` 服务，运行时通过 jar 依赖把各能力模块组装在一起；后续如果需要微服务化，可以逐个模块增加启动类和远程接口后独立部署。

## 模块边界

- `bio-drqi-ai-api`
  - DTO、Schema、Entity、异常、通用工具、AI 配置属性、LLM 客户端接口。
  - 后续可作为其他 AI 子服务共同依赖的契约包。

- `bio-drqi-ai-provider`
  - 模型供应商适配层。
  - 当前包含 OpenAI Compatible 协议实现，后续可继续增加 AGC、Dify、Coze、Spring AI 等适配器。

- `bio-drqi-ai-semantic`
  - AI 语义层。
  - 包含业务域注册、数据库业务域自动加载、业务术语、意图关键词、意图路由等能力。

- `bio-drqi-ai-tool`
  - AI 工具调用层。
  - 包含命令式工具选择、工具参数校验、已有业务接口调用、项目/流程工具等能力。

- `bio-drqi-ai-query`
  - AI 查询层。
  - 包含查询计划生成、查询计划校验、风险检查、权限条件注入、SQL 执行、图表/表格结果、报表导出等能力。

- `bio-drqi-ai-memory`
  - AI 记忆层。
  - 包含短期会话上下文、聊天记录、结果摘要、结果快照、LLM 缓存、会话清理任务等能力。

- `bio-drqi-ai-observe`
  - AI 观测层。
  - 包含请求审计、查询日志、失败记录兜底等能力。

- `bio-drqi-ai-orchestrator`
  - AI 编排层。
  - 负责编排会话、意图、语义、工具、查询、记忆、审计等模块，决定一次自然语言请求应该走聊天、业务查询、工具调用、导出还是澄清。

- `bio-drqi-ai-gateway`
  - AI 网关入口层。
  - 包含 Controller、CORS 等 HTTP 入口能力。
  - 后续可以继续扩展鉴权、限流、入口审计、灰度、租户上下文、请求追踪等能力，也可以独立迁移为 AI Gateway 服务。

- `bio-drqi-ai-app`
  - 可运行 Spring Boot 应用。
  - 包含启动类、bootstrap 配置、静态测试页面和 prompt 资源。

## 构建

```bash
mvn -pl bio-drqi-ai/bio-drqi-ai-app -am clean package -DskipTests
```

构建产物：

```text
bio-drqi-ai/bio-drqi-ai-app/target/bio-drqi-ai-1.0-SNAPSHOT.jar
```
