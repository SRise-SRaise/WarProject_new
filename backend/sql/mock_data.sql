-- 完整示例数据（覆盖 backend/sql/init-tabel.sql 中的主要表）
-- 目标：一键导入即可在前后端跑通登录 / 班级 / 实验 / 作业 / 考试 / 日志等核心链路
-- 特性：尽量可重复执行（固定主键 + ON DUPLICATE KEY UPDATE）
--
-- 账号说明（与后端 AuthLoginServiceImpl 口径一致）
-- 密码统一：123456
-- 存储：MD5(SALT + 明文) = MD5("springboot" + "123456") = a384380c440fb620eb080df5cbfcd0f0
--
-- 教师：admin / 123456（角色选 teacher）
-- 学生：23201321~23201324 / 123456（角色选 student）
--
-- 注意：
-- - 若你前面有 Nginx 代理，请保证 X-Forwarded-For / X-Real-IP 透传，否则登录 IP 可能显示为 ::1/127.0.0.1
-- - 本脚本不负责建表；请先执行 init-tabel.sql

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS edu_hub;
USE edu_hub;

START TRANSACTION;

-- =====================================================
-- 0) 清理旧数据（可重复执行 / 重置自增）
--    说明：在 FOREIGN_KEY_CHECKS=0 下 TRUNCATE，避免外键顺序问题
-- =====================================================

TRUNCATE TABLE `edu_experiment_attachment`;
TRUNCATE TABLE `sys_admin_log`;
TRUNCATE TABLE `sys_student_log`;
TRUNCATE TABLE `sys_config`;
TRUNCATE TABLE `edu_lecture`;
TRUNCATE TABLE `res_exam_summary`;
TRUNCATE TABLE `res_exam_record`;
TRUNCATE TABLE `rel_paper_question`;
TRUNCATE TABLE `edu_exam`;
TRUNCATE TABLE `edu_paper`;
TRUNCATE TABLE `edu_question_bank`;
TRUNCATE TABLE `res_exercise_record`;
TRUNCATE TABLE `rel_exercise_class`;
TRUNCATE TABLE `rel_exercise_item`;
TRUNCATE TABLE `edu_exercise_item`;
TRUNCATE TABLE `edu_exercise`;
TRUNCATE TABLE `t_score`;
TRUNCATE TABLE `t_student_item_log`;
TRUNCATE TABLE `t_student_item`;
TRUNCATE TABLE `t_student_answer`;
TRUNCATE TABLE `t_experiment_item`;
TRUNCATE TABLE `t_experiment`;
TRUNCATE TABLE `t_question_type`;
TRUNCATE TABLE `auth_assistant`;
TRUNCATE TABLE `auth_student`;
TRUNCATE TABLE `auth_teacher`;
TRUNCATE TABLE `auth_class`;

-- =====================================================
-- 1) 班级 / 教师 / 学生 / 助教（Auth Module）
-- =====================================================

INSERT INTO `t_question_type` (`type_id`, `type_name`)
VALUES (1, '填空'),
       (2, '单选'),
       (3, '多选'),
       (4, '判断'),
       (5, '简答'),
       (6, '编程'),
       (7, '综合')
ON DUPLICATE KEY UPDATE type_name = VALUES(type_name);

