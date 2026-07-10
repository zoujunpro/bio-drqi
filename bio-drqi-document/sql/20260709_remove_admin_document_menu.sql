-- 企业文档保留在独立文档入口；后台管理不再展示文档管理菜单。
-- 文档系统(system_id=11)自身菜单切到 portal 路由，历史后台 /admin/document 菜单置为禁用。

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

UPDATE system_menu_tb
SET menu_status = 'N',
    update_time = NOW()
WHERE system_id <> @document_system_id
  AND menu_type = '1'
  AND (
      permission_code LIKE '/admin/document%'
      OR component_path LIKE '/admin/document%'
      OR router_path LIKE '/admin/document%'
      OR menu_name IN ('文档管理系统', '文档管理')
  );
