# 表到 ES 同步

代码位置：`bio-drqi-es` 模块。

## 功能
- 保留一个同步入口：按表名同步。
- 入参表名后，自动：
  1. 读取 MySQL 表字段
  2. 生成 ES 索引 mapping（若索引不存在则创建）
  3. 全量查询该表并入库到 ES

## 配置
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
    hosts:
      - http://127.0.0.1:9200
    username: elastic
    password: changeme
```

## API
```bash
curl -X POST "http://127.0.0.1:8080/es/sync/table" \
  -H "Content-Type: application/json" \
  -d '{"tableName":"bio_cer.seed_stock_tb","idField":"id"}'
```

`tableName` 支持：
- `database.table`
- `table`（默认使用当前数据库）

## 本地执行
启动：

```bash
mvn -pl bio-drqi-es -DskipTests spring-boot:run -Dspring-boot.run.profiles=local
```

调用同步：

```bash
curl -X POST "http://127.0.0.1:8099/es/sync/table" \
  -H "Content-Type: application/json" \
  -d '{"tableName":"bio_cer.seed_stock_tb","idField":"id"}'
```
