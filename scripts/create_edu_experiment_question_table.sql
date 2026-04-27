-- 创建实验题库表 edu_experiment_question
-- 用于存储实验中的各类题目（选择题、填空题、编程题、简答题）

CREATE TABLE IF NOT EXISTS `edu_experiment_question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目主键ID',
  `question_name` VARCHAR(255) DEFAULT NULL COMMENT '题目名称/标题',
  `question_content` TEXT NOT NULL COMMENT '题目内容/题干',
  `question_type` INT NOT NULL DEFAULT 1 COMMENT '题目类型：1-选择题，2-填空题，3-编程题，4-简答题',
  `difficulty` INT DEFAULT 2 COMMENT '难度等级：1-简单，2-中等，3-困难',
  `tag` VARCHAR(255) DEFAULT NULL COMMENT '所属知识点/标签',
  `options` TEXT DEFAULT NULL COMMENT '选项内容（JSON格式，适用选择题）',
  `standard_answer` TEXT DEFAULT NULL COMMENT '标准答案',
  `answer_analysis` TEXT DEFAULT NULL COMMENT '答案解析',
  `reference_code` TEXT DEFAULT NULL COMMENT '参考代码（适用编程题）',
  `score` INT DEFAULT 10 COMMENT '题目分值',
  `status` INT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_question_type` (`question_type`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实验题库表';