INSERT INTO auth_class (class_code, headmaster_name, class_status, created_at, updated_at)
VALUES ('2301', '张老师（示例班主任）', 0, NOW(), NOW()),
       ('2302', '李老师（示例班主任）', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE headmaster_name = VALUES(headmaster_name),
                        class_status = VALUES(class_status),
                        updated_at = VALUES(updated_at);

-- 教师（固定 id=1，方便其他表 teacher_id 引用）
INSERT INTO auth_teacher (id, username, password_md5, real_name, created_at, updated_at)
VALUES (1, 'admin', 'a384380c440fb620eb080df5cbfcd0f0', '管理员教师', NOW(), NOW())
ON DUPLICATE KEY UPDATE username = VALUES(username),
                        password_md5 = VALUES(password_md5),
                        real_name = VALUES(real_name),
                        updated_at = VALUES(updated_at);

-- 学生（固定 id，便于引用）
INSERT INTO auth_student (id, student_code, student_name, password_md5, class_code, remark, account_status, login_fail_count, last_login_ip, created_at, updated_at)
VALUES (101, '23201321', '廖同学', 'a384380c440fb620eb080df5cbfcd0f0', '2301', '示例学生A', 0, 0, '10.86.64.103', NOW(), NOW()),
       (102, '23201322', '刘同学', 'a384380c440fb620eb080df5cbfcd0f0', '2301', '示例学生B', 0, 0, '10.86.64.104', NOW(), NOW()),
       (103, '23201323', '罗同学', 'a384380c440fb620eb080df5cbfcd0f0', '2301', '示例学生C', 0, 1, '10.86.64.105', NOW(), NOW()),
       (104, '23201324', '钱同学', 'a384380c440fb620eb080df5cbfcd0f0', '2302', '示例学生D（ip限制班级）', 0, 0, '10.86.64.106', NOW(), NOW())
ON DUPLICATE KEY UPDATE student_code = VALUES(student_code),
                        student_name = VALUES(student_name),
                        password_md5 = VALUES(password_md5),
                        class_code = VALUES(class_code),
                        remark = VALUES(remark),
                        account_status = VALUES(account_status),
                        login_fail_count = VALUES(login_fail_count),
                        last_login_ip = VALUES(last_login_ip),
                        updated_at = VALUES(updated_at);

-- 助教（password_hash 字段与 teacher/student 不同，这里给个示例值即可）
INSERT INTO auth_assistant (id, username, password_hash, bind_student_code, real_name, class_code, created_at, updated_at)
VALUES (201, 'assistant01', 'a384380c440fb620eb080df5cbfcd0f0', '23201321', '王助教', '2301', NOW(), NOW())
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash),
                        bind_student_code = VALUES(bind_student_code),
                        real_name = VALUES(real_name),
                        class_code = VALUES(class_code),
                        updated_at = VALUES(updated_at);

-- =====================================================
-- 2) 实验与实验子项目（Experiment Module）
-- =====================================================

INSERT INTO t_experiment (experiment_id, experiment_no, experiment_name, experiment_type, instruction_type, experiment_requirement, experiment_content, state)
VALUES (1, 1, '实验一：Java 基础与输入输出', 1, 'pdf', '按要求完成输入输出与基本语法练习。', '包含填空/判断/简答/编程等子项目。', 1),
       (2, 2, '实验二：SQL 基础与查询', 1, 'pdf', '完成基本 DDL/DML 与查询题。', '关注条件查询、聚合与分组。', 1)
ON DUPLICATE KEY UPDATE experiment_no = VALUES(experiment_no),
                        experiment_name = VALUES(experiment_name),
                        experiment_type = VALUES(experiment_type),
                        instruction_type = VALUES(instruction_type),
                        experiment_requirement = VALUES(experiment_requirement),
                        experiment_content = VALUES(experiment_content),
                        state = VALUES(state);

INSERT INTO t_experiment_item (experiment_item_id, experiment_item_no, experiment_item_name, experiment_item_type, experiment_item_content, experiment_id, experiment_item_answer, experiment_item_score, state)
VALUES
  (11, 1, '填空：Java main 方法入口', 1, '请填写 Java 程序入口方法签名中的关键字。', 1, 'public static void main', 5, 1),
  (12, 2, '判断：String 可变', 4, 'Java 中 String 是可变对象。', 1, 'F', 3, 1),
  (13, 3, '简答：异常与错误区别', 5, '简述 Exception 与 Error 的区别。', 1, NULL, 10, 1),
  (14, 4, '编程：读取一行并输出', 6, '读取一行字符串并原样输出。', 1, NULL, 20, 1),
  (21, 1, '单选：SQL 聚合函数', 2, '以下哪个是聚合函数？', 2, 'COUNT', 5, 1),
  (22, 2, '多选：WHERE 子句可用操作符', 3, '哪些可以用于 WHERE？', 2, 'IN,LIKE,BETWEEN', 8, 1)
ON DUPLICATE KEY UPDATE experiment_item_no = VALUES(experiment_item_no),
                        experiment_item_name = VALUES(experiment_item_name),
                        experiment_item_type = VALUES(experiment_item_type),
                        experiment_item_content = VALUES(experiment_item_content),
                        experiment_id = VALUES(experiment_id),
                        experiment_item_answer = VALUES(experiment_item_answer),
                        experiment_item_score = VALUES(experiment_item_score),
                        state = VALUES(state);

-- 学生实验提交（t_student_item / t_student_item_log / t_student_answer / t_score）
INSERT INTO t_student_item (student_item_id, student_id, item_id, content, score, fill_time, score_flag)
VALUES
  (1001, 101, 11, 'public static void main', 5, NOW(), 1),
  (1002, 101, 12, 'F', 3, NOW(), 1),
  (1003, 101, 13, 'Exception 表示可处理异常；Error 多为严重错误一般不处理。', 9, NOW(), 2),
  (1004, 102, 11, 'public static void main', 5, NOW(), 1),
  (1005, 102, 12, 'T', 0, NOW(), 1)
