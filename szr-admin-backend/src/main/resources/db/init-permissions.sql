-- 初始化菜单权限
INSERT INTO admin_menus (id, parent_id, name, path, component, permission, type, icon, sort, status, create_time, update_time)
VALUES 
(1, 0, '系统管理', '/system', 'Layout', NULL, 0, 'system', 1, 1, NOW(), NOW()),
(2, 1, '用户管理', 'user', 'system/user/index', 'user:list', 1, 'user', 1, 1, NOW(), NOW()),
(3, 1, '角色管理', 'role', 'system/role/index', 'role:list', 1, 'role', 2, 1, NOW(), NOW()),
(4, 1, '菜单管理', 'menu', 'system/menu/index', 'menu:list', 1, 'menu', 3, 1, NOW(), NOW());

-- 初始化角色
INSERT INTO admin_roles (id, name, code, status, create_time, update_time)
VALUES (1, '超级管理员', 'SUPER_ADMIN', 1, NOW(), NOW());

-- 初始化角色菜单关联
INSERT INTO admin_role_menu (role_id, menu_id)
VALUES 
(1, 1), (1, 2), (1, 3), (1, 4);

-- 初始化管理员用户角色关联
INSERT INTO admin_user_role (admin_id, role_id)
VALUES (1, 1); 