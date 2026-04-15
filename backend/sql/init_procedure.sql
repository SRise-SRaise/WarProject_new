USE edu_hub;

DELIMITER ;;

-- ---------------------------------------------------------
-- 1. 核心答题提交流程 (适配新表 res_exercise_record)
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS `answerQuestion`;;
CREATE PROCEDURE `answerQuestion`(
    IN p_studentId      BIGINT,
    IN p_exerciseId     BIGINT,
    IN p_itemId         BIGINT,
    IN p_type           INT,
    IN p_studentAnswer  VARCHAR(500)
)
BEGIN
    DECLARE n INT;
    -- 检查是否已经答过该题（唯一索引：student_id + item_id）
    SELECT COUNT(*) INTO n
    FROM `res_exercise_record`
    WHERE `student_id` = p_studentId
      AND `item_id` = p_itemId;

    IF n = 0 THEN
        -- 首次作答
        IF p_type = 1 OR p_type = 2 THEN
            -- 选择/判断等客观题
            INSERT INTO `res_exercise_record`(
                `exercise_id`, `student_id`, `item_id`,
                `choice_answer`, `submitted_at`
            )
            VALUES (p_exerciseId, p_studentId, p_itemId, p_studentAnswer, NOW());
        ELSE
            -- 编程/简答等主观题
            INSERT INTO `res_exercise_record`(
                `exercise_id`, `student_id`, `item_id`,
                `text_content`, `submitted_at`
            )
            VALUES (p_exerciseId, p_studentId, p_itemId, p_studentAnswer, NOW());
        END IF;
    ELSE
        -- 已经作答过，走更新逻辑（exercise_id 基本不会变，这里无需更新）
        IF p_type = 1 OR p_type = 2 THEN
            UPDATE `res_exercise_record`
            SET `choice_answer` = p_studentAnswer,
                `submitted_at` = NOW()
            WHERE `student_id` = p_studentId
              AND `item_id` = p_itemId;
        ELSE
            UPDATE `res_exercise_record`
            SET `text_content` = p_studentAnswer,
                `submitted_at` = NOW()
            WHERE `student_id` = p_studentId
              AND `item_id` = p_itemId;
        END IF;
    END IF;
END ;;

-- ---------------------------------------------------------
-- 2. 查询班级学生某实验的所有答题记录
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS `p_clazz_experiment_answers`;;
CREATE PROCEDURE `p_clazz_experiment_answers`(IN `p_expid` BIGINT, IN `p_cno` VARCHAR(6))
BEGIN
    SELECT r.`student_item_id`, r.`item_id`, i.`experiment_item_score`, r.`content`
    FROM `t_student_item` AS r
             JOIN `t_experiment_item` AS i ON r.`item_id` = i.`experiment_item_id`
             JOIN `auth_student` AS s ON r.`student_id` = s.`id`
    WHERE i.`experiment_id` = p_expid
      AND s.`class_code` = p_cno;
END ;;

-- ---------------------------------------------------------
-- 3. 查询班级学生某实验的总分
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS `p_clazz_experiment_score`;;
CREATE PROCEDURE `p_clazz_experiment_score`(IN `p_cno` VARCHAR(6), IN `p_expid` BIGINT)
BEGIN
    SELECT s.`id`                            AS student_id,
           s.`student_code`                  AS student_no,
           s.`student_name`,
           s.`class_code`                    AS clazz_no,
           s.`remark`                        AS memo,
           ( SELECT SUM(r.`score`)
             FROM `t_student_item` r
                      JOIN `t_experiment_item` i ON r.`item_id` = i.`experiment_item_id`
             WHERE i.`experiment_id` = p_expid
               AND r.`student_id` = s.`id` ) AS score
    FROM `auth_student` s
    WHERE s.`class_code` = p_cno
    ORDER BY s.`student_code`;
END ;;

-- ---------------------------------------------------------
-- 4. 查询某学生某实验各子项目的得分详情
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS `p_student_experiment_item_score`;;
CREATE PROCEDURE `p_student_experiment_item_score`(IN `p_sid` BIGINT, IN `p_expid` BIGINT)
BEGIN
    SELECT i.`experiment_item_id`      AS experiment_item_id,
           i.`experiment_item_no`      AS experiment_item_no,
           i.`experiment_item_name`    AS experiment_item_name,
           i.`experiment_item_type`    AS experiment_item_type,
           i.`experiment_item_content` AS experiment_item_content,
           i.`experiment_item_score`   AS experiment_item_score,
           ( SELECT r.`student_item_id`
             FROM `t_student_item` r
             WHERE r.`item_id` = i.`experiment_item_id`
               AND r.`student_id` = p_sid ) AS student_item_id,
           ( SELECT r.`content`
             FROM `t_student_item` r
             WHERE r.`item_id` = i.`experiment_item_id`
               AND r.`student_id` = p_sid ) AS student_answer,
           ( SELECT r.`score`
             FROM `t_student_item` r
             WHERE r.`item_id` = i.`experiment_item_id`
               AND r.`student_id` = p_sid ) AS score,
           i.`state` AS state
    FROM `t_experiment_item` i
    WHERE i.`experiment_id` = p_expid
    ORDER BY i.`experiment_item_no`;
END ;;

-- ---------------------------------------------------------
-- 5. 查询某学生所有实验的总分
-- ---------------------------------------------------------
DROP PROCEDURE IF EXISTS `p_student_experiment_score`;;
CREATE PROCEDURE `p_student_experiment_score`(IN `p_sid` BIGINT)
BEGIN
    SELECT e.`experiment_id`      AS experiment_id,
           e.`experiment_no`      AS experiment_no,
           e.`experiment_name`    AS experiment_name,
           e.`experiment_type`    AS experiment_type,
           e.`instruction_type`   AS instruction_type,
           ( SELECT SUM(r.`score`)
             FROM `t_student_item` r
                      JOIN `t_experiment_item` i ON r.`item_id` = i.`experiment_item_id`
             WHERE i.`experiment_id` = e.`experiment_id`
               AND r.`student_id` = p_sid ) AS score,
           e.`state` AS state
    FROM `t_experiment` e
    ORDER BY e.`experiment_no`;
END ;;

DELIMITER ;
