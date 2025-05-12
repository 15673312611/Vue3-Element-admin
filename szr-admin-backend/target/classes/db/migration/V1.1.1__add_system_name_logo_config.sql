-- 添加系统名称和Logo配置项
INSERT INTO system_config (config_key, config_value, description, create_time, update_time)
VALUES ('system_name', '数字人后台', '系统名称', NOW(), NOW()),
       ('system_logo', '/static/logo.svg', '系统Logo路径', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    config_value = VALUES(config_value),
    description = VALUES(description),
    update_time = NOW(); 