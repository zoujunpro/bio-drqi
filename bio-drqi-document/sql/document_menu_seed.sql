-- 文档管理系统菜单初始化脚本。
-- 适配当前数据库表结构：
--   system_sys_tb(id, system_name, status)
--   system_menu_tb(parent_id, system_id, permission_code, menu_name, component_path, menu_type, menu_status, menu_icon, order_num, router_path)
--   system_user_sys_ref(user_id, system_id)
--   system_role_menu_ref(role_id, menu_id)

SET @document_system_id := 11;

INSERT INTO system_sys_tb (id, system_name, status)
SELECT @document_system_id, '文档管理系统', 'Y'
WHERE NOT EXISTS (
    SELECT 1 FROM system_sys_tb WHERE id = @document_system_id
);

UPDATE system_sys_tb
SET system_name = '文档管理系统',
    status = 'Y'
WHERE id = @document_system_id;

INSERT INTO system_menu_tb (
    parent_id, system_id, permission_code, menu_name, component_path,
    menu_type, menu_status, menu_icon, order_num, create_time, update_time, router_path
)
SELECT 0, @document_system_id, '/portal/document', '文档管理系统', '/portal/document/enterprise',
       '1', 'Y', 'Document', 1, NOW(), NOW(), '/portal/document'
WHERE NOT EXISTS (
    SELECT 1
    FROM system_menu_tb
    WHERE system_id = @document_system_id
      AND parent_id = 0
      AND menu_name = '文档管理系统'
);

UPDATE system_menu_tb
SET permission_code = '/portal/document',
    menu_name = '文档管理系统',
    component_path = '/portal/document/enterprise',
    parent_id = 0,
    menu_type = '1',
    menu_status = 'Y',
    menu_icon = 'Document',
    order_num = 1,
    router_path = '/portal/document',
    update_time = NOW()
WHERE system_id = @document_system_id
  AND (
      (parent_id = 0 AND menu_name IN ('文档管理系统', '文档管理', '文档中心'))
      OR id = parent_id
  );

DELETE ref
FROM system_role_menu_ref ref
JOIN system_menu_tb m ON m.id = ref.menu_id
WHERE m.system_id = @document_system_id
  AND m.id = m.parent_id;

DELETE FROM system_menu_tb
WHERE system_id = @document_system_id
  AND id = parent_id;

DELETE ref
FROM system_role_menu_ref ref
JOIN system_menu_tb duplicate_root ON duplicate_root.id = ref.menu_id
LEFT JOIN (
    SELECT MIN(id) AS keep_id, permission_code
    FROM system_menu_tb
    WHERE system_id = @document_system_id
      AND parent_id = 0
      AND menu_type = '1'
    GROUP BY permission_code
) keep_root
  ON keep_root.keep_id = duplicate_root.id
WHERE duplicate_root.system_id = @document_system_id
  AND duplicate_root.parent_id = 0
  AND duplicate_root.menu_type = '1'
  AND keep_root.keep_id IS NULL;

DELETE duplicate_root
FROM system_menu_tb duplicate_root
LEFT JOIN (
    SELECT MIN(id) AS keep_id, permission_code
    FROM system_menu_tb
    WHERE system_id = @document_system_id
      AND parent_id = 0
      AND menu_type = '1'
    GROUP BY permission_code
) keep_root
  ON keep_root.keep_id = duplicate_root.id
WHERE duplicate_root.system_id = @document_system_id
  AND duplicate_root.parent_id = 0
  AND duplicate_root.menu_type = '1'
  AND keep_root.keep_id IS NULL;

INSERT INTO system_menu_tb (
    parent_id, system_id, permission_code, menu_name, component_path,
    menu_type, menu_status, menu_icon, order_num, create_time, update_time, router_path
)
SELECT parent.id, @document_system_id, menu.permission_code, menu.menu_name, menu.component_path,
       '1', 'Y', menu.menu_icon, menu.order_num, NOW(), NOW(), menu.router_path
