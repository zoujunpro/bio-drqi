-- AI 配置后台菜单初始化/迁移脚本。
-- 适配当前代码中的 system_menu_tb 字段：
--   parent_id, system_id, front_permission_code, menu_name, component_path,
--   menu_type, menu_status, menu_icon, order_num, create_time, update_time, back_permission_code
--
-- 作用：
-- 1. 确保后台管理(system_id=1)存在 AI管理 根菜单；
-- 2. 删除旧 AI 子菜单；
-- 3. 插入新的配置驱动菜单；
-- 4. 把拥有旧 AI 菜单权限的角色同步授权到新菜单。

SET @admin_system_id := 1;

INSERT INTO system_menu_tb (
    parent_id, system_id, front_permission_code, menu_name, component_path,
    menu_type, menu_status, menu_icon, order_num, create_time, update_time, back_permission_code
)
SELECT 0, @admin_system_id, '/admin/ai', 'AI管理', '/admin/ai',
       '1', 'Y', 'Setting', 9, NOW(), NOW(), NULL
WHERE NOT EXISTS (
    SELECT 1
    FROM system_menu_tb
    WHERE system_id = @admin_system_id
      AND parent_id = 0
      AND menu_name IN ('AI管理', 'AI配置')
);

UPDATE system_menu_tb
SET menu_name = 'AI管理',
    front_permission_code = '/admin/ai',
    component_path = '/admin/ai',
    menu_type = '1',
    menu_status = 'Y',
    menu_icon = 'Setting',
    order_num = 9,
    update_time = NOW()
WHERE system_id = @admin_system_id
  AND parent_id = 0
  AND menu_name IN ('AI管理', 'AI配置');

SELECT @ai_root_id := id
FROM system_menu_tb
WHERE system_id = @admin_system_id
  AND parent_id = 0
  AND menu_name = 'AI管理'
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
      OR menu.component_path IN (
          '/admin/ai/terms',
          '/admin/ai/intentKeywords',
          '/admin/ai/audit',
          '/admin/ai/prompts',
          '/admin/ai/apis',
          '/admin/ai/apiParams',
          '/admin/ai/workflows'
      )
      OR menu.front_permission_code IN (
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
      menu_name IN ('业务术语', '意图关键词', 'Prompt管理', 'API注册', 'API参数', 'Workflow编排')
      OR component_path IN (
          '/admin/ai/terms',
          '/admin/ai/intentKeywords',
          '/admin/ai/prompts',
          '/admin/ai/apis',
          '/admin/ai/apiParams',
          '/admin/ai/workflows'
      )
      OR front_permission_code IN (
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
      menu_name IN ('业务术语', '意图关键词', 'Prompt管理', 'API注册', 'API参数', 'Workflow编排')
      OR component_path IN (
          '/admin/ai/terms',
          '/admin/ai/intentKeywords',
          '/admin/ai/prompts',
          '/admin/ai/apis',
          '/admin/ai/apiParams',
          '/admin/ai/workflows'
      )
      OR front_permission_code IN (
          '/admin/ai/terms',
          '/admin/ai/intentKeywords',
          '/admin/ai/prompts',
          '/admin/ai/apis',
          '/admin/ai/apiParams',
          '/admin/ai/workflows'
      )
  );

INSERT INTO system_menu_tb (
    parent_id, system_id, front_permission_code, menu_name, component_path,
    menu_type, menu_status, menu_icon, order_num, create_time, update_time, back_permission_code
)
SELECT @ai_root_id, @admin_system_id, menu.path, menu.name, menu.path,
       '1', 'Y', menu.icon, menu.order_num, NOW(), NOW(), NULL
FROM (
    SELECT '/admin/ai/tools' AS path, '工具定义' AS name, 'Operation' AS icon, 1 AS order_num
    UNION ALL SELECT '/admin/ai/intents', '意图管理', 'Tickets', 2
    UNION ALL SELECT '/admin/ai/intentExamples', '意图样例', 'Chat', 3
    UNION ALL SELECT '/admin/ai/intentTools', '意图工具', 'Connection', 4
    UNION ALL SELECT '/admin/ai/taskTemplates', '任务模板', 'Document', 5
    UNION ALL SELECT '/admin/ai/taskTemplateSteps', '模板步骤', 'List', 6
    UNION ALL SELECT '/admin/ai/dictionaries', '业务词典', 'Collection', 7
    UNION ALL SELECT '/admin/ai/semanticPatterns', '语义规则', 'Aim', 8
    UNION ALL SELECT '/admin/ai/audit', '查询审计', 'Monitor', 9
) menu
WHERE NOT EXISTS (
    SELECT 1
    FROM system_menu_tb exists_menu
    WHERE exists_menu.system_id = @admin_system_id
      AND exists_menu.component_path = menu.path
);

UPDATE system_menu_tb child
JOIN (
    SELECT '/admin/ai/tools' AS path, '工具定义' AS name, 'Operation' AS icon, 1 AS order_num
    UNION ALL SELECT '/admin/ai/intents', '意图管理', 'Tickets', 2
    UNION ALL SELECT '/admin/ai/intentExamples', '意图样例', 'Chat', 3
    UNION ALL SELECT '/admin/ai/intentTools', '意图工具', 'Connection', 4
    UNION ALL SELECT '/admin/ai/taskTemplates', '任务模板', 'Document', 5
    UNION ALL SELECT '/admin/ai/taskTemplateSteps', '模板步骤', 'List', 6
    UNION ALL SELECT '/admin/ai/dictionaries', '业务词典', 'Collection', 7
    UNION ALL SELECT '/admin/ai/semanticPatterns', '语义规则', 'Aim', 8
    UNION ALL SELECT '/admin/ai/audit', '查询审计', 'Monitor', 9
) menu ON menu.path = child.component_path
SET child.parent_id = @ai_root_id,
    child.system_id = @admin_system_id,
    child.front_permission_code = menu.path,
    child.menu_name = menu.name,
    child.component_path = menu.path,
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
