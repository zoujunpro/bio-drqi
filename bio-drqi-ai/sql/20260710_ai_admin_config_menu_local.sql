-- AI 配置后台菜单初始化/迁移脚本，本地库旧菜单表结构版本。
-- 适配字段：
--   parent_id, system_id, permission_code, menu_name, component_path,
--   menu_type, menu_status, menu_icon, order_num, create_time, update_time, router_path

SET @admin_system_id := 1;

INSERT INTO system_menu_tb (
    parent_id, system_id, permission_code, menu_name, component_path,
    menu_type, menu_status, menu_icon, order_num, create_time, update_time, router_path
)
SELECT 0, @admin_system_id, '/admin/ai', 'AI管理', 'admin/ai/index',
       '1', 'Y', 'Setting', 9, NOW(), NOW(), '/admin/ai'
WHERE NOT EXISTS (
    SELECT 1
    FROM system_menu_tb
    WHERE system_id = @admin_system_id
      AND parent_id = 0
      AND (permission_code = '/admin/ai' OR router_path = '/admin/ai' OR component_path = 'admin/ai/index')
);

UPDATE system_menu_tb
SET menu_name = 'AI管理',
    permission_code = '/admin/ai',
    component_path = 'admin/ai/index',
    router_path = '/admin/ai',
    menu_type = '1',
    menu_status = 'Y',
    menu_icon = 'Setting',
    order_num = 9,
    update_time = NOW()
WHERE system_id = @admin_system_id
  AND parent_id = 0
  AND (menu_name IN ('AI管理', 'AI配置') OR permission_code = '/admin/ai' OR router_path = '/admin/ai');

SELECT @ai_root_id := id
FROM system_menu_tb
WHERE system_id = @admin_system_id
  AND parent_id = 0
  AND (permission_code = '/admin/ai' OR router_path = '/admin/ai' OR component_path = 'admin/ai/index')
ORDER BY id
LIMIT 1;

CREATE TEMPORARY TABLE IF NOT EXISTS tmp_ai_admin_role_ids (
    role_id INT PRIMARY KEY
);

INSERT IGNORE INTO tmp_ai_admin_role_ids(role_id)
SELECT DISTINCT ref.role_id
FROM system_role_menu_ref ref
JOIN system_menu_tb menu ON menu.id = ref.menu_id
WHERE menu.system_id = @admin_system_id
  AND (
      menu.id = @ai_root_id
      OR menu.parent_id = @ai_root_id
      OR menu.router_path IN (
          '/admin/ai/chat',
          '/admin/ai/terms',
          '/admin/ai/intentKeywords',
          '/admin/ai/audit',
          '/admin/ai/prompts',
          '/admin/ai/apis',
          '/admin/ai/apiParams',
          '/admin/ai/workflows'
      )
      OR menu.permission_code IN (
          '/admin/ai/chat',
          '/admin/ai/terms',
          '/admin/ai/intentKeywords',
          '/admin/ai/audit',
          '/admin/ai/prompts',
          '/admin/ai/apis',
          '/admin/ai/apiParams',
          '/admin/ai/workflows'
      )
  );

DELETE ref
FROM system_role_menu_ref ref
JOIN system_menu_tb menu ON menu.id = ref.menu_id
WHERE menu.system_id = @admin_system_id
  AND menu.parent_id = @ai_root_id
  AND (
      menu_name IN ('智能查询', '业务术语', '意图关键词', 'Prompt管理', 'API注册', 'API参数', 'Workflow编排')
      OR router_path IN (
          '/admin/ai/chat',
          '/admin/ai/terms',
          '/admin/ai/intentKeywords',
          '/admin/ai/prompts',
          '/admin/ai/apis',
          '/admin/ai/apiParams',
          '/admin/ai/workflows'
      )
      OR permission_code IN (
          '/admin/ai/chat',
          '/admin/ai/terms',
          '/admin/ai/intentKeywords',
          '/admin/ai/prompts',
          '/admin/ai/apis',
          '/admin/ai/apiParams',
          '/admin/ai/workflows'
      )
  );

DELETE FROM system_menu_tb
WHERE system_id = @admin_system_id
  AND parent_id = @ai_root_id
  AND (
      menu_name IN ('智能查询', '业务术语', '意图关键词', 'Prompt管理', 'API注册', 'API参数', 'Workflow编排')
      OR router_path IN (
          '/admin/ai/chat',
          '/admin/ai/terms',
          '/admin/ai/intentKeywords',
          '/admin/ai/prompts',
          '/admin/ai/apis',
          '/admin/ai/apiParams',
          '/admin/ai/workflows'
      )
      OR permission_code IN (
          '/admin/ai/chat',
          '/admin/ai/terms',
          '/admin/ai/intentKeywords',
          '/admin/ai/prompts',
          '/admin/ai/apis',
          '/admin/ai/apiParams',
          '/admin/ai/workflows'
      )
  );

