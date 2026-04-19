-- 作业模块数据库补充脚本
-- 为 res_exercise_record 表添加批改状态和评语字段

USE edu_hub;

-- 添加批改状态字段
ALTER TABLE `res_exercise_record`
ADD COLUMN `grading_status` INT DEFAULT 0 COMMENT '批改状态：0未批改,1自动判分,2教师已批改' AFTER `score`;

-- 添加教师评语字段
ALTER TABLE `res_exercise_record`
ADD COLUMN `comment` VARCHAR(500) DEFAULT NULL COMMENT '教师评语' AFTER `grading_status`;

-- 创建索引（可选，用于快速查询未批改记录）
CREATE INDEX `idx_grading_status` ON `res_exercise_record` (`grading_status`);

-- 为 edu_exercise_item 表补充题目解析与难度字段
ALTER TABLE `edu_exercise_item`
ADD COLUMN `analysis` TEXT COMMENT '题目解析说明' AFTER `question_bank_id`;

ALTER TABLE `edu_exercise_item`
ADD COLUMN `difficulty` TINYINT DEFAULT NULL COMMENT '难度系数：1-5' AFTER `analysis`;
