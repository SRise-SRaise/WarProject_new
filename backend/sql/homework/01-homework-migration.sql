-- Homework Migration 01
-- 目的：整合作业模块历史迁移脚本，统一完成以下演进：
-- 1) 为作业题目补充 analysis / difficulty 字段（若缺失）
-- 2) 为答题记录补充 grading_status / comment 字段与索引（若缺失）
-- 3) 引入 rel_exercise_item 关系表并回填历史数据
-- 4) 删除 edu_exercise_item.exercise_id（关系改由 rel_exercise_item 维护）

USE edu_hub;

START TRANSACTION;

-- =====================================================
-- A. 列补丁（幂等）
-- =====================================================

-- A1) edu_exercise_item.analysis
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.columns
WHERE table_schema = 'edu_hub'
  AND table_name = 'edu_exercise_item'
  AND column_name = 'analysis';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE edu_exercise_item ADD COLUMN analysis TEXT COMMENT ''题目解析说明'' AFTER question_bank_id',
    'SELECT ''skip add edu_exercise_item.analysis'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- A2) edu_exercise_item.difficulty
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.columns
WHERE table_schema = 'edu_hub'
  AND table_name = 'edu_exercise_item'
  AND column_name = 'difficulty';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE edu_exercise_item ADD COLUMN difficulty TINYINT DEFAULT NULL COMMENT ''难度系数：1-5'' AFTER analysis',
    'SELECT ''skip add edu_exercise_item.difficulty'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- A3) res_exercise_record.grading_status
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.columns
WHERE table_schema = 'edu_hub'
  AND table_name = 'res_exercise_record'
  AND column_name = 'grading_status';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE res_exercise_record ADD COLUMN grading_status INT DEFAULT 0 COMMENT ''批改状态：0未批改,1自动判分,2教师已批改'' AFTER score',
    'SELECT ''skip add res_exercise_record.grading_status'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- A4) res_exercise_record.comment
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.columns
WHERE table_schema = 'edu_hub'
  AND table_name = 'res_exercise_record'
  AND column_name = 'comment';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE res_exercise_record ADD COLUMN comment VARCHAR(500) DEFAULT NULL COMMENT ''教师评语'' AFTER grading_status',
    'SELECT ''skip add res_exercise_record.comment'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- A5) res_exercise_record.idx_grading_status
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists
FROM information_schema.statistics
WHERE table_schema = 'edu_hub'
  AND table_name = 'res_exercise_record'
  AND index_name = 'idx_grading_status';

SET @sql = IF(@idx_exists = 0,
    'CREATE INDEX idx_grading_status ON res_exercise_record (grading_status)',
    'SELECT ''skip create idx_grading_status'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- B. 作业-题目关系拆分
-- =====================================================

CREATE TABLE IF NOT EXISTS `rel_exercise_item`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '关联主键ID',
    `exercise_id` BIGINT      NOT NULL COMMENT '作业ID',
    `item_id`     BIGINT      NOT NULL COMMENT '题目ID（edu_exercise_item.id）',
    `item_order`  INT                  DEFAULT NULL COMMENT '题目顺序',
    `item_score`  TINYINT              DEFAULT NULL COMMENT '作业内该题分值（为空时使用题目默认分值）',
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_exercise_item` (`exercise_id`, `item_id`),
    KEY `idx_item_id` (`item_id`),
    CONSTRAINT `fk_rel_exe_item_exercise` FOREIGN KEY (`exercise_id`) REFERENCES `edu_exercise` (`id`),
    CONSTRAINT `fk_rel_exe_item_item` FOREIGN KEY (`item_id`) REFERENCES `edu_exercise_item` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='作业-题目关联表';

-- B1) 回填（仅当旧列 exercise_id 存在时执行）
SET @has_exercise_id = 0;
SELECT COUNT(*) INTO @has_exercise_id
FROM information_schema.columns
WHERE table_schema = 'edu_hub'
  AND table_name = 'edu_exercise_item'
  AND column_name = 'exercise_id';

SET @sql = IF(@has_exercise_id = 1,
    'INSERT INTO rel_exercise_item (exercise_id, item_id, item_order, item_score) '
    'SELECT t.exercise_id, t.id, t.item_order, t.max_score '
    'FROM ( '
    '   SELECT e.exercise_id, e.id, e.max_score, '
    '          IF(@prev_exercise = e.exercise_id, @row_no := @row_no + 1, @row_no := 1) AS item_order, '
    '          @prev_exercise := e.exercise_id AS _prev_exercise '
    '   FROM edu_exercise_item e, (SELECT @row_no := 0, @prev_exercise := NULL) vars '
    '   WHERE e.exercise_id IS NOT NULL '
    '   ORDER BY e.exercise_id, e.id '
    ') t '
    'ON DUPLICATE KEY UPDATE item_score = VALUES(item_score), item_order = VALUES(item_order)',
    'SELECT ''skip backfill rel_exercise_item (exercise_id already removed)'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- B2) 删除旧外键（存在才删）
SET @fk_exists = 0;
SELECT COUNT(*) INTO @fk_exists
FROM information_schema.table_constraints
WHERE constraint_schema = 'edu_hub'
  AND table_name = 'edu_exercise_item'
  AND constraint_name = 'fk_exe_item_exe'
  AND constraint_type = 'FOREIGN KEY';

SET @sql = IF(@fk_exists = 1,
    'ALTER TABLE edu_exercise_item DROP FOREIGN KEY fk_exe_item_exe',
    'SELECT ''skip drop fk_exe_item_exe'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- B3) 删除旧索引（存在才删）
SET @idx_exists = 0;
SELECT COUNT(*) INTO @idx_exists
FROM information_schema.statistics
WHERE table_schema = 'edu_hub'
  AND table_name = 'edu_exercise_item'
  AND index_name = 'fk_exe_item_exe';

SET @sql = IF(@idx_exists > 0,
    'ALTER TABLE edu_exercise_item DROP INDEX fk_exe_item_exe',
    'SELECT ''skip drop index fk_exe_item_exe'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- B4) 删除旧列（存在才删）
SET @has_exercise_id = 0;
SELECT COUNT(*) INTO @has_exercise_id
FROM information_schema.columns
WHERE table_schema = 'edu_hub'
  AND table_name = 'edu_exercise_item'
  AND column_name = 'exercise_id';

SET @sql = IF(@has_exercise_id = 1,
    'ALTER TABLE edu_exercise_item DROP COLUMN exercise_id',
    'SELECT ''skip drop column exercise_id'' AS msg');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

COMMIT;
