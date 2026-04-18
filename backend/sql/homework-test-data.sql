-- 作业模块测试数据脚本（健壮版本）
-- 用于端到端测试验证前后端链路
-- 可重复执行，不会因数据已存在而报错
--
-- ==========================================
-- 测试账户说明
-- ==========================================
-- 教师账号：admin    密码：123456（角色选 teacher）
-- 学生账号：23201321 密码：123456（角色选 student）- 廖同学，班级2301
-- 学生账号：23201322 密码：123456（角色选 student）- 刘同学，班级2301
-- 学生账号：23201323 密码：123456（角色选 student）- 罗同学，班级2301
-- 学生账号：23201324 密码：123456（角色选 student）- 钱同学，班级2301
--
-- 密码存储方式：MD5(SALT + 明文) = MD5("springboot" + "123456") = a384380c440fb620eb080df5cbfcd0f0
-- ==========================================

USE edu_hub;

-- ==========================================
-- 1. 补充数据库字段（安全方式）
-- ==========================================

-- 检查并添加批改状态字段
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM information_schema.columns
WHERE table_schema = 'edu_hub' AND table_name = 'res_exercise_record' AND column_name = 'grading_status';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE res_exercise_record ADD COLUMN grading_status INT DEFAULT 0 COMMENT ''批改状态：0未批改,1自动判分,2教师批改''',
    'SELECT ''字段 grading_status 已存在'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加教师评语字段
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists FROM information_schema.columns
WHERE table_schema = 'edu_hub' AND table_name = 'res_exercise_record' AND column_name = 'comment';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE res_exercise_record ADD COLUMN comment VARCHAR(500) COMMENT ''教师评语''',
    'SELECT ''字段 comment 已存在'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 2. 确保班级数据存在
-- ==========================================