ON DUPLICATE KEY UPDATE content = VALUES(content),
                        score = VALUES(score),
                        fill_time = VALUES(fill_time),
                        score_flag = VALUES(score_flag);

INSERT INTO t_student_item_log (log_id, student_item, content, fill_time)
VALUES
  (2001, 1003, '第一次提交：Exception 和 Error 的概念区别（草稿）', NOW()),
  (2002, 1003, '第二次提交：补充可恢复性与 JVM 层错误', NOW())
ON DUPLICATE KEY UPDATE content = VALUES(content),
                        fill_time = VALUES(fill_time);

-- 学生填空答案去重依赖 content_hash，这里用固定值便于重复执行
INSERT INTO t_student_answer (id, item_id, fill_no, content, content_hash, count, is_correct)
VALUES
  (3001, 11, 1, 'public static void main', '0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f', 1, b'1')
ON DUPLICATE KEY UPDATE content = VALUES(content),
                        count = VALUES(count),
                        is_correct = VALUES(is_correct);

INSERT INTO t_score (score_id, student_id, experiment_id, score)
VALUES
  (4001, 101, 1, 17),
  (4002, 102, 1, 5)
ON DUPLICATE KEY UPDATE score = VALUES(score);

-- =====================================================
-- 3) 练习/作业（Exercise Module）
-- =====================================================

INSERT INTO edu_exercise (id, sort_order, task_name, teacher_id, relate_exp_id, interact_mode, description, publish_status, start_time, end_time, created_at, updated_at)
VALUES
  (1, 1, '作业一：角色旅程拆解（示例）', 1, NULL, 1, '用于测试班级发布、题目列表、答题记录。', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), NOW(), NOW()),
  (2, 2, '作业二：Vue3 组合式 API（示例）', 1, NULL, 1, '用于测试客观题与主观题混合。', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), NOW(), NOW())
ON DUPLICATE KEY UPDATE task_name = VALUES(task_name),
                        publish_status = VALUES(publish_status),
                        updated_at = VALUES(updated_at);

INSERT INTO edu_exercise_item (id, question, options_text, standard_answer, question_type, max_score, question_bank_id, analysis, difficulty, created_at, updated_at)
VALUES
  (1, '以下哪项最适合作为角色旅程的首层分段？', 'A. 按数据库表名分段,B. 按用户任务阶段分段,C. 按代码文件目录分段', 'B', 2, 5, NULL, '角色旅程应按用户任务阶段拆分。', 2, NOW(), NOW()),
  (2, '验收边界描述中以下哪些属于关键内容？（多选）', 'A. 通过条件,B. 失败条件,C. 界面颜色与字号,D. 开发者个人偏好', 'AB', 3, 5, NULL, '验收边界聚焦通过/失败条件。', 3, NOW(), NOW()),
  (3, '角色旅程图只需要描述主流程，不需要描述异常流。', '', 'F', 4, 3, NULL, '异常流同样重要。', 2, NOW(), NOW()),
  (4, '在需求分析中，___用于描述交互过程，而___定义验收通过条件。', '角色旅程图|验收边界', '角色旅程图|验收边界', 1, 6, NULL, '概念区分。', 3, NOW(), NOW()),
  (5, '请简述角色旅程图的核心价值。', '', '', 5, 10, NULL, '主观题。', 4, NOW(), NOW()),
  (6, 'Vue3 的组合式 API 使用哪个函数创建响应式数据？', 'A. reactive,B. ref,C. computed,D. watch', 'B', 2, 5, NULL, 'ref 常用于基础类型。', 2, NOW(), NOW()),
  (7, '以下哪些是 Vue3 的特性？（多选）', 'A. 组合式API,B. 更好的TypeScript支持,C. 更快的渲染性能,D. 兼容IE8', 'ABC', 3, 5, NULL, 'Vue3 不支持 IE8。', 2, NOW(), NOW()),
  (8, '请简述 Vue3 相比 Vue2 的主要改进。', '', '', 5, 20, NULL, '主观题。', 4, NOW(), NOW())
ON DUPLICATE KEY UPDATE question = VALUES(question),
                        options_text = VALUES(options_text),
                        standard_answer = VALUES(standard_answer),
                        question_type = VALUES(question_type),
                        max_score = VALUES(max_score),
                        analysis = VALUES(analysis),
                        difficulty = VALUES(difficulty),
                        updated_at = VALUES(updated_at);