FROM system_menu_tb parent
JOIN (
    SELECT '/portal/document/enterprise' AS permission_code, '企业文档' AS menu_name, '/portal/document/enterprise' AS component_path, '/portal/document/enterprise' AS router_path, 'Document' AS menu_icon, 1 AS order_num
    UNION ALL SELECT '/portal/document/personal', '个人空间', '/portal/document/personal', '/portal/document/personal', 'User', 2
    UNION ALL SELECT '/portal/document/favorites', '我的收藏', '/portal/document/favorites', '/portal/document/favorites', 'Document', 3
    UNION ALL SELECT '/portal/document/recent', '最近浏览', '/portal/document/recent', '/portal/document/recent', 'Monitor', 4
    UNION ALL SELECT '/portal/document/shares', '我的分享', '/portal/document/shares', '/portal/document/shares', 'Share', 5
    UNION ALL SELECT '/portal/document/recycle', '回收站', '/portal/document/recycle', '/portal/document/recycle', 'Delete', 6
    UNION ALL SELECT '/portal/document/aiEvents', 'AI索引事件', '/portal/document/aiEvents', '/portal/document/aiEvents', 'Document', 7
) menu
WHERE parent.system_id = @document_system_id
  AND parent.parent_id = 0
  AND parent.menu_name = '文档管理系统'
  AND NOT EXISTS (
      SELECT 1
      FROM system_menu_tb exists_menu
      WHERE exists_menu.system_id = @document_system_id
        AND exists_menu.router_path = menu.router_path
        AND exists_menu.id <> parent.id
  );

UPDATE system_menu_tb child
JOIN system_menu_tb parent
  ON parent.system_id = @document_system_id
 AND parent.parent_id = 0
 AND parent.menu_name = '文档管理系统'
JOIN (
    SELECT '/portal/document/enterprise' AS permission_code, '企业文档' AS menu_name, '/portal/document/enterprise' AS component_path, '/portal/document/enterprise' AS router_path, 'Document' AS menu_icon, 1 AS order_num
    UNION ALL SELECT '/portal/document/personal', '个人空间', '/portal/document/personal', '/portal/document/personal', 'User', 2
    UNION ALL SELECT '/portal/document/favorites', '我的收藏', '/portal/document/favorites', '/portal/document/favorites', 'Document', 3
    UNION ALL SELECT '/portal/document/recent', '最近浏览', '/portal/document/recent', '/portal/document/recent', 'Monitor', 4
    UNION ALL SELECT '/portal/document/shares', '我的分享', '/portal/document/shares', '/portal/document/shares', 'Share', 5
    UNION ALL SELECT '/portal/document/recycle', '回收站', '/portal/document/recycle', '/portal/document/recycle', 'Delete', 6
    UNION ALL SELECT '/portal/document/aiEvents', 'AI索引事件', '/portal/document/aiEvents', '/portal/document/aiEvents', 'Document', 7
) menu
  ON menu.router_path = child.router_path
SET child.parent_id = parent.id,
    child.system_id = @document_system_id,
    child.permission_code = menu.permission_code,
    child.menu_name = menu.menu_name,
    child.component_path = menu.component_path,
    child.menu_type = '1',
    child.menu_status = 'Y',
    child.menu_icon = menu.menu_icon,
    child.order_num = menu.order_num,
    child.router_path = menu.router_path,
    child.update_time = NOW()
WHERE child.system_id = @document_system_id
  AND child.id <> parent.id;

INSERT INTO system_menu_tb (
    parent_id, system_id, permission_code, menu_name, component_path,
    menu_type, menu_status, menu_icon, order_num, create_time, update_time, router_path
)
SELECT center.id, @document_system_id, button.permission_code, button.menu_name, '',
       '2', 'Y', NULL, button.order_num, NOW(), NOW(), ''
