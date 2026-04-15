-- 物流管理系统演示数据
-- 适用库：logistics_db
-- 执行前请确保已完成基础表结构初始化

USE logistics_db;

-- 1) 补充部分普通用户与快递员账号（若不存在）
INSERT INTO t_user(username, password, role, nickname, phone, status)
SELECT 'user001', '123456', 'USER', '张三', '13800000001', 1
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE username = 'user001');

INSERT INTO t_user(username, password, role, nickname, phone, status)
SELECT 'user002', '123456', 'USER', '李四', '13800000002', 1
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE username = 'user002');

INSERT INTO t_user(username, password, role, nickname, phone, status)
SELECT 'user003', '123456', 'USER', '王五', '13800000003', 1
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE username = 'user003');

INSERT INTO t_user(username, password, role, nickname, phone, status)
SELECT 'user004', '123456', 'USER', '赵六', '13800000004', 1
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE username = 'user004');

INSERT INTO t_user(username, password, role, nickname, phone, status)
SELECT 'courier002', '123456', 'COURIER', '派送员02', '13900000002', 1
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE username = 'courier002');

INSERT INTO t_user(username, password, role, nickname, phone, status)
SELECT 'courier003', '123456', 'COURIER', '派送员03', '13900000003', 1
WHERE NOT EXISTS (SELECT 1 FROM t_user WHERE username = 'courier003');

-- 2) 演示订单数据（跨多天分布，便于看板展示趋势）
INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404050001', '张三', '13800000001', '上海市浦东新区', '李四', '13911110001', '杭州市西湖区', 2.50, 120.00, 35.00, 0, DATE_SUB(NOW(), INTERVAL 7 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404050001');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404050002', '王五', '13800000003', '上海市闵行区', '赵六', '13911110002', '南京市鼓楼区', 1.20, 80.00, 12.40, 1, DATE_SUB(NOW(), INTERVAL 6 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404050002');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404060001', '李四', '13800000002', '广州市天河区', '陈七', '13911110003', '深圳市南山区', 3.80, 260.00, 50.00, 2, DATE_SUB(NOW(), INTERVAL 6 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404060001');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404060002', '周八', '13800000004', '北京市朝阳区', '钱九', '13911110004', '天津市和平区', 4.20, 150.00, 43.00, 3, DATE_SUB(NOW(), INTERVAL 5 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404060002');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404070001', '孙十', '13800000005', '深圳市福田区', '吴一', '13911110005', '厦门市思明区', 2.00, 180.00, 26.00, 4, DATE_SUB(NOW(), INTERVAL 4 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404070001');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404070002', '赵二', '13800000006', '成都市武侯区', '刘三', '13911110006', '重庆市渝中区', 5.00, 320.00, 70.00, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404070002');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404080001', '钱四', '13800000007', '杭州市余杭区', '郑五', '13911110007', '苏州市工业园区', 0.80, 60.00, 11.60, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404080001');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404080002', '冯六', '13800000008', '武汉市洪山区', '褚七', '13911110008', '长沙市岳麓区', 1.50, 110.00, 22.00, 0, DATE_SUB(NOW(), INTERVAL 3 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404080002');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404090001', '卫八', '13800000009', '西安市雁塔区', '蒋九', '13911110009', '郑州市金水区', 6.00, 410.00, 92.00, 3, DATE_SUB(NOW(), INTERVAL 2 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404090001');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404090002', '沈十', '13800000010', '南京市建邺区', '韩一', '13911110010', '合肥市蜀山区', 2.80, 95.00, 15.60, 4, DATE_SUB(NOW(), INTERVAL 1 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404090002');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404100001', '吕二', '13800000011', '宁波市海曙区', '马三', '13911110011', '福州市鼓楼区', 1.90, 210.00, 27.00, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404100001');

INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, receiver_address, weight, distance, fee, status, create_time)
SELECT 'LOG202404100002', '朱四', '13800000012', '青岛市市南区', '何五', '13911110012', '济南市历下区', 3.30, 130.00, 36.00, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_order WHERE order_no = 'LOG202404100002');

-- 3) 轨迹数据（按订单号关联，便于展示完整时间线）
INSERT INTO t_track(order_id, content, create_time)
SELECT id, '订单已创建，等待揽件', DATE_SUB(NOW(), INTERVAL 7 DAY) + INTERVAL 1 HOUR
FROM t_order WHERE order_no = 'LOG202404050001'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '订单已创建，等待揽件');

INSERT INTO t_track(order_id, content, create_time)
SELECT id, '仓库已揽件，正准备发往分拨中心', DATE_SUB(NOW(), INTERVAL 6 DAY) + INTERVAL 2 HOUR
FROM t_order WHERE order_no = 'LOG202404050002'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '仓库已揽件，正准备发往分拨中心');

INSERT INTO t_track(order_id, content, create_time)
SELECT id, '包裹已发出，正在运输途中', DATE_SUB(NOW(), INTERVAL 6 DAY) + INTERVAL 4 HOUR
FROM t_order WHERE order_no = 'LOG202404060001'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '包裹已发出，正在运输途中');

INSERT INTO t_track(order_id, content, create_time)
SELECT id, '包裹已到达目的地网点，派送员正在派送', DATE_SUB(NOW(), INTERVAL 5 DAY) + INTERVAL 3 HOUR
FROM t_order WHERE order_no = 'LOG202404060002'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '包裹已到达目的地网点，派送员正在派送');

INSERT INTO t_track(order_id, content, create_time)
SELECT id, '包裹已签收，感谢使用本物流服务', DATE_SUB(NOW(), INTERVAL 4 DAY) + INTERVAL 5 HOUR
FROM t_order WHERE order_no = 'LOG202404070001'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '包裹已签收，感谢使用本物流服务');

INSERT INTO t_track(order_id, content, create_time)
SELECT id, '包裹已发出，正在运输途中', DATE_SUB(NOW(), INTERVAL 4 DAY) + INTERVAL 2 HOUR
FROM t_order WHERE order_no = 'LOG202404070002'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '包裹已发出，正在运输途中');

INSERT INTO t_track(order_id, content, create_time)
SELECT id, '订单已创建，等待揽件', DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 1 HOUR
FROM t_order WHERE order_no = 'LOG202404080001'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '订单已创建，等待揽件');

INSERT INTO t_track(order_id, content, create_time)
SELECT id, '包裹已到达目的地网点，派送员正在派送', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 3 HOUR
FROM t_order WHERE order_no = 'LOG202404090001'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '包裹已到达目的地网点，派送员正在派送');

INSERT INTO t_track(order_id, content, create_time)
SELECT id, '包裹已签收，感谢使用本物流服务', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 4 HOUR
FROM t_order WHERE order_no = 'LOG202404090002'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '包裹已签收，感谢使用本物流服务');

INSERT INTO t_track(order_id, content, create_time)
SELECT id, '包裹已发出，正在运输途中', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 2 HOUR
FROM t_order WHERE order_no = 'LOG202404100001'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '包裹已发出，正在运输途中');

INSERT INTO t_track(order_id, content, create_time)
SELECT id, '订单已创建，等待揽件', NOW()
FROM t_order WHERE order_no = 'LOG202404100002'
AND NOT EXISTS (SELECT 1 FROM t_track WHERE order_id = t_order.id AND content = '订单已创建，等待揽件');

-- 4) 检查演示结果
SELECT id, order_no, status, fee, create_time
FROM t_order
ORDER BY create_time DESC;
