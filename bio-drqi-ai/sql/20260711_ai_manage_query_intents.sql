insert into ai_intent
(intent_code, intent_name, domain, description, handler_type, status)
values
('query_project', '查询项目', 'CER业务', '查询项目基础信息、项目状态、负责人、项目编码等信息。', 'TOOL', 'ACTIVE'),
('query_project_plan', '查询项目实施方案', 'CER业务', '查询指定项目下的实施方案，项目是实施方案的上层业务对象。', 'TOOL', 'ACTIVE'),
('query_plan_transform', '查询实施方案转化', 'CER业务', '查询指定实施方案下的转化信息，实施方案是转化任务的上层业务对象。', 'TOOL', 'ACTIVE'),
('query_plan_sample_test', '查询实施方案取样检测', 'CER业务', '查询指定实施方案下的取样检测信息，取样检测针对苗或样品进行。', 'TOOL', 'ACTIVE'),
('query_plan_plant', '查询实施方案种植', 'CER业务', '查询指定实施方案下的种植信息，苗属于种植业务过程。', 'TOOL', 'ACTIVE'),
('query_plant_seed_stock', '查询种植种子库', 'CER业务', '查询种植收获后的种子库信息，种子来自苗或植株收获。', 'TOOL', 'ACTIVE'),
('query_project_progress_chain', '查询项目全流程进展', 'CER业务', '按项目、实施方案、转化、取样检测、种植、种子库链路查询项目整体进展。', 'TOOL', 'ACTIVE'),
('query_seed_trace', '查询种子追溯', 'CER业务', '根据种子编号、种植申请号、实施方案编码或项目编码追溯种子来源链路。', 'TOOL', 'ACTIVE')
on duplicate key update
    intent_name = values(intent_name),
    domain = values(domain),
    description = values(description),
    handler_type = values(handler_type),
    status = values(status),
    update_time = current_timestamp;

insert into ai_intent_tool_rel
(intent_code, tool_code, priority, status)
values
('query_project', 'query_project_list', 10, 'ACTIVE'),

('query_project_plan', 'query_project_list', 10, 'ACTIVE'),
('query_project_plan', 'query_implementation_plan_list', 20, 'ACTIVE'),

('query_plan_transform', 'query_implementation_plan_list', 10, 'ACTIVE'),
('query_plan_transform', 'query_transform_list', 20, 'ACTIVE'),

('query_plan_sample_test', 'query_implementation_plan_list', 10, 'ACTIVE'),
('query_plan_sample_test', 'query_sample_test_list', 20, 'ACTIVE'),

('query_plan_plant', 'query_implementation_plan_list', 10, 'ACTIVE'),
('query_plan_plant', 'query_plant_list', 20, 'ACTIVE'),

('query_plant_seed_stock', 'query_plant_list', 10, 'ACTIVE'),
('query_plant_seed_stock', 'query_seed_stock_list', 20, 'ACTIVE'),

('query_project_progress_chain', 'query_project_list', 10, 'ACTIVE'),
('query_project_progress_chain', 'query_implementation_plan_list', 20, 'ACTIVE'),
('query_project_progress_chain', 'query_transform_list', 30, 'ACTIVE'),
('query_project_progress_chain', 'query_sample_test_list', 40, 'ACTIVE'),
('query_project_progress_chain', 'query_plant_list', 50, 'ACTIVE'),
('query_project_progress_chain', 'query_seed_stock_list', 60, 'ACTIVE'),

('query_seed_trace', 'query_seed_stock_list', 10, 'ACTIVE'),
('query_seed_trace', 'query_plant_list', 20, 'ACTIVE'),
('query_seed_trace', 'query_sample_test_list', 30, 'ACTIVE'),
('query_seed_trace', 'query_transform_list', 40, 'ACTIVE'),
('query_seed_trace', 'query_implementation_plan_list', 50, 'ACTIVE'),
('query_seed_trace', 'query_project_list', 60, 'ACTIVE')
on duplicate key update
    priority = values(priority),
    status = values(status),
    update_time = current_timestamp;

delete from ai_intent_example
where intent_code in (
    'query_project',
    'query_project_plan',
    'query_plan_transform',
    'query_plan_sample_test',
    'query_plan_plant',
    'query_plant_seed_stock',
    'query_project_progress_chain',
    'query_seed_trace'
);

insert into ai_intent_example
(intent_code, example_text, status)
values
('query_project', '查询项目列表', 'ACTIVE'),
('query_project', '帮我查一下项目编码为P001的项目', 'ACTIVE'),
('query_project', '查询玉米相关项目', 'ACTIVE'),

('query_project_plan', '查询这个项目下面有哪些实施方案', 'ACTIVE'),
('query_project_plan', '查看P001项目的实施方案', 'ACTIVE'),
('query_project_plan', '项目下面的试验方案有哪些', 'ACTIVE'),

('query_plan_transform', '查询这个实施方案下的转化情况', 'ACTIVE'),
('query_plan_transform', '查看实施方案VT001的转化任务', 'ACTIVE'),
('query_plan_transform', '这个方案做了哪些转化', 'ACTIVE'),

('query_plan_sample_test', '查询这个实施方案下的取样检测', 'ACTIVE'),
('query_plan_sample_test', '查看VT001方案下苗的检测结果', 'ACTIVE'),
('query_plan_sample_test', '这个方案下有哪些样品检测记录', 'ACTIVE'),

('query_plan_plant', '查询这个实施方案下的种植情况', 'ACTIVE'),
('query_plan_plant', '查看VT001方案下有哪些苗', 'ACTIVE'),
('query_plan_plant', '这个方案的种植申请有哪些', 'ACTIVE'),

('query_plant_seed_stock', '查询种植收获后的种子', 'ACTIVE'),
('query_plant_seed_stock', '查看这个苗收获后进入种子库的记录', 'ACTIVE'),
('query_plant_seed_stock', '这个种植申请产生了哪些种子', 'ACTIVE'),

('query_project_progress_chain', '查询项目全流程进展', 'ACTIVE'),
('query_project_progress_chain', '查看项目从实施方案到种子的整体情况', 'ACTIVE'),
('query_project_progress_chain', '查询项目的实施方案、转化、检测、种植和种子情况', 'ACTIVE'),

('query_seed_trace', '根据种子编号追溯来源', 'ACTIVE'),
('query_seed_trace', '查看这个种子来自哪个种植和实施方案', 'ACTIVE'),
('query_seed_trace', '追溯种子对应的项目和转化检测记录', 'ACTIVE');