INSERT INTO system_menu_tb (
    parent_id, system_id, permission_code, menu_name, component_path,
    menu_type, menu_status, menu_icon, order_num, create_time, update_time, router_path
)
SELECT @ai_root_id, @admin_system_id, menu.path, menu.name, menu.component,
       '1', 'Y', menu.icon, menu.order_num, NOW(), NOW(), menu.path
FROM (
    SELECT '/admin/ai/tools' AS path, '工具定义' AS name, 'admin/ai/tools/index' AS component, 'Setting' AS icon, 1 AS order_num
    UNION ALL SELECT '/admin/ai/intents', '意图管理', 'admin/ai/intents/index', 'Tickets', 2
    UNION ALL SELECT '/admin/ai/intentExamples', '意图样例', 'admin/ai/intentExamples/index', 'Chat', 3
    UNION ALL SELECT '/admin/ai/intentTools', '意图工具', 'admin/ai/intentTools/index', 'Link', 4
    UNION ALL SELECT '/admin/ai/taskTemplates', '任务模板', 'admin/ai/taskTemplates/index', 'Document', 5
    UNION ALL SELECT '/admin/ai/taskTemplateSteps', '模板步骤', 'admin/ai/taskTemplateSteps/index', 'List', 6
    UNION ALL SELECT '/admin/ai/dictionaries', '业务词典', 'admin/ai/dictionaries/index', 'Data', 7
    UNION ALL SELECT '/admin/ai/semanticPatterns', '语义规则', 'admin/ai/semanticPatterns/index', 'Aim', 8
    UNION ALL SELECT '/admin/ai/audit', '查询审计', 'admin/ai/audit/index', 'Monitor', 9
) menu
WHERE NOT EXISTS (
    SELECT 1
    FROM system_menu_tb exists_menu
    WHERE exists_menu.system_id = @admin_system_id
      AND (exists_menu.permission_code = menu.path OR exists_menu.router_path = menu.path)
);

UPDATE system_menu_tb child
JOIN (
    SELECT '/admin/ai/tools' AS path, '工具定义' AS name, 'admin/ai/tools/index' AS component, 'Setting' AS icon, 1 AS order_num
    UNION ALL SELECT '/admin/ai/intents', '意图管理', 'admin/ai/intents/index', 'Tickets', 2
    UNION ALL SELECT '/admin/ai/intentExamples', '意图样例', 'admin/ai/intentExamples/index', 'Chat', 3
    UNION ALL SELECT '/admin/ai/intentTools', '意图工具', 'admin/ai/intentTools/index', 'Link', 4
    UNION ALL SELECT '/admin/ai/taskTemplates', '任务模板', 'admin/ai/taskTemplates/index', 'Document', 5
    UNION ALL SELECT '/admin/ai/taskTemplateSteps', '模板步骤', 'admin/ai/taskTemplateSteps/index', 'List', 6
    UNION ALL SELECT '/admin/ai/dictionaries', '业务词典', 'admin/ai/dictionaries/index', 'Data', 7
    UNION ALL SELECT '/admin/ai/semanticPatterns', '语义规则', 'admin/ai/semanticPatterns/index', 'Aim', 8
    UNION ALL SELECT '/admin/ai/audit', '查询审计', 'admin/ai/audit/index', 'Monitor', 9
) menu ON menu.path = child.permission_code OR menu.path = child.router_path
SET child.parent_id = @ai_root_id,
    child.system_id = @admin_system_id,
    child.permission_code = menu.path,
    child.menu_name = menu.name,
    child.component_path = menu.component,
    child.router_path = menu.path,
    child.menu_type = '1',
    child.menu_status = 'Y',
    child.menu_icon = menu.icon,
    child.order_num = menu.order_num,
    child.update_time = NOW()
WHERE child.system_id = @admin_system_id;

INSERT INTO system_role_menu_ref(role_id, menu_id)
SELECT role.role_id, menu.id
FROM tmp_ai_admin_role_ids role
JOIN system_menu_tb menu
  ON menu.system_id = @admin_system_id
 AND (menu.id = @ai_root_id OR menu.parent_id = @ai_root_id)
 AND menu.menu_status = 'Y'
WHERE NOT EXISTS (
    SELECT 1
    FROM system_role_menu_ref exists_ref
    WHERE exists_ref.role_id = role.role_id
      AND exists_ref.menu_id = menu.id
);

DROP TEMPORARY TABLE IF EXISTS tmp_ai_admin_role_ids;
