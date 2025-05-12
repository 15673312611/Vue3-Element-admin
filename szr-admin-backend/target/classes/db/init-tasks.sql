-- 创建任务表
CREATE TABLE IF NOT EXISTS `tasks` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
  `name` VARCHAR(100) NOT NULL COMMENT '任务名称',
  `digital_human_id` BIGINT NOT NULL COMMENT '数字人ID',
  `digital_human_name` VARCHAR(100) DEFAULT NULL COMMENT '数字人名称',
  `content` TEXT NOT NULL COMMENT '任务内容',
  `video_url` VARCHAR(255) DEFAULT NULL COMMENT '视频URL',
  `status` VARCHAR(2) NOT NULL DEFAULT '0' COMMENT '任务状态：0-待处理，1-处理中，2-已完成，3-失败',
  `progress` INT NOT NULL DEFAULT 0 COMMENT '进度百分比',
  `error_message` VARCHAR(255) DEFAULT NULL COMMENT '错误信息',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_digital_human_id` (`digital_human_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_create_time` (`create_time`)
) COMMENT='任务表'; 