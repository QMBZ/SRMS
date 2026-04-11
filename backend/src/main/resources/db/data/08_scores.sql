-- 计算机科学与技术（1号专业）课程
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '计算机组成原理', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=1;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '数据结构与算法', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=1;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '操作系统', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=1;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '数据库原理', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=1;

-- 软件工程（2号专业）课程
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '软件工程导论', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=2;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '软件测试技术', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=2;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, 'Java程序设计', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=2;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '软件项目管理', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=2;

-- 数据科学与大数据技术（3号专业）课程
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, 'Python数据分析', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=3;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '大数据技术基础', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=3;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '机器学习导论', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=3;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '数据可视化', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=3;

-- 电子信息工程（4号专业）课程
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '电路分析基础', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=4;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '模拟电子技术', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=4;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '数字电子技术', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=4;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '信号与系统', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=4;

-- 通信工程（5号专业）课程
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '通信原理', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=5;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '移动通信技术', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=5;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '电磁场与电磁波', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=5;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '光纤通信', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=5;

-- 人工智能（6号专业）课程
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '人工智能导论', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=6;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '深度学习基础', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=6;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '计算机视觉', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=6;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '自然语言处理', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=6;

-- 机械设计制造（7号专业）课程
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '机械制图', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=7;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '工程力学', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=7;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '机械设计基础', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=7;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '数控技术', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=7;

-- 车辆工程（8号专业）课程
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '汽车构造', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=8;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '汽车理论', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=8;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '发动机原理', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=8;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '汽车电子控制技术', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=8;

-- 智能制造工程（9号专业）课程
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '智能制造系统', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=9;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '工业机器人技术', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=9;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '智能生产线设计', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=9;
INSERT INTO scores (student_no, course_name, score)
SELECT student_no, '工业物联网', ROUND(55 + (RAND() * 60),1) FROM student_info WHERE major_id=9;