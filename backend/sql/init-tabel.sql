-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- 建表时先关闭外键检查

CREATE DATABASE IF NOT EXISTS edu_hub;
USE edu_hub;

-- ==========================================
-- 1. 权限与组织架构模块 (Auth Module)
-- ==========================================

DROP TABLE IF EXISTS `auth_class`;
CREATE TABLE `auth_class`
(
    `class_code`      VARCHAR(6) NOT NULL COMMENT '班级编号',
    `headmaster_name` TEXT COMMENT '班主任姓名/备注',
    `class_status`    INT                 DEFAULT 0 COMMENT '班级状态：0正常, 1归档',
    `created_at`      DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`class_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='班级信息表';

DROP TABLE IF EXISTS `auth_student`;
CREATE TABLE `auth_student`
(
    `id`               BIGINT      NOT NULL AUTO_INCREMENT COMMENT '学生主键ID',
    `student_code`     VARCHAR(8)  NOT NULL COMMENT '学号',
    `student_name`     VARCHAR(20) NOT NULL COMMENT '学生姓名',
    `password_md5`     CHAR(32)    NOT NULL COMMENT '登录密码',
    `class_code`       VARCHAR(6)  NOT NULL COMMENT '所属班级编号',
    `remark`           TEXT COMMENT '备注',
    `account_status`   INT                  DEFAULT 0 COMMENT '账号状态(0正常)',
    `login_fail_count` INT                  DEFAULT 0 COMMENT '连续登录失败次数',
    `last_login_ip`    VARCHAR(20)          DEFAULT NULL COMMENT '最近一次登录IP',
    `created_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_code` (`student_code`),
    KEY `fk_auth_student_class` (`class_code`),
    CONSTRAINT `fk_auth_student_class` FOREIGN KEY (`class_code`) REFERENCES `auth_class` (`class_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学生信息表';

DROP TABLE IF EXISTS `auth_teacher`;
CREATE TABLE `auth_teacher`
(
    `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '教师主键ID',
    `username`     VARCHAR(6)  NOT NULL COMMENT '教师工号',
    `password_md5` CHAR(32)    NOT NULL COMMENT '登录密码',
    `real_name`    VARCHAR(20) NOT NULL COMMENT '教师真实姓名',
    `created_at`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_teacher_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='教师信息表';

DROP TABLE IF EXISTS `auth_assistant`;
CREATE TABLE `auth_assistant`
(
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '助教主键ID',
    `username`          VARCHAR(255) NOT NULL COMMENT '助教登录账号',
    `password_hash`     VARCHAR(255)          DEFAULT NULL COMMENT '登录密码',
    `bind_student_code` VARCHAR(8)            DEFAULT NULL COMMENT '关联的学生学号',
    `real_name`         VARCHAR(255)          DEFAULT NULL COMMENT '助教真实姓名',
    `class_code`        VARCHAR(6)            DEFAULT NULL COMMENT '所属班级编号',
    `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_assistant_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='助教信息表';

-- ==========================================
-- 2. 实验核心业务 (Experiment Module)
-- ==========================================

DROP TABLE IF EXISTS `edu_question_type`;
CREATE TABLE `edu_question_type`
(
    `type_id`    INT      NOT NULL COMMENT '类型编号',
    `type_name`  VARCHAR(10)       DEFAULT NULL COMMENT '类型名称',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`type_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='题目类型字典表';
-- 初始数据植入
INSERT INTO `edu_question_type` (`type_id`, `type_name`)
VALUES (1, '填空'),
       (2, '单选'),
       (3, '多选'),
       (4, '判断'),
       (5, '简答'),
       (6, '编程'),
       (7, '综合');

DROP TABLE IF EXISTS `edu_experiment`;
CREATE TABLE `edu_experiment`
(
    `id`             BIGINT      NOT NULL AUTO_INCREMENT COMMENT '实验项目主键ID',
    `sort_order`     INT         NOT NULL COMMENT '实验序号(排序)',
    `name`           VARCHAR(30) NOT NULL COMMENT '实验名称',
    `category_id`    INT                  DEFAULT NULL COMMENT '实验类型',
    `file_type`      VARCHAR(10)          DEFAULT NULL COMMENT '指导书文件类型',
    `requirement`    TEXT COMMENT '实验要求',
    `content_desc`   TEXT COMMENT '实验内容描述',
    `publish_status` INT                  DEFAULT NULL COMMENT '发布状态',
    `created_at`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_experiment_sort` (`sort_order`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='实验项目表';

DROP TABLE IF EXISTS `edu_experiment_item`;
CREATE TABLE `edu_experiment_item`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '子项目主键ID',
    `sort_order`       INT          NOT NULL COMMENT '子项目序号',
    `item_name`        VARCHAR(100) NOT NULL COMMENT '子项目名称',
    `question_type`    INT          NOT NULL COMMENT '子项目类型',
    `question_content` TEXT COMMENT '题目要求',
    `experiment_id`    BIGINT       NOT NULL COMMENT '所属实验ID',
    `standard_answer`  VARCHAR(255)          DEFAULT NULL COMMENT '标准答案',
    `max_score`        TINYINT               DEFAULT NULL COMMENT '该项满分',
    `item_status`      INT                   DEFAULT NULL COMMENT '状态',
    `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_exp_item_sort` (`sort_order`),
    KEY `fk_exp_item_exp` (`experiment_id`),
    CONSTRAINT `fk_exp_item_exp` FOREIGN KEY (`experiment_id`) REFERENCES `edu_experiment` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='实验子项目评分项表';

-- ==========================================
-- 3. 课内练习模块 (Exercise Module)
-- ==========================================

DROP TABLE IF EXISTS `edu_exercise`;
CREATE TABLE `edu_exercise`
(
    `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '练习主键ID',
    `sort_order`    INT               DEFAULT NULL COMMENT '练习序号',
    `task_name`     VARCHAR(255)      DEFAULT NULL COMMENT '练习名称',
    `relate_exp_id` BIGINT            DEFAULT NULL COMMENT '关联实验编号',
    `interact_mode` INT               DEFAULT NULL COMMENT '练习类型',
    `description`   VARCHAR(255)      DEFAULT NULL COMMENT '练习描述',
    `start_time`    DATETIME          DEFAULT NULL COMMENT '开放时间',
    `end_time`      DATETIME          DEFAULT NULL COMMENT '截止时间',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='课内练习表';

DROP TABLE IF EXISTS `edu_exercise_item`;
CREATE TABLE `edu_exercise_item`
(
    `id`              BIGINT   NOT NULL AUTO_INCREMENT COMMENT '练习题目主键ID',
    `exercise_id`     BIGINT   NOT NULL COMMENT '所属练习ID',
    `question`        VARCHAR(255)      DEFAULT NULL COMMENT '题目题干',
    `options_text`    VARCHAR(255)      DEFAULT NULL COMMENT '选项',
    `standard_answer` VARCHAR(255)      DEFAULT NULL COMMENT '标准答案',
    `question_type`   INT               DEFAULT NULL COMMENT '题目类型',
    `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_exe_item_exe` (`exercise_id`),
    CONSTRAINT `fk_exe_item_exe` FOREIGN KEY (`exercise_id`) REFERENCES `edu_exercise` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='练习题目表';

-- ==========================================
-- 4. 考试与题库 (Exam Module)
-- ==========================================

DROP TABLE IF EXISTS `edu_question_bank`;
CREATE TABLE `edu_question_bank`
(
    `id`               BIGINT   NOT NULL AUTO_INCREMENT COMMENT '题目主键ID',
    `question_content` VARCHAR(255)      DEFAULT NULL COMMENT '题目题干',
    `standard_answer`  VARCHAR(255)      DEFAULT NULL COMMENT '标准答案',
    `question_type`    INT               DEFAULT NULL COMMENT '题目类型',
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='题目表/题库';

DROP TABLE IF EXISTS `edu_exam`;
CREATE TABLE `edu_exam`
(
    `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '考试主键ID',
    `exam_name`    VARCHAR(30) NOT NULL COMMENT '考试名称',
    `duration_min` INT                  DEFAULT NULL COMMENT '考试时长',
    `start_time`   DATETIME             DEFAULT NULL COMMENT '开始时间',
    `is_published` BIT(1)               DEFAULT NULL COMMENT '开放标记',
    `created_at`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='考试信息表';

DROP TABLE IF EXISTS `edu_paper`;
CREATE TABLE `edu_paper`
(
    `id`              BIGINT   NOT NULL AUTO_INCREMENT COMMENT '试卷主键ID',
    `paper_code`      INT      NOT NULL COMMENT '试卷序号',
    `paper_name`      VARCHAR(255)      DEFAULT NULL COMMENT '试卷名称',
    `description`     VARCHAR(255)      DEFAULT NULL COMMENT '试卷描述',
    `generation_time` DATETIME          DEFAULT NULL COMMENT '试卷生成时间',
    `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='试卷表';

DROP TABLE IF EXISTS `rel_paper_question`;
CREATE TABLE `rel_paper_question`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '关联表主键',
    `paper_id`    BIGINT            DEFAULT NULL COMMENT '试卷ID',
    `question_id` BIGINT            DEFAULT NULL COMMENT '题目ID',
    `score`       INT               DEFAULT NULL COMMENT '该题分值',
    `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='试卷题目关联表';

-- ==========================================
-- 5. 提交记录与成绩 (Result Module)
-- ==========================================

DROP TABLE IF EXISTS `res_exercise_record`;
CREATE TABLE `res_exercise_record`
(
    `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '答题记录主键ID',
    `item_id`       BIGINT            DEFAULT NULL COMMENT '练习题目ID',
    `student_id`    BIGINT            DEFAULT NULL COMMENT '学生ID',
    `choice_answer` VARCHAR(30)       DEFAULT NULL COMMENT '选择/判断题答案',
    `text_content`  TEXT COMMENT '编程/简答作答内容',
    `score`         INT               DEFAULT NULL COMMENT '得分',
    `submitted_at`  DATETIME          DEFAULT NULL COMMENT '提交时间',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_res_exe_item` (`item_id`),
    KEY `fk_res_exe_student` (`student_id`),
    CONSTRAINT `fk_res_exe_item` FOREIGN KEY (`item_id`) REFERENCES `edu_exercise_item` (`id`),
    CONSTRAINT `fk_res_exe_student` FOREIGN KEY (`student_id`) REFERENCES `auth_student` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学生练习答题记录表';

DROP TABLE IF EXISTS `res_fill_blank_detail`;
CREATE TABLE `res_fill_blank_detail`
(
    `id`             BIGINT   NOT NULL AUTO_INCREMENT COMMENT '答案记录主键ID',
    `item_id`        BIGINT                           DEFAULT NULL COMMENT '实验子项目ID',
    `blank_index`    INT                              DEFAULT NULL COMMENT '填空号',
    `answer_content` VARCHAR(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '填写的答案内容',
    `answer_hash`    CHAR(32)                         DEFAULT NULL COMMENT '答案MD5',
    `submit_count`   INT                              DEFAULT NULL COMMENT '提交次数',
    `is_correct`     BIT(1)                           DEFAULT NULL COMMENT '是否正确',
    `created_at`     DATETIME NOT NULL                DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME NOT NULL                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_res_fill_blank` (`item_id`, `blank_index`, `answer_hash`),
    CONSTRAINT `fk_res_fill_blank_item` FOREIGN KEY (`item_id`) REFERENCES `edu_experiment_item` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学生填空答案表';

DROP TABLE IF EXISTS `res_experiment_result`;
CREATE TABLE `res_experiment_result`
(
    `id`             BIGINT   NOT NULL AUTO_INCREMENT COMMENT '提交记录主键ID',
    `student_id`     BIGINT   NOT NULL COMMENT '学生ID',
    `item_id`        BIGINT   NOT NULL COMMENT '实验子项目ID',
    `sub_content`    TEXT     NOT NULL COMMENT '提交的实验内容',
    `score`          TINYINT           DEFAULT NULL COMMENT '评分',
    `submitted_at`   DATETIME NOT NULL COMMENT '提交时间',
    `grading_status` INT               DEFAULT NULL COMMENT '评分标记',
    `created_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_res_exp_result` (`student_id`, `item_id`),
    KEY `fk_res_exp_result_item` (`item_id`),
    CONSTRAINT `fk_res_exp_result_item` FOREIGN KEY (`item_id`) REFERENCES `edu_experiment_item` (`id`),
    CONSTRAINT `fk_res_exp_result_student` FOREIGN KEY (`student_id`) REFERENCES `auth_student` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学生实验提交记录表';

DROP TABLE IF EXISTS `res_submission_log`;
CREATE TABLE `res_submission_log`
(
    `id`               BIGINT   NOT NULL AUTO_INCREMENT COMMENT '日志主键ID',
    `result_id`        BIGINT            DEFAULT NULL COMMENT '提交记录ID',
    `content_snapshot` TEXT COMMENT '快照内容',
    `snapshot_time`    DATETIME          DEFAULT NULL COMMENT '快照时间',
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_res_sub_log` (`result_id`, `snapshot_time`),
    CONSTRAINT `fk_res_sub_log_result` FOREIGN KEY (`result_id`) REFERENCES `res_experiment_result` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学生实验提交日志表';

DROP TABLE IF EXISTS `res_exam_record`;
CREATE TABLE `res_exam_record`
(
    `id`             BIGINT   NOT NULL AUTO_INCREMENT COMMENT '考试答题主键ID',
    `student_id`     BIGINT            DEFAULT NULL COMMENT '学生ID',
    `question_id`    BIGINT            DEFAULT NULL COMMENT '题目ID',
    `student_answer` VARCHAR(255)      DEFAULT NULL COMMENT '填写的答案',
    `created_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_res_exam_record` (`student_id`, `question_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学生考试答题表';

DROP TABLE IF EXISTS `res_score_summary`;
CREATE TABLE `res_score_summary`
(
    `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '成绩汇总主键ID',
    `student_id`    BIGINT            DEFAULT NULL COMMENT '学生ID',
    `experiment_id` BIGINT            DEFAULT NULL COMMENT '实验ID',
    `total_score`   INT               DEFAULT NULL COMMENT '总成绩',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_res_score_student` (`student_id`),
    KEY `fk_res_score_exp` (`experiment_id`),
    CONSTRAINT `fk_res_score_student` FOREIGN KEY (`student_id`) REFERENCES `auth_student` (`id`),
    CONSTRAINT `fk_res_score_exp` FOREIGN KEY (`experiment_id`) REFERENCES `edu_experiment` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='实验成绩汇总缓存表';

-- ==========================================
-- 6. 系统配置与日志 (System Module)
-- ==========================================

DROP TABLE IF EXISTS `edu_lecture`;
CREATE TABLE `edu_lecture`
(
    `id`             BIGINT   NOT NULL AUTO_INCREMENT COMMENT '课件主键ID',
    `lecture_name`   VARCHAR(50)       DEFAULT NULL COMMENT '课件名称',
    `category_id`    INT               DEFAULT NULL COMMENT '课件分类',
    `file_extension` VARCHAR(10)       DEFAULT NULL COMMENT '文件格式',
    `created_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='教学课件资料表';

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `config_key`   VARCHAR(20) NOT NULL COMMENT '配置键名',
    `config_value` VARCHAR(100)         DEFAULT NULL COMMENT '配置值',
    `created_at`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`config_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统配置参数表';

DROP TABLE IF EXISTS `sys_student_log`;
CREATE TABLE `sys_student_log`
(
    `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '日志主键ID',
    `account`       VARCHAR(20)       DEFAULT NULL COMMENT '操作账号',
    `action_type`   INT               DEFAULT NULL COMMENT '动作类型',
    `action_detail` VARCHAR(255)      DEFAULT NULL COMMENT '详情',
    `op_time`       DATETIME          DEFAULT NULL COMMENT '操作时间',
    `client_ip`     VARCHAR(20)       DEFAULT NULL COMMENT '客户端IP',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_sys_stu_log_time` (`op_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学生操作日志表';

DROP TABLE IF EXISTS `sys_admin_log`;
CREATE TABLE `sys_admin_log`
(
    `id`            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '日志主键ID',
    `account`       VARCHAR(20)       DEFAULT NULL COMMENT '管理账号',
    `action_type`   INT               DEFAULT NULL COMMENT '动作类型',
    `action_detail` VARCHAR(255)      DEFAULT NULL COMMENT '详情',
    `op_time`       DATETIME          DEFAULT NULL COMMENT '操作时间',
    `client_ip`     VARCHAR(100)      DEFAULT NULL COMMENT 'IP地址',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_sys_admin_log_time` (`op_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统后台日志表';

SET FOREIGN_KEY_CHECKS = 1; -- 恢复外键检查