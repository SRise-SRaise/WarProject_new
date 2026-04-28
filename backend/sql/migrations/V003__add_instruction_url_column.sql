-- 为 t_experiment 表添加指导书/附件 URL 列
-- 此列用于存储实验指导书或附件文件的访问路径

ALTER TABLE `t_experiment`
ADD COLUMN `instruction_url` VARCHAR(500) DEFAULT NULL COMMENT '指导书/附件文件访问路径' AFTER `state`;
