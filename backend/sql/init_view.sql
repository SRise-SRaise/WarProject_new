
USE edu_hub;

-- 1. v_clazz_info (班级信息统计)
CREATE OR REPLACE VIEW `v_clazz_info`
AS
SELECT MAX(`class_code`) AS `maxId`, '----' AS `lastAccess`, COUNT(`class_code`) AS `count`
FROM `auth_class`;

-- 2. v_student_answer_data_info (学生实验提交统计)
CREATE OR REPLACE VIEW `v_student_answer_data_info`
AS
SELECT MAX(`id`) AS `maxId`, MAX(`submitted_at`) AS `lastAccess`, COUNT(`id`) AS `count`
FROM `res_experiment_result`;

-- 3. v_student_answer_log_info (学生实验提交日志统计)
CREATE OR REPLACE VIEW `v_student_answer_log_info`
AS
SELECT MAX(`id`) AS `maxId`, MAX(`snapshot_time`) AS `lastAccess`, COUNT(`id`) AS `count`
FROM `res_submission_log`;

-- 4. v_sys_log_info (系统日志统计)
CREATE OR REPLACE VIEW `v_sys_log_info`
AS
SELECT MAX(`id`) AS `maxId`, MAX(`op_time`) AS `lastAccess`, COUNT(`id`) AS `count`
FROM `sys_admin_log`;

-- 5. v_student_info (学生信息统计)
CREATE OR REPLACE VIEW `v_student_info`
AS
SELECT MAX(`id`) AS `maxId`, MAX(`last_login_ip`) AS `lastAccess`, COUNT(`id`) AS `count`
FROM `auth_student`;

-- 6. v_student_experiment_score (核心：学生-实验维度的基础成绩视图)
CREATE OR REPLACE VIEW `v_student_experiment_score`
AS
SELECT r.`score`       AS `score`,
       r.`student_id`  AS `student_id`,
       e.`id`          AS `experiment_id`,
       e.`sort_order`  AS `experiment_no`,
       e.`name`        AS `experiment_name`,
       e.`category_id` AS `experiment_type`,
       e.`file_type`   AS `instruction_type`
FROM `edu_experiment` e
         LEFT JOIN ( `res_experiment_result` r JOIN `edu_experiment_item` i ON r.`item_id` = i.`id` )
                   ON i.`experiment_id` = e.`id`;

-- 7. v_clazz_experiments_score (展示班级维度下各学生的各实验总分)
CREATE OR REPLACE VIEW `v_clazz_experiments_score`
AS
SELECT v.`student_id`                                                            AS `student_id`,
       ( SELECT `student_code` FROM `auth_student` WHERE `id` = v.`student_id` ) AS `student_no`,
       ( SELECT `student_name` FROM `auth_student` WHERE `id` = v.`student_id` ) AS `student_name`,
       ( SELECT `class_code` FROM `auth_student` WHERE `id` = v.`student_id` )   AS `clazz_no`,
       v.`experiment_id`                                                         AS `experiment_id`,
       v.`experiment_no`                                                         AS `experiment_no`,
       SUM(v.`score`)                                                            AS `score`
FROM `v_student_experiment_score` v
GROUP BY v.`student_id`, v.`experiment_id`;

-- 8. v_student_expeirment_items (学生实验子项目联合视图)
CREATE OR REPLACE VIEW `v_student_expeirment_items`
AS
SELECT i.`sort_order`    AS `itemNo`,
       i.`item_name`     AS `itemName`,
       i.`question_type` AS `itemType`,
       r.`score`         AS `score`,
       r.`submitted_at`  AS `fillTime`,
       r.`student_id`    AS `studentId`,
       i.`id`            AS `itemId`,
       i.`experiment_id` AS `experimentId`
FROM `edu_experiment_item` i
         JOIN `res_experiment_result` r ON r.`item_id` = i.`id`;