FROM system_menu_tb center
JOIN (
    SELECT 'document:center:category' AS permission_code, '分类管理' AS menu_name, 1 AS order_num
    UNION ALL SELECT 'document:center:upload', '上传文档', 2
    UNION ALL SELECT 'document:center:download', '下载文档', 3
    UNION ALL SELECT 'document:center:version', '查看版本', 4
    UNION ALL SELECT 'document:center:uploadVersion', '上传新版本', 5
    UNION ALL SELECT 'document:center:permission', '权限配置', 6
    UNION ALL SELECT 'document:center:delete', '删除文档', 7
) button
WHERE center.system_id = @document_system_id
  AND center.router_path = '/portal/document/enterprise'
  AND NOT EXISTS (
      SELECT 1
      FROM system_menu_tb exists_button
      WHERE exists_button.system_id = @document_system_id
        AND exists_button.parent_id = center.id
        AND exists_button.permission_code = button.permission_code
  );

UPDATE system_menu_tb button
JOIN system_menu_tb center
  ON center.system_id = @document_system_id
 AND center.router_path = '/portal/document/enterprise'
JOIN (
    SELECT 'document:center:category' AS permission_code, '分类管理' AS menu_name, 1 AS order_num
    UNION ALL SELECT 'document:center:upload', '上传文档', 2
    UNION ALL SELECT 'document:center:download', '下载文档', 3
    UNION ALL SELECT 'document:center:version', '查看版本', 4
    UNION ALL SELECT 'document:center:uploadVersion', '上传新版本', 5
    UNION ALL SELECT 'document:center:permission', '权限配置', 6
    UNION ALL SELECT 'document:center:delete', '删除文档', 7
) button_def
  ON button_def.permission_code = button.permission_code
SET button.parent_id = center.id,
    button.system_id = @document_system_id,
    button.menu_name = button_def.menu_name,
    button.component_path = '',
    button.menu_type = '2',
    button.menu_status = 'Y',
    button.menu_icon = NULL,
    button.order_num = button_def.order_num,
    button.router_path = '',
    button.update_time = NOW()
WHERE button.system_id = @document_system_id
  AND button.permission_code = button_def.permission_code;

DELETE ref
FROM system_role_menu_ref ref
JOIN system_menu_tb duplicate_menu ON duplicate_menu.id = ref.menu_id
LEFT JOIN (
    SELECT MIN(id) AS keep_id, parent_id, permission_code
    FROM system_menu_tb
    WHERE system_id = @document_system_id
      AND menu_type = '2'
    GROUP BY parent_id, permission_code
) keep_menu
  ON keep_menu.keep_id = duplicate_menu.id
WHERE duplicate_menu.system_id = @document_system_id
  AND duplicate_menu.menu_type = '2'
  AND keep_menu.keep_id IS NULL;

DELETE duplicate_menu
FROM system_menu_tb duplicate_menu
LEFT JOIN (
    SELECT MIN(id) AS keep_id, parent_id, permission_code
    FROM system_menu_tb
    WHERE system_id = @document_system_id
      AND menu_type = '2'
    GROUP BY parent_id, permission_code
) keep_menu
  ON keep_menu.keep_id = duplicate_menu.id
WHERE duplicate_menu.system_id = @document_system_id
  AND duplicate_menu.menu_type = '2'
  AND keep_menu.keep_id IS NULL;

-- 当前已有后台系统权限的用户，默认同步文档管理系统访问权限。
INSERT INTO system_user_sys_ref (user_id, system_id)
SELECT DISTINCT ref.user_id, @document_system_id
FROM system_user_sys_ref ref
LEFT JOIN system_user_sys_ref exists_ref
  ON exists_ref.user_id = ref.user_id
 AND exists_ref.system_id = @document_system_id
WHERE ref.system_id = 1
  AND exists_ref.id IS NULL;

-- 给已有管理员角色默认授权；其他角色可在“角色管理”中手动勾选。
INSERT INTO system_role_menu_ref (role_id, menu_id)
SELECT r.id, m.id
FROM system_role_tb r
JOIN system_menu_tb m
  ON m.system_id = @document_system_id
LEFT JOIN system_role_menu_ref exists_ref
  ON exists_ref.role_id = r.id
 AND exists_ref.menu_id = m.id
WHERE exists_ref.id IS NULL
  AND (
      r.id = 1
      OR r.role_name LIKE '%管理员%'
      OR r.role_name LIKE '%admin%'
  );
