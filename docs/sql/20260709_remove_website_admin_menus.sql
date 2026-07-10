-- 后台管理移除官网配置相关菜单。
-- 只禁用菜单入口和按钮授权，不删除业务数据表。

UPDATE system_menu_tb
SET menu_status = 'N',
    update_time = NOW()
WHERE menu_type = '1'
  AND (
      menu_name IN ('官网配置', '文章管理', '新闻发布', '意见反馈', '留言板')
      OR permission_code LIKE '/admin/website%'
      OR permission_code = '/admin/releaseNews'
      OR component_path LIKE '/admin/website%'
      OR component_path = '/admin/releaseNews'
      OR router_path LIKE '/admin/website%'
      OR router_path = '/admin/releaseNews'
  );
