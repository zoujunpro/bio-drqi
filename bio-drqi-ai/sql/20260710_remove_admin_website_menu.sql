-- 官网配置不再在后台管理展示，删除历史后台官网菜单和角色关联。

DELETE ref
FROM system_role_menu_ref ref
JOIN system_menu_tb menu ON menu.id = ref.menu_id
WHERE menu.system_id = 1
  AND (
      menu.menu_name IN ('官网配置', '文章管理', '新闻发布', '意见反馈', '留言板')
      OR menu.permission_code LIKE '/admin/website%'
      OR menu.router_path LIKE '/admin/website%'
      OR menu.component_path LIKE '%admin/website%'
      OR menu.parent_id IN (
          SELECT parent.id
          FROM system_menu_tb parent
          WHERE parent.system_id = 1
            AND (
                parent.menu_name = '官网配置'
                OR parent.permission_code LIKE '/admin/website%'
                OR parent.router_path LIKE '/admin/website%'
                OR parent.component_path LIKE '%admin/website%'
            )
      )
  );

DELETE menu
FROM system_menu_tb menu
WHERE menu.system_id = 1
  AND (
      menu.menu_name IN ('官网配置', '文章管理', '新闻发布', '意见反馈', '留言板')
      OR menu.permission_code LIKE '/admin/website%'
      OR menu.router_path LIKE '/admin/website%'
      OR menu.component_path LIKE '%admin/website%'
      OR menu.parent_id IN (
          SELECT parent.id
          FROM system_menu_tb parent
          WHERE parent.system_id = 1
            AND (
                parent.menu_name = '官网配置'
                OR parent.permission_code LIKE '/admin/website%'
                OR parent.router_path LIKE '/admin/website%'
                OR parent.component_path LIKE '%admin/website%'
            )
      )
  );
