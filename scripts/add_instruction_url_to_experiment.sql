-- 为 t_experiment 表新增 instruction_url 字段
-- 用于存储实验指导书文件的访问路径

ALTER TABLE t_experiment
    ADD COLUMN IF NOT EXISTS instruction_url VARCHAR(500) NULL COMMENT '指导书文件访问路径';
