# Canal + Kafka + ES 增量同步

代码位置：`bio-drqi-es` 模块。

## 1. 功能说明
- 支持两阶段同步：
  - 全量：通过 API 手动触发，按配置表从 MySQL 拉历史数据写入 ES
  - 增量：服务启动后消费 Canal Kafka binlog 实时写 ES
- 增量规则：`INSERT/UPDATE` -> `index`，`DELETE` -> `delete`。
- 通过业务主键字段映射到 ES `_id`，实现幂等覆盖。

## 2. 开关与配置
在 Nacos 或应用配置中增加：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/bio_cer?useUnicode=true&characterEncoding=utf-8
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver

sync:
  es:
    enabled: true
    realtime-enabled: true
    realtime-auto-start: false
    topic: canal_binlog
    group-id: bio-drqi-es-sync
    hosts:
      - http://127.0.0.1:9200
    username: elastic
    password: changeme
    full:
      enabled: true
      batch-size: 1000
    table-rules:
      bio_cer.seed_stock_tb:
        index: seed_stock
        id-field: id
        source-table: bio_cer.seed_stock_tb
        where-clause: "id > 0"
        full-sync: true
      bio_cer.cer_vector_tb:
        index: cer_vector
        id-field: id
        source-table: bio_cer.cer_vector_tb
        full-sync: true
```

说明：
- `table-rules` 的 key 为 `database.table`。
- 未配置规则的表会被忽略，不会写入 ES。
- 全量不在启动时自动执行，必须通过 API 手动触发。
- `realtime-auto-start=false` 时，增量监听器不会自动启动，需要调用 API 手动开启。

## 3. 全量同步 API

同步所有配置规则：

```bash
curl -X POST "http://127.0.0.1:8080/es/sync/full/all"
```

按指定规则同步（`ruleKeys` 为 `database.table`）：

```bash
curl -X POST "http://127.0.0.1:8080/es/sync/full" \
  -H "Content-Type: application/json" \
  -d '{"ruleKeys":["bio_cer.seed_stock_tb","bio_cer.cer_vector_tb"]}'
```

启动增量监听（建议在首次全量成功后调用）：

```bash
curl -X POST "http://127.0.0.1:8080/es/sync/realtime/start"
```

## 4. 消息格式
支持两种 Canal Kafka 消息：
- 单条对象
- 数组（批量）

最小字段示例：

```json
{
  "database":"bio_cer",
  "table":"seed_stock_tb",
  "type":"UPDATE",
  "isDdl":false,
  "data":[{"id":1,"seed_num":"S0001"}]
}
```

## 5. 上线建议
- 先做全量导入，再开启增量消费。
- 关注 Kafka lag、ES bulk failure、消费重试次数。
- 对关键表做抽样对账，验证 MySQL 与 ES 一致性。
