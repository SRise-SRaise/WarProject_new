USE edu_hub;

DROP TRIGGER IF EXISTS `trg_res_exam_record_auto_grade_before_insert`;

DELIMITER ;;

CREATE TRIGGER `trg_res_exam_record_auto_grade_before_insert`
BEFORE INSERT ON `res_exam_record`
FOR EACH ROW
BEGIN
    DECLARE v_question_type INT DEFAULT NULL;
    DECLARE v_standard_answer VARCHAR(255) DEFAULT NULL;
    DECLARE v_full_score INT DEFAULT NULL;
    DECLARE v_effective_paper_id BIGINT DEFAULT NULL;
    DECLARE v_match_count INT DEFAULT 0;
    DECLARE v_student_answer_normalized VARCHAR(255) DEFAULT NULL;
    DECLARE v_standard_answer_normalized VARCHAR(255) DEFAULT NULL;

    SET v_effective_paper_id = NEW.paper_id;

    IF v_effective_paper_id IS NULL AND NEW.exam_id IS NOT NULL THEN
        SELECT e.paper_id
        INTO v_effective_paper_id
        FROM edu_exam e
        WHERE e.id = NEW.exam_id
        LIMIT 1;
    END IF;

    IF NEW.paper_question_id IS NOT NULL THEN
        SELECT qb.question_type,
               qb.standard_answer,
               rpq.score
        INTO v_question_type,
             v_standard_answer,
             v_full_score
        FROM rel_paper_question rpq
        JOIN edu_question_bank qb ON qb.id = rpq.question_id
        WHERE rpq.id = NEW.paper_question_id
        LIMIT 1;
    ELSEIF v_effective_paper_id IS NOT NULL AND NEW.question_id IS NOT NULL THEN
        SELECT COUNT(*)
        INTO v_match_count
        FROM rel_paper_question rpq
        WHERE rpq.paper_id = v_effective_paper_id
          AND rpq.question_id = NEW.question_id;

        IF v_match_count = 1 THEN
            SELECT qb.question_type,
                   qb.standard_answer,
                   rpq.score
            INTO v_question_type,
                 v_standard_answer,
                 v_full_score
            FROM rel_paper_question rpq
            JOIN edu_question_bank qb ON qb.id = rpq.question_id
            WHERE rpq.paper_id = v_effective_paper_id
              AND rpq.question_id = NEW.question_id
            LIMIT 1;
        END IF;
    ELSEIF NEW.question_id IS NOT NULL THEN
        SELECT qb.question_type,
               qb.standard_answer
        INTO v_question_type,
             v_standard_answer
        FROM edu_question_bank qb
        WHERE qb.id = NEW.question_id
        LIMIT 1;

        SELECT COUNT(*)
        INTO v_match_count
        FROM rel_paper_question rpq
        WHERE rpq.question_id = NEW.question_id;

        IF v_match_count = 1 THEN
            SELECT rpq.score
            INTO v_full_score
            FROM rel_paper_question rpq
            WHERE rpq.question_id = NEW.question_id
            LIMIT 1;
        END IF;
    END IF;

    IF v_question_type IN (1, 2, 3, 4) AND v_standard_answer IS NOT NULL AND v_full_score IS NOT NULL THEN
        SET v_student_answer_normalized = UPPER(TRIM(COALESCE(NEW.student_answer, '')));
        SET v_standard_answer_normalized = UPPER(TRIM(COALESCE(v_standard_answer, '')));

        SET v_student_answer_normalized = REPLACE(v_student_answer_normalized, '，', ',');
        SET v_standard_answer_normalized = REPLACE(v_standard_answer_normalized, '，', ',');
        SET v_student_answer_normalized = REPLACE(v_student_answer_normalized, ' ', '');
        SET v_standard_answer_normalized = REPLACE(v_standard_answer_normalized, ' ', '');

        IF v_student_answer_normalized = v_standard_answer_normalized THEN
            SET NEW.score = v_full_score;
        ELSE
            SET NEW.score = 0;
        END IF;

        SET NEW.grading_status = 1;
    ELSEIF v_question_type IN (5, 6, 7) THEN
        IF NEW.grading_status IS NULL THEN
            SET NEW.grading_status = 0;
        END IF;
    END IF;
END;;

DELIMITER ;
