-- DocHub style enterprise document mock data alignment.
-- This script is idempotent for categories and only reassigns MOCK-ENT documents.

INSERT INTO doc_category_info
    (parent_id, category_name, category_code, category_type, sort_num, status, manager_scope, inherit_permission, create_by, create_time, update_by, update_time, deleted)
SELECT 0, '企业文档', 'ENT_ROOT', 'ENTERPRISE', 10, 1, 'TREE', 'Y', 1, NOW(), 1, NOW(), 'N'
WHERE NOT EXISTS (
    SELECT 1 FROM doc_category_info WHERE category_code = 'ENT_ROOT' AND category_type = 'ENTERPRISE' AND deleted = 'N'
);

SET @doc_enterprise_root_id = (
    SELECT id FROM doc_category_info
    WHERE category_code = 'ENT_ROOT' AND category_type = 'ENTERPRISE' AND deleted = 'N'
    ORDER BY id LIMIT 1
);

INSERT INTO doc_category_info
    (parent_id, category_name, category_code, category_type, sort_num, status, manager_scope, inherit_permission, create_by, create_time, update_by, update_time, deleted)
SELECT @doc_enterprise_root_id, category_name, category_code, 'ENTERPRISE', sort_num, 1, 'TREE', 'Y', 1, NOW(), 1, NOW(), 'N'
FROM (
    SELECT '子目录' category_name, 'ENT_SUB' category_code, 10 sort_num
    UNION ALL SELECT '技术方案', 'ENT_TECH', 20
    UNION ALL SELECT '测试报告', 'ENT_TEST_REPORT', 30
    UNION ALL SELECT '实验数据', 'ENT_EXPERIMENT', 40
    UNION ALL SELECT '制度文件', 'ENT_POLICY', 50
    UNION ALL SELECT '培训资料', 'ENT_TRAINING', 60
    UNION ALL SELECT '会议纪要', 'ENT_MEETING', 70
    UNION ALL SELECT '合同资料', 'ENT_CONTRACT', 80
    UNION ALL SELECT '公共文档', 'ENT_PUBLIC', 90
) seed
WHERE NOT EXISTS (
    SELECT 1 FROM doc_category_info c
    WHERE c.category_code = seed.category_code AND c.category_type = 'ENTERPRISE' AND c.deleted = 'N'
);

SET @doc_sub_id = (
    SELECT id FROM doc_category_info
    WHERE category_code = 'ENT_SUB' AND category_type = 'ENTERPRISE' AND deleted = 'N'
    ORDER BY id LIMIT 1
);

INSERT INTO doc_category_info
    (parent_id, category_name, category_code, category_type, sort_num, status, manager_scope, inherit_permission, create_by, create_time, update_by, update_time, deleted)
SELECT @doc_sub_id, category_name, category_code, 'ENTERPRISE', sort_num, 1, 'TREE', 'Y', 1, NOW(), 1, NOW(), 'N'
FROM (
    SELECT '测试子目录' category_name, 'ENT_SUB_TEST_A' category_code, 10 sort_num
    UNION ALL SELECT '测试子目录', 'ENT_SUB_TEST_B', 20
    UNION ALL SELECT '子目录第二', 'ENT_SUB_SECOND', 30
) seed
WHERE NOT EXISTS (
    SELECT 1 FROM doc_category_info c
    WHERE c.category_code = seed.category_code AND c.category_type = 'ENTERPRISE' AND c.deleted = 'N'
);

SET @doc_sub_test_a_id = (
    SELECT id FROM doc_category_info
    WHERE category_code = 'ENT_SUB_TEST_A' AND category_type = 'ENTERPRISE' AND deleted = 'N'
    ORDER BY id LIMIT 1
);

INSERT INTO doc_category_info
    (parent_id, category_name, category_code, category_type, sort_num, status, manager_scope, inherit_permission, create_by, create_time, update_by, update_time, deleted)
SELECT @doc_sub_test_a_id, '测试子目录', 'ENT_SUB_TEST_A_CHILD', 'ENTERPRISE', 10, 1, 'TREE', 'Y', 1, NOW(), 1, NOW(), 'N'
WHERE NOT EXISTS (
    SELECT 1 FROM doc_category_info WHERE category_code = 'ENT_SUB_TEST_A_CHILD' AND category_type = 'ENTERPRISE' AND deleted = 'N'
);

DROP TEMPORARY TABLE IF EXISTS tmp_dochub_mock_categories;
CREATE TEMPORARY TABLE tmp_dochub_mock_categories AS
SELECT id, ROW_NUMBER() OVER (ORDER BY sort_num, id) AS rn
FROM doc_category_info
WHERE category_type = 'ENTERPRISE'
  AND deleted = 'N'
  AND status = 1
  AND category_code IN (
      'ENT_SUB', 'ENT_SUB_TEST_A', 'ENT_SUB_TEST_A_CHILD', 'ENT_SUB_TEST_B', 'ENT_SUB_SECOND',
      'ENT_TECH', 'ENT_TEST_REPORT', 'ENT_EXPERIMENT', 'ENT_POLICY', 'ENT_TRAINING',
      'ENT_MEETING', 'ENT_CONTRACT', 'ENT_PUBLIC'
  );

SET @doc_mock_category_count = (SELECT COUNT(1) FROM tmp_dochub_mock_categories);

UPDATE doc_file_info f
JOIN (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM doc_file_info
    WHERE doc_code LIKE 'MOCK-ENT-%'
      AND deleted = 'N'
) d ON d.id = f.id
JOIN tmp_dochub_mock_categories c
    ON c.rn = MOD(d.rn - 1, @doc_mock_category_count) + 1
SET f.category_id = c.id,
    f.space_type = 'ENTERPRISE',
    f.status = 'PUBLISHED',
    f.update_by = 1,
    f.update_time = NOW()
WHERE @doc_mock_category_count > 0;

DROP TEMPORARY TABLE IF EXISTS tmp_dochub_mock_categories;
