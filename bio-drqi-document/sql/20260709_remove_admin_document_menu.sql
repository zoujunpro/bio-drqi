-- 企业文档保留在独立文档入口；后台管理不再展示文档管理菜单。
-- 文档系统(system_id=11)自身菜单切到 portal 路由，历史后台 /admin/document 菜单从后台管理删除。

SET @document_system_id := 11;

UPDATE system_menu_tb
SET permission_code = REPLACE(permission_code, '/admin/document', '/portal/document'),
    component_path = REPLACE(component_path, '/admin/document', '/portal/document'),
    router_path = REPLACE(router_path, '/admin/document', '/portal/document'),
    update_time = NOW()
WHERE system_id = @document_system_id
  AND (
      permission_code LIKE '/admin/document%'
      OR component_path LIKE '/admin/document%'
      OR router_path LIKE '/admin/document%'
  );

DELETE ref
FROM system_role_menu_ref ref
JOIN system_menu_tb menu ON menu.id = ref.menu_id
WHERE menu.system_id <> @document_system_id
  AND (
      menu.permission_code LIKE '/admin/document%'
      OR menu.component_path LIKE '/admin/document%'
      OR menu.router_path LIKE '/admin/document%'
      OR menu.menu_name IN ('文档管理系统', '文档管理')
      OR menu.parent_id IN (
          SELECT parent.id
          FROM system_menu_tb parent
          WHERE parent.system_id <> @document_system_id
            AND parent.menu_name IN ('文档管理系统', '文档管理')
      )
  );

DELETE menu
FROM system_menu_tb menu
WHERE menu.system_id <> @document_system_id
  AND (
      menu.permission_code LIKE '/admin/document%'
      OR menu.component_path LIKE '/admin/document%'
      OR menu.router_path LIKE '/admin/document%'
      OR menu.menu_name IN ('文档管理系统', '文档管理')
      OR menu.parent_id IN (
          SELECT parent.id
          FROM system_menu_tb parent
          WHERE parent.system_id <> @document_system_id
            AND parent.menu_name IN ('文档管理系统', '文档管理')
      )
  );