INSERT INTO rel_exercise_item (exercise_id, item_id, item_order, item_score, created_at, updated_at)
VALUES
  (1, 1, 1, 5, NOW(), NOW()),
  (1, 2, 2, 5, NOW(), NOW()),
  (1, 3, 3, 3, NOW(), NOW()),
  (1, 4, 4, 6, NOW(), NOW()),
  (1, 5, 5, 10, NOW(), NOW()),
  (2, 6, 1, 5, NOW(), NOW()),
  (2, 7, 2, 5, NOW(), NOW()),
  (2, 8, 3, 20, NOW(), NOW())
ON DUPLICATE KEY UPDATE item_order = VALUES(item_order),
                        item_score = VALUES(item_score),
                        updated_at = VALUES(updated_at);

INSERT INTO rel_exercise_class (exercise_id, class_code, created_at, updated_at)
VALUES (1, '2301', NOW(), NOW()),
       (2, '2301', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);

-- 学生作业答题记录：本示例保持为空，确保学生端进入后显示“待完成”而非“已完成/逾期”

-- =====================================================
-- 4) 题库 / 试卷 / 考试（Exam Module）
-- =====================================================

INSERT INTO edu_question_bank (id, question_content, question_type, options_text, standard_answer, analysis, difficulty, creator_teacher_id, created_at, updated_at)
VALUES
  (9001, 'Java 中 int 的默认值是？', 2, 'A.0,B.1,C.null,D.undefined', 'A', '基础类型默认值。', 1, 1, NOW(), NOW()),
  (9002, 'SQL 中用于过滤行的是？', 2, 'A.SELECT,B.WHERE,C.GROUP BY,D.ORDER BY', 'B', 'WHERE 用于过滤。', 1, 1, NOW(), NOW()),
  (9003, '判断：LEFT JOIN 会保留左表全部记录。', 4, 'T/F', 'T', '外连接语义。', 2, 1, NOW(), NOW()),
  (9004, '简答：解释数据库索引的作用。', 5, NULL, NULL, '主观题需人工批改。', 3, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE question_content = VALUES(question_content),
                        question_type = VALUES(question_type),
                        options_text = VALUES(options_text),
                        standard_answer = VALUES(standard_answer),
                        analysis = VALUES(analysis),
                        difficulty = VALUES(difficulty),
                        updated_at = VALUES(updated_at);

INSERT INTO edu_paper (id, paper_code, paper_name, description, generation_time, created_at, updated_at)
VALUES (8001, 1, '期中测验A卷（示例）', '用于验证试卷组卷与自动判分触发器。', NOW(), NOW(), NOW())
ON DUPLICATE KEY UPDATE paper_name = VALUES(paper_name),
                        description = VALUES(description),
                        generation_time = VALUES(generation_time),
                        updated_at = VALUES(updated_at);

INSERT INTO rel_paper_question (id, paper_id, question_id, score, question_order, section_name, created_at, updated_at)
VALUES
  (8101, 8001, 9001, 5, 1, '单选题', NOW(), NOW()),
  (8102, 8001, 9002, 5, 2, '单选题', NOW(), NOW()),
  (8103, 8001, 9003, 5, 3, '判断题', NOW(), NOW()),
  (8104, 8001, 9004, 10, 4, '简答题', NOW(), NOW())
ON DUPLICATE KEY UPDATE score = VALUES(score),
                        question_order = VALUES(question_order),
                        section_name = VALUES(section_name),
                        updated_at = VALUES(updated_at);

INSERT INTO edu_exam (id, exam_name, paper_id, duration_min, start_time, end_time, is_published, created_at, updated_at)
VALUES (7001, '期中测验（示例）', 8001, 45, '2026-04-15 09:00:00', '2026-04-15 09:45:00', b'1', NOW(), NOW())
ON DUPLICATE KEY UPDATE exam_name = VALUES(exam_name),
                        paper_id = VALUES(paper_id),
                        duration_min = VALUES(duration_min),
                        start_time = VALUES(start_time),
                        end_time = VALUES(end_time),
                        is_published = VALUES(is_published),
                        updated_at = VALUES(updated_at);

-- 学生考试答题：触发器 trg_res_exam_record_auto_grade_before_insert 会自动给客观题打分

INSERT INTO res_exam_record (exam_id, paper_id, paper_question_id, student_id, question_id, student_answer, grading_status, comment, created_at, updated_at)
VALUES
  (7001, 8001, 8101, 101, 9001, 'A', 0, NULL, NOW(), NOW()),
  (7001, 8001, 8102, 101, 9002, 'B', 0, NULL, NOW(), NOW()),
  (7001, 8001, 8103, 101, 9003, 'T', 0, NULL, NOW(), NOW()),
  (7001, 8001, 8104, 101, 9004, '索引用于加速查询，通过维护有序结构减少扫描。', 0, NULL, NOW(), NOW()),
  (7001, 8001, 8101, 102, 9001, 'B', 0, NULL, NOW(), NOW()),
  (7001, 8001, 8102, 102, 9002, 'B', 0, NULL, NOW(), NOW()),
  (7001, 8001, 8103, 102, 9003, 'F', 0, NULL, NOW(), NOW());

INSERT INTO res_exam_summary (id, exam_id, student_id, paper_id, total_score, objective_score, subjective_score, created_at, updated_at)
VALUES
  (6001, 7001, 101, 8001, NULL, NULL, NULL, NOW(), NOW()),
  (6002, 7001, 102, 8001, NULL, NULL, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE paper_id = VALUES(paper_id),
                        updated_at = VALUES(updated_at);

-- =====================================================
-- 5) 课件 / 系统配置 / 日志（System Module）
-- =====================================================

INSERT INTO edu_lecture (id, lecture_name, category_id, file_extension, file_path, created_at, updated_at)
VALUES
  (1, '第一讲：课程导论（示例）', 1, 'pdf', '/files/lecture_material/materials/lecture-01.pdf', NOW(), NOW()),
  (2, '第二讲：SQL 基础（示例）', 1, 'pptx', '/files/lecture_material/materials/lecture-02.pptx', NOW(), NOW())
ON DUPLICATE KEY UPDATE lecture_name = VALUES(lecture_name),
                        category_id = VALUES(category_id),
                        file_extension = VALUES(file_extension),
                        file_path = VALUES(file_path),
                        updated_at = VALUES(updated_at);

INSERT INTO sys_config (config_key, config_value, created_at, updated_at)
VALUES
  ('site_name', 'EduHub 教学平台', NOW(), NOW()),
  ('login_salt', 'springboot', NOW(), NOW()),
  ('upload_root', '/files', NOW(), NOW())
ON DUPLICATE KEY UPDATE config_value = VALUES(config_value),
                        updated_at = VALUES(updated_at);

INSERT INTO sys_student_log (id, account, action_type, action_detail, op_time, client_ip, created_at)
VALUES
  (1, '23201321', 1, '学生登录', NOW(), '10.86.64.103', NOW()),
  (2, '23201321', 2, '进入作业列表', NOW(), '10.86.64.103', NOW()),
  (3, '23201322', 1, '学生登录', NOW(), '10.86.64.104', NOW())
ON DUPLICATE KEY UPDATE action_detail = VALUES(action_detail),
                        op_time = VALUES(op_time),
                        client_ip = VALUES(client_ip);

INSERT INTO sys_admin_log (id, account, action_type, action_detail, op_time, client_ip, created_at)
VALUES
  (1, 'admin', 1, '教师登录', NOW(), '10.86.64.1', NOW()),
  (2, 'admin', 2, '发布作业到班级 2301', NOW(), '10.86.64.1', NOW()),
  (3, 'admin', 3, '创建考试：期中测验（示例）', NOW(), '10.86.64.1', NOW())
ON DUPLICATE KEY UPDATE action_detail = VALUES(action_detail),
                        op_time = VALUES(op_time),
                        client_ip = VALUES(client_ip);

-- =====================================================
-- 6) 实验附件（Attachment Module）
-- =====================================================

INSERT INTO edu_experiment_attachment (id, result_id, student_id, file_name, file_size, file_type, file_suffix, obs_url, obs_bucket, upload_status, is_deleted, created_at, updated_at)
VALUES
  (1, 1001, 101, 'report-lab1.docx', 102400, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'docx',
   '/obs/mock/report-lab1.docx', 'edu-hub-mock', 1, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE result_id = VALUES(result_id),
                        student_id = VALUES(student_id),
                        file_name = VALUES(file_name),
                        file_size = VALUES(file_size),
                        file_type = VALUES(file_type),
                        file_suffix = VALUES(file_suffix),
                        obs_url = VALUES(obs_url),
                        obs_bucket = VALUES(obs_bucket),
                        upload_status = VALUES(upload_status),
                        is_deleted = VALUES(is_deleted),
                        updated_at = VALUES(updated_at);

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

