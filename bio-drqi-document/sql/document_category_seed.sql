-- 文档目录初始化脚本。
-- 企业目录全局共享；个人空间目录按用户初始化，create_by 用于个人目录隔离。

INSERT INTO doc_category_info (
    parent_id, category_name, category_code, category_type, sort_num, status,
    manager_user_id, manager_scope, inherit_permission, create_by, create_time,
    update_by, update_time, deleted
)
SELECT 0, category_name, category_code, 'ENTERPRISE', sort_num, 1,
       NULL, 'TREE', 'Y', 1, NOW(), 1, NOW(), 'N'
FROM (
    SELECT '制度规范' AS category_name, 'POLICY' AS category_code, 10 AS sort_num
    UNION ALL SELECT '项目资料', 'PROJECT', 20
    UNION ALL SELECT '检测资料', 'TEST', 30
    UNION ALL SELECT '研发资料', 'RND', 40
) seed
WHERE NOT EXISTS (
    SELECT 1
    FROM doc_category_info c
    WHERE c.category_type = 'ENTERPRISE'
      AND c.category_code = seed.category_code
      AND c.deleted = 'N'
);

INSERT INTO doc_category_info (
    parent_id, category_name, category_code, category_type, sort_num, status,
    manager_user_id, manager_scope, inherit_permission, create_by, create_time,
    update_by, update_time, deleted
)
SELECT 0, '我的资料', CONCAT('PERSONAL_', u.id), 'PERSONAL', 10, 1,
       u.id, 'TREE', 'Y', u.id, NOW(), u.id, NOW(), 'N'
FROM system_user_tb u
WHERE NOT EXISTS (
    SELECT 1
    FROM doc_category_info c
    WHERE c.category_type = 'PERSONAL'
      AND c.create_by = u.id
      AND c.deleted = 'N'
);
