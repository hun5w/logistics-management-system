-- 安全增强与管理员操作日志迁移脚本
-- 适用库：logistics_db
-- 说明：
-- 1) 新建管理员操作日志表 t_admin_op_log
-- 2) 兼容旧账号明文密码（程序登录时会自动升级成 BCrypt）

USE logistics_db;

-- 1) 管理员操作日志表
CREATE TABLE IF NOT EXISTS `t_admin_op_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `operator_username` VARCHAR(50) NOT NULL COMMENT '操作管理员账号',
    `target_user_id` BIGINT DEFAULT NULL COMMENT '目标用户ID',
    `action` VARCHAR(50) NOT NULL COMMENT '动作类型',
    `detail` VARCHAR(255) DEFAULT NULL COMMENT '操作详情',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2) 可选：为日志检索创建索引
CREATE INDEX idx_admin_log_time ON t_admin_op_log(create_time);
CREATE INDEX idx_admin_log_operator ON t_admin_op_log(operator_username);

-- 3) 预留说明：无需批量修改旧密码
--    系统已支持“明文登录成功后自动升级为 BCrypt 哈希”
--    所以原有 admin / warehouse / courier 可继续使用原密码首次登录

SELECT 'security-and-admin-log migration done' AS result;
