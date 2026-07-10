-- 后台菜单层级调整：
-- 1. 打印管理 -> 配置管理 提升为一级菜单“打印配置”
-- 2. 系统日志 -> 更新日志 提升为一级菜单“更新日志”
-- 3. 同步原父菜单角色授权到提升后的一级菜单，避免点击 403

SET @admin_system_id := 1;
SET @print_parent_id := NULL;
SET @print_config_id := NULL;
SET @log_parent_id := NULL;
SET @log_list_id := NULL;

SELECT @print_parent_id := id
FROM system_menu_tb
WHERE system_id = @admin_system_id
  AND menu_type = '1'
  AND (
      router_path = '/admin/print'
      OR permission_code = '/admin/print'
      OR menu_name = '打印管理'
  )
ORDER BY id
LIMIT 1;

SELECT @print_config_id := id
FROM system_menu_tb
WHERE system_id = @admin_system_id
  AND menu_type = '1'
  AND (
      router_path = '/admin/print/printConfig'
      OR permission_code = '/admin/print/printConfig'
      OR component_path = '/admin/print/printConfig'
      OR menu_name = '配置管理'
  )
ORDER BY CASE WHEN parent_id = @print_parent_id THEN 0 ELSE 1 END, id
LIMIT 1;

UPDATE system_menu_tb
SET parent_id = 0,
    permission_code = '/admin/printConfig',
    menu_name = '打印配置',
    component_path = '/admin/printConfig',
    menu_type = '1',
    menu_status = 'Y',
    menu_icon = COALESCE(menu_icon, 'Document'),
    order_num = 5,
    router_path = '/admin/printConfig',
    update_time = NOW()
WHERE id = @print_config_id;

INSERT INTO system_role_menu_ref (role_id, menu_id)
SELECT ref.role_id, @print_config_id
FROM system_role_menu_ref ref
LEFT JOIN system_role_menu_ref exists_ref
  ON exists_ref.role_id = ref.role_id
 AND exists_ref.menu_id = @print_config_id
WHERE ref.menu_id = @print_parent_id
  AND @print_config_id IS NOT NULL
  AND exists_ref.id IS NULL;

UPDATE system_menu_tb
SET menu_status = 'N',
    update_time = NOW()
WHERE id = @print_parent_id
  AND @print_parent_id <> @print_config_id;

SELECT @log_parent_id := id
FROM system_menu_tb
WHERE system_id = @admin_system_id
  AND menu_type = '1'
  AND (
      router_path = '/admin/logSystem'
      OR permission_code = '/admin/logSystem'
      OR menu_name = '系统日志'
  )
ORDER BY id
LIMIT 1;

SELECT @log_list_id := id
FROM system_menu_tb
WHERE system_id = @admin_system_id
  AND menu_type = '1'
  AND (
      router_path = '/admin/logSystem/logList'
      OR permission_code = '/admin/logSystem/logList'
      OR component_path = '/admin/logSystem/logList'
      OR menu_name IN ('日志列表', '更新日志')
  )
ORDER BY CASE WHEN parent_id = @log_parent_id THEN 0 ELSE 1 END, id
LIMIT 1;

UPDATE system_menu_tb
SET parent_id = 0,
    permission_code = '/admin/logList',
    menu_name = '更新日志',
    component_path = '/admin/logList',
    menu_type = '1',
    menu_status = 'Y',
    menu_icon = COALESCE(menu_icon, 'Document'),
    order_num = 8,
    router_path = '/admin/logList',
    update_time = NOW()
WHERE id = @log_list_id;

INSERT INTO system_role_menu_ref (role_id, menu_id)
SELECT ref.role_id, @log_list_id
FROM system_role_menu_ref ref
LEFT JOIN system_role_menu_ref exists_ref
  ON exists_ref.role_id = ref.role_id
 AND exists_ref.menu_id = @log_list_id
WHERE ref.menu_id = @log_parent_id
  AND @log_list_id IS NOT NULL
  AND exists_ref.id IS NULL;

UPDATE system_menu_tb
SET menu_status = 'N',
    update_time = NOW()
WHERE id = @log_parent_id
  AND @log_parent_id <> @log_list_id;
