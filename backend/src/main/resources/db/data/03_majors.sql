TRUNCATE TABLE majors;

-- 01计算机学院
INSERT INTO majors (major_name, major_code, college_id) VALUES
('计算机科学与技术', '0101', 1),
('软件工程', '0102', 1),
('数据科学与大数据技术', '0103', 1);

-- 02电子信息学院
INSERT INTO majors (major_name, major_code, college_id) VALUES
('电子信息工程', '0201', 2),
('通信工程', '0202', 2),
('人工智能', '0203', 2);

-- 03机械工程学院
INSERT INTO majors (major_name, major_code, college_id) VALUES
('机械设计制造', '0301', 3),
('车辆工程', '0302', 3),
('智能制造工程', '0303', 3);