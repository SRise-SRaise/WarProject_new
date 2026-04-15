-- 基础初始化数据
USE edu_hub;

-- ==========================================
-- 1. 班级基础数据
-- ==========================================
INSERT INTO `auth_class` (`class_code`, `headmaster_name`, `class_status`)
VALUES ('2301', '实验班级', 0)
ON DUPLICATE KEY UPDATE
    `headmaster_name` = VALUES(`headmaster_name`),
    `class_status` = VALUES(`class_status`);

-- ==========================================
-- 2. 教师基础数据
-- 账号：admin
-- 密码：123456（MD5: e10adc3949ba59abbe56e057f20f883e）
-- ==========================================
INSERT INTO `auth_teacher` (`username`, `password_md5`, `real_name`)
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员教师')
ON DUPLICATE KEY UPDATE
    `password_md5` = VALUES(`password_md5`),
    `real_name` = VALUES(`real_name`);

-- ==========================================
-- 3. 学生基础数据
-- 密码统一：123456（MD5: e10adc3949ba59abbe56e057f20f883e）
-- ==========================================
INSERT INTO `auth_student` (`student_code`, `student_name`, `password_md5`, `class_code`, `remark`, `account_status`)
VALUES ('23201321', '廖同学', 'e10adc3949ba59abbe56e057f20f883e', '2301', '基础初始化学生', 0),
       ('23201322', '刘同学', 'e10adc3949ba59abbe56e057f20f883e', '2301', '基础初始化学生', 0),
       ('23201323', '罗同学', 'e10adc3949ba59abbe56e057f20f883e', '2301', '基础初始化学生', 0),
       ('23201324', '钱同学', 'e10adc3949ba59abbe56e057f20f883e', '2301', '基础初始化学生', 0)
ON DUPLICATE KEY UPDATE
    `student_name` = VALUES(`student_name`),
    `password_md5` = VALUES(`password_md5`),
    `class_code` = VALUES(`class_code`),
    `remark` = VALUES(`remark`),
    `account_status` = VALUES(`account_status`);
