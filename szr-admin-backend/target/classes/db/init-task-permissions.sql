-- 添加任务管理菜单
INSERT INTO admin_menus (parent_id, name, path, component, permission, type, icon, sort, status)
VALUES 
(0, '任务管理', '/task', 'Layout', 'system:task', 0, 'task', 5, 1);

-- 获取任务管理菜单ID
SET @task_menu_id = LAST_INSERT_ID();

-- 添加任务列表菜单
INSERT INTO admin_menus (parent_id, name, path, component, permission, type, icon, sort, status)
VALUES 
(@task_menu_id, '合成任务', 'index', 'task/index', 'system:task:list', 1, 'list', 1, 1);

-- 获取任务列表菜单ID
SET @task_list_id = LAST_INSERT_ID();

-- 添加任务按钮权限
INSERT INTO admin_menus (parent_id, name, permission, type, sort, status)
VALUES 
(@task_list_id, '任务查询', 'system:task:query', 2, 1, 1),
(@task_list_id, '新建任务', 'system:task:add', 2, 2, 1),
(@task_list_id, '取消任务', 'system:task:edit', 2, 3, 1),
(@task_list_id, '删除任务', 'system:task:remove', 2, 4, 1);

-- 为管理员角色添加任务管理权限
INSERT INTO admin_role_menu (role_id, menu_id)
SELECT 1, id FROM admin_menus WHERE permission LIKE 'system:task%'; 