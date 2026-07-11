insert into ai_tool_definition
(tool_code, tool_name, description, tool_type, target_code, input_schema, output_schema, service_url, http_method, risk_level, read_only, status)
values
('query_implementation_execution_detail', '查询实施方案执行详情',
 '根据实施方案编码查询完整执行详情，包括当前执行步骤、转化取样检测明细、收获种子明细，并返回概要和明细数据。',
 'API', 'query_implementation_execution_detail',
 '{"type":"object","properties":{"vectorTaskCode":{"type":"string","description":"实施方案编码，例如 CR00101-01"},"keyword":{"type":"string","description":"实施方案编码或关键词"}},"required":["vectorTaskCode"]}',
 '{"type":"object","properties":{"resultType":{"type":"string"},"answer":{"type":"string"},"summary":{"type":"string"},"table":{"type":"object"},"data":{"type":"object","properties":{"summary":{"type":"object"},"planInfo":{"type":"object"},"transformSampleSeedList":{"type":"array"}}}}}',
 '/ai/tool/manage/implementation-plan/execution-detail', 'POST', 'LOW', 1, 'ACTIVE')
on duplicate key update
tool_name = values(tool_name),
description = values(description),
tool_type = values(tool_type),
target_code = values(target_code),
input_schema = values(input_schema),
output_schema = values(output_schema),
service_url = values(service_url),
http_method = values(http_method),
risk_level = values(risk_level),
read_only = values(read_only),
status = values(status);

insert into ai_intent
(intent_code, intent_name, domain, description, handler_type, status)
values
('intent_query_implementation_execution_detail', '查询实施方案执行详情', 'CER业务',
 '根据实施方案编码查询当前执行步骤、取样检测情况、收获种子情况和相关明细。',
 'TOOL', 'ACTIVE')
on duplicate key update
intent_name = values(intent_name),
domain = values(domain),
description = values(description),
handler_type = values(handler_type),
status = values(status);

insert into ai_intent_tool_rel
(intent_code, tool_code, priority, status)
values
('intent_query_implementation_execution_detail', 'query_implementation_execution_detail', 1, 'ACTIVE'),
('intent_query_vector_task_detail', 'query_implementation_execution_detail', 1, 'ACTIVE')
on duplicate key update
priority = values(priority),
status = values(status);

insert into ai_intent_example
(intent_code, example_text, status)
values
('intent_query_implementation_execution_detail', '帮我查看实施方案CR00101-01的执行情况', 'ACTIVE'),
('intent_query_implementation_execution_detail', 'CR00101-01取样了没有，取样了多少', 'ACTIVE'),
('intent_query_implementation_execution_detail', 'CR00101-01有没有收获，收获了多少种子', 'ACTIVE'),
('intent_query_implementation_execution_detail', '查看CR00101-01的取样和收获明细', 'ACTIVE'),
('intent_query_implementation_execution_detail', '实施方案CR00101-01执行到哪一步了', 'ACTIVE');

insert into ai_semantic_pattern
(pattern_type, pattern_code, pattern_text, target_value, weight, domain, status)
values
('INTENT_KEYWORD', 'kw_implementation_execution_detail', '实施方案执行详情', 'intent_query_implementation_execution_detail', 3.0000, 'CER业务', 'ACTIVE'),
('INTENT_KEYWORD', 'kw_implementation_execution_sample_harvest', '取样 收获 种子', 'intent_query_implementation_execution_detail', 3.0000, 'CER业务', 'ACTIVE'),
('INTENT_KEYWORD', 'kw_implementation_execution_status', '实施方案执行情况', 'intent_query_implementation_execution_detail', 2.5000, 'CER业务', 'ACTIVE'),
('INTENT_KEYWORD', 'kw_implementation_current_step', '执行到哪一步', 'intent_query_implementation_execution_detail', 2.5000, 'CER业务', 'ACTIVE')
on duplicate key update
pattern_text = values(pattern_text),
target_value = values(target_value),
weight = values(weight),
domain = values(domain),
status = values(status);
