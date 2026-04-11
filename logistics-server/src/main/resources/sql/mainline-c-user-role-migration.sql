-- 主线 C：用户角色与账号管理迁移脚本
-- 适用库：logistics_db
-- 执行前建议先备份数据库

USE logistics_db;

-- 1) 确保 t_user 存在 phone/status 字段（兼容旧表结构）
ALTER TABLE t_user
    ADD COLUMN IF NOT EXISTS phone VARCHAR(20) NULL UNIQUE COMMENT '联系电话',
    ADD COLUMN IF NOT EXISTS status TINYINT DEFAULT 1 COMMENT '账号状态: 1正常 0禁用';

-- 2) 如果历史数据 status 为空，统一回填为 1
UPDATE t_user SET status = 1 WHERE status IS NULL;

-- 3) 初始化超级管理员（若不存在）
INSERT INTO t_user(username, password, role, nickname, phone, status)
SELECT 'admin', '123456', 'ADMIN', '超级管理员', NULL, 1
WHERE NOT EXISTS (
    SELECT 1 FROM t_user WHERE username = 'admin'
);

-- 4) 初始化仓库管理员（若不存在）
INSERT INTO t_user(username, password, role, nickname, phone, status)
SELECT 'shanghai_wh', '123456', 'WAREHOUSE', '上海仓库员', NULL, 1
WHERE NOT EXISTS (
    SELECT 1 FROM t_user WHERE username = 'shanghai_wh'
);

-- 5) 可选：初始化快递员（若不存在）
INSERT INTO t_user(username, password, role, nickname, phone, status)
SELECT 'courier001', '123456', 'COURIER', '末端快递员', NULL, 1
WHERE NOT EXISTS (
    SELECT 1 FROM t_user WHERE username = 'courier001'
);

-- 6) 检查结果
SELECT id, username, role, nickname, phone, status, create_time
FROM t_user
ORDER BY create_time DESC;