INSERT INTO auth_class (class_code, headmaster_name, class_status, created_at, updated_at)
VALUES ('2301', '实验班级', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE headmaster_name = VALUES(headmaster_name);

-- ==========================================
-- 3. 创建测试账户（密码统一：123456）
-- 密码存储为加盐MD5：MD5("springboot123456") = a384380c440fb620eb080df5cbfcd0f0
-- 教师ID=1 对应 admin，作业数据中 teacher_id=1 引用此账户
-- ==========================================

-- 教师：admin / 123456（登录时角色选 teacher）
INSERT INTO auth_teacher (username, password_md5, real_name, created_at, updated_at)
VALUES ('admin', 'a384380c440fb620eb080df5cbfcd0f0', '管理员教师', NOW(), NOW())
ON DUPLICATE KEY UPDATE password_md5 = VALUES(password_md5), real_name = VALUES(real_name);

-- 学生：23201321~23201324 / 123456（登录时角色选 student）
INSERT INTO auth_student (student_code, student_name, password_md5, class_code, remark, account_status, created_at, updated_at)
VALUES ('23201321', '廖同学', 'a384380c440fb620eb080df5cbfcd0f0', '2301', '作业测试学生', 0, NOW(), NOW()),
       ('23201322', '刘同学', 'a384380c440fb620eb080df5cbfcd0f0', '2301', '作业测试学生', 0, NOW(), NOW()),
       ('23201323', '罗同学', 'a384380c440fb620eb080df5cbfcd0f0', '2301', '作业测试学生', 0, NOW(), NOW()),
       ('23201324', '钱同学', 'a384380c440fb620eb080df5cbfcd0f0', '2301', '作业测试学生', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE student_name = VALUES(student_name), password_md5 = VALUES(password_md5), class_code = VALUES(class_code), account_status = VALUES(account_status);

-- ==========================================
-- 4. 创建测试作业数据
-- ==========================================

-- 创建作业（作业ID: 1）
INSERT INTO edu_exercise (id, sort_order, task_name, teacher_id, relate_exp_id, interact_mode, description, publish_status, start_time, end_time, created_at, updated_at)
VALUES (1, 1, '需求分析作业一：角色旅程拆解', 1, NULL, 1, '围绕教学平台案例输出角色旅程和验收边界分析。', 1, '2026-04-14 08:00:00', '2026-04-20 20:00:00', NOW(), NOW())
ON DUPLICATE KEY UPDATE task_name = VALUES(task_name), publish_status = VALUES(publish_status);

-- 创建作业（作业ID: 2）
INSERT INTO edu_exercise (id, sort_order, task_name, teacher_id, relate_exp_id, interact_mode, description, publish_status, start_time, end_time, created_at, updated_at)
VALUES (2, 2, '界面结构复盘报告', 1, NULL, 1, '复盘壳层、页面层、共享组件层的边界划分。', 1, '2026-04-10 09:00:00', '2026-04-18 18:00:00', NOW(), NOW())
ON DUPLICATE KEY UPDATE task_name = VALUES(task_name), publish_status = VALUES(publish_status);

-- 创建作业（作业ID: 3）- 草稿状态
INSERT INTO edu_exercise (id, sort_order, task_name, teacher_id, relate_exp_id, interact_mode, description, publish_status, start_time, end_time, created_at, updated_at)
VALUES (3, 3, '课堂展示结构草案', 1, NULL, 1, '用于下周课堂展示的结构草案，尚未布置。', 0, NULL, '2026-04-24 17:30:00', NOW(), NOW())
ON DUPLICATE KEY UPDATE task_name = VALUES(task_name), publish_status = VALUES(publish_status);

-- ==========================================
-- 5. 创建作业题目
-- ==========================================

-- 作业1的题目
INSERT INTO edu_exercise_item (id, exercise_id, question, options_text, standard_answer, question_type, max_score, question_bank_id, created_at, updated_at)
VALUES (1, 1, '以下哪项最适合作为角色旅程的首层分段？', 'A. 按数据库表名分段,B. 按用户任务阶段分段,C. 按代码文件目录分段', 'B', 2, 5, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE question = VALUES(question);

INSERT INTO edu_exercise_item (id, exercise_id, question, options_text, standard_answer, question_type, max_score, question_bank_id, created_at, updated_at)
VALUES (2, 1, '验收边界描述中以下哪些属于关键内容？（多选）', 'A. 通过条件,B. 失败条件,C. 界面颜色与字号,D. 开发者个人偏好', 'AB', 3, 5, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE question = VALUES(question);

INSERT INTO edu_exercise_item (id, exercise_id, question, options_text, standard_answer, question_type, max_score, question_bank_id, created_at, updated_at)
VALUES (3, 1, '角色旅程图只需要描述主流程，不需要描述异常流。', '', 'F', 4, 3, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE question = VALUES(question);

INSERT INTO edu_exercise_item (id, exercise_id, question, options_text, standard_answer, question_type, max_score, question_bank_id, created_at, updated_at)
VALUES (4, 1, '在需求分析中，___是用来描述用户与系统交互过程的工具，而___则定义了系统在何种条件下可以通过验收。', '角色旅程图|验收边界', '角色旅程图|验收边界', 1, 6, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE question = VALUES(question);

INSERT INTO edu_exercise_item (id, exercise_id, question, options_text, standard_answer, question_type, max_score, question_bank_id, created_at, updated_at)
VALUES (5, 1, '请简述角色旅程图的核心价值，并结合教学平台案例说明其在需求分析中的作用。', '', '', 5, 10, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE question = VALUES(question);

-- 作业2的题目
INSERT INTO edu_exercise_item (id, exercise_id, question, options_text, standard_answer, question_type, max_score, question_bank_id, created_at, updated_at)
VALUES (6, 2, 'Vue3的组合式API使用哪个函数创建响应式数据？', 'A. reactive,B. ref,C. computed,D. watch', 'B', 2, 5, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE question = VALUES(question);

INSERT INTO edu_exercise_item (id, exercise_id, question, options_text, standard_answer, question_type, max_score, question_bank_id, created_at, updated_at)
VALUES (7, 2, '以下哪些是Vue3的特性？（多选）', 'A. 组合式API,B. 更好的TypeScript支持,C. 更快的渲染性能,D. 兼容IE8', 'ABC', 3, 5, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE question = VALUES(question);

INSERT INTO edu_exercise_item (id, exercise_id, question, options_text, standard_answer, question_type, max_score, question_bank_id, created_at, updated_at)
VALUES (8, 2, '请简述Vue3相比Vue2的主要改进。', '', '', 5, 20, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE question = VALUES(question);

-- ==========================================
-- 6. 发布作业到班级
-- ==========================================

-- 将作业1发布到班级2301
INSERT INTO rel_exercise_class (exercise_id, class_code, created_at, updated_at)
VALUES (1, '2301', NOW(), NOW())
ON DUPLICATE KEY UPDATE created_at = VALUES(created_at);

-- 将作业2发布到班级2301
INSERT INTO rel_exercise_class (exercise_id, class_code, created_at, updated_at)
VALUES (2, '2301', NOW(), NOW())
ON DUPLICATE KEY UPDATE created_at = VALUES(created_at);

-- ==========================================
-- 7. 创建学生答题记录（模拟学生提交）
-- ==========================================

-- 获取学生ID
SET @student_id_1 = (SELECT id FROM auth_student WHERE student_code = '23201321' LIMIT 1);
SET @student_id_2 = (SELECT id FROM auth_student WHERE student_code = '23201322' LIMIT 1);

-- 学生1提交作业1的答案（如果学生存在）
-- 先删除已有记录避免重复
DELETE FROM res_exercise_record WHERE exercise_id = 1 AND student_id = @student_id_1;

INSERT INTO res_exercise_record (exercise_id, item_id, student_id, choice_answer, text_content, score, submitted_at, grading_status, comment, created_at, updated_at)
VALUES (1, 1, @student_id_1, 'B', NULL, 5, NOW(), 1, NULL, NOW(), NOW());

INSERT INTO res_exercise_record (exercise_id, item_id, student_id, choice_answer, text_content, score, submitted_at, grading_status, comment, created_at, updated_at)
VALUES (1, 2, @student_id_1, 'AB', NULL, 5, NOW(), 1, NULL, NOW(), NOW());

INSERT INTO res_exercise_record (exercise_id, item_id, student_id, choice_answer, text_content, score, submitted_at, grading_status, comment, created_at, updated_at)
VALUES (1, 3, @student_id_1, 'F', NULL, 3, NOW(), 1, NULL, NOW(), NOW());

INSERT INTO res_exercise_record (exercise_id, item_id, student_id, choice_answer, text_content, score, submitted_at, grading_status, comment, created_at, updated_at)
VALUES (1, 4, @student_id_1, NULL, '角色旅程图|验收边界', 6, NOW(), 1, NULL, NOW(), NOW());

INSERT INTO res_exercise_record (exercise_id, item_id, student_id, choice_answer, text_content, score, submitted_at, grading_status, comment, created_at, updated_at)
VALUES (1, 5, @student_id_1, NULL, '角色旅程图帮助团队理解用户在系统中的完整交互流程，明确各阶段的目标和触点。在教学平台中，它帮助区分学生、教师两类角色的任务链路。', NULL, NOW(), 0, NULL, NOW(), NOW());

-- 学生2提交作业1的部分答案
DELETE FROM res_exercise_record WHERE exercise_id = 1 AND student_id = @student_id_2;

INSERT INTO res_exercise_record (exercise_id, item_id, student_id, choice_answer, text_content, score, submitted_at, grading_status, comment, created_at, updated_at)
VALUES (1, 1, @student_id_2, 'B', NULL, 5, NOW(), 1, NULL, NOW(), NOW());

INSERT INTO res_exercise_record (exercise_id, item_id, student_id, choice_answer, text_content, score, submitted_at, grading_status, comment, created_at, updated_at)
VALUES (1, 2, @student_id_2, 'ABC', NULL, 0, NOW(), 1, '答案错误，正确答案是AB', NOW(), NOW());

INSERT INTO res_exercise_record (exercise_id, item_id, student_id, choice_answer, text_content, score, submitted_at, grading_status, comment, created_at, updated_at)
VALUES (1, 3, @student_id_2, 'T', NULL, 0, NOW(), 1, '答案错误，正确答案是F', NOW(), NOW());

-- ==========================================
-- 8. 验证数据
-- ==========================================

SELECT '=== 作业列表 ===' AS section;
SELECT id, task_name, publish_status FROM edu_exercise ORDER BY id;

SELECT '=== 作业题目 ===' AS section;
SELECT id, exercise_id, question, question_type, max_score FROM edu_exercise_item ORDER BY id;

SELECT '=== 班级发布 ===' AS section;
SELECT exercise_id, class_code FROM rel_exercise_class ORDER BY exercise_id;

SELECT '=== 答题记录 ===' AS section;
SELECT id, exercise_id, item_id, student_id, score, grading_status FROM res_exercise_record ORDER BY id;

SELECT '=== 成绩汇总 ===' AS section;
SELECT
  s.student_name,
  e.task_name,
  SUM(rer.score) as total_score,
  COUNT(rer.id) as answered_count,
  SUM(CASE WHEN rer.grading_status > 0 THEN 1 ELSE 0 END) as graded_count
FROM res_exercise_record rer
JOIN auth_student s ON rer.student_id = s.id
JOIN edu_exercise e ON rer.exercise_id = e.id
GROUP BY s.student_name, e.task_name;