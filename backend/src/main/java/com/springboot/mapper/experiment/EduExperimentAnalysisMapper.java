package com.springboot.mapper.experiment;

import com.springboot.model.dto.experiment.ExperimentScoreDistributionDTO;
import com.springboot.model.dto.experiment.ExperimentTypeCountDTO;
import com.springboot.model.dto.experiment.StepScoreAnalysisDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 教师端实验数据分析 Mapper
 */
@Mapper
public interface EduExperimentAnalysisMapper {

    // ==================== 单实验统计 ====================

    @Select("<script>" +
            "SELECT COUNT(DISTINCT r.student_id) " +
            "FROM t_student_item r " +
            "JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "WHERE i.experiment_id = #{experimentId} " +
            "  <if test='clazzNo != null and clazzNo.length() > 0'>" +
            "    AND r.student_id IN (SELECT student_id FROM t_student WHERE class_no = #{clazzNo})" +
            "  </if>" +
            "</script>")
    Integer countTotalStudents(@Param("experimentId") Long experimentId, @Param("clazzNo") String clazzNo);

    @Select("<script>" +
            "SELECT COUNT(DISTINCT r.student_id) " +
            "FROM t_student_item r " +
            "JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "WHERE i.experiment_id = #{experimentId} AND r.score_flag IN (1, 2) " +
            "  <if test='clazzNo != null and clazzNo.length() > 0'>" +
            "    AND r.student_id IN (SELECT student_id FROM t_student WHERE class_no = #{clazzNo})" +
            "  </if>" +
            "</script>")
    Integer countSubmittedStudents(@Param("experimentId") Long experimentId, @Param("clazzNo") String clazzNo);

    @Select("<script>" +
            "SELECT COUNT(DISTINCT r.student_id) " +
            "FROM t_student_item r " +
            "JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "WHERE i.experiment_id = #{experimentId} AND r.score_flag = 2 AND r.score IS NOT NULL " +
            "  <if test='clazzNo != null and clazzNo.length() > 0'>" +
            "    AND r.student_id IN (SELECT student_id FROM t_student WHERE class_no = #{clazzNo})" +
            "  </if>" +
            "</script>")
    Integer countGradedStudents(@Param("experimentId") Long experimentId, @Param("clazzNo") String clazzNo);

    @Select("<script>" +
            "SELECT IFNULL(AVG(s.exp_score), 0) FROM (" +
            "  SELECT r.student_id, SUM(r.score) AS exp_score " +
            "  FROM t_student_item r " +
            "  JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "  WHERE i.experiment_id = #{experimentId} AND r.score_flag = 2 AND r.score IS NOT NULL " +
            "  <if test='clazzNo != null and clazzNo.length() > 0'>" +
            "    AND r.student_id IN (SELECT student_id FROM t_student WHERE class_no = #{clazzNo})" +
            "  </if>" +
            "  GROUP BY r.student_id" +
            ") AS s" +
            "</script>")
    Double avgScore(@Param("experimentId") Long experimentId, @Param("clazzNo") String clazzNo);

    @Select("<script>" +
            "SELECT IFNULL(MAX(s.exp_score), 0) FROM (" +
            "  SELECT SUM(r.score) AS exp_score " +
            "  FROM t_student_item r " +
            "  JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "  WHERE i.experiment_id = #{experimentId} AND r.score_flag = 2 AND r.score IS NOT NULL " +
            "  <if test='clazzNo != null and clazzNo.length() > 0'>" +
            "    AND r.student_id IN (SELECT student_id FROM t_student WHERE class_no = #{clazzNo})" +
            "  </if>" +
            "  GROUP BY r.student_id" +
            ") AS s" +
            "</script>")
    Integer maxScore(@Param("experimentId") Long experimentId, @Param("clazzNo") String clazzNo);

    @Select("<script>" +
            "SELECT IFNULL(MIN(s.exp_score), 0) FROM (" +
            "  SELECT SUM(r.score) AS exp_score " +
            "  FROM t_student_item r " +
            "  JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "  WHERE i.experiment_id = #{experimentId} AND r.score_flag = 2 AND r.score IS NOT NULL " +
            "  <if test='clazzNo != null and clazzNo.length() > 0'>" +
            "    AND r.student_id IN (SELECT student_id FROM t_student WHERE class_no = #{clazzNo})" +
            "  </if>" +
            "  GROUP BY r.student_id" +
            ") AS s" +
            "</script>")
    Integer minScore(@Param("experimentId") Long experimentId, @Param("clazzNo") String clazzNo);

    @Select("<script>" +
            "SELECT SUM(r.score) AS score " +
            "FROM t_student_item r " +
            "JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "WHERE i.experiment_id = #{experimentId} AND r.score_flag = 2 AND r.score IS NOT NULL " +
            "  <if test='clazzNo != null and clazzNo.length() > 0'>" +
            "    AND r.student_id IN (SELECT student_id FROM t_student WHERE class_no = #{clazzNo})" +
            "  </if>" +
            "GROUP BY r.student_id" +
            "</script>")
    List<ExperimentScoreDistributionDTO> selectStudentScores(
            @Param("experimentId") Long experimentId,
            @Param("clazzNo") String clazzNo);

    // ==================== 步骤得分分析 ====================

    /**
     * 查询某个实验每个步骤（题目）的得分统计
     * 返回：步骤序号、题目名称、题型、满分、平均分、最高分、最低分、作答人数
     *
     * 注意：使用独立顶层 DTO 类 + @Results 显式映射，避免 MyBatis 对静态内部类映射失败的问题
     */
    @Results(id = "stepScoreResultMap", value = {
            @Result(property = "sortOrder",    column = "sort_order"),
            @Result(property = "itemName",     column = "item_name"),
            @Result(property = "questionType", column = "question_type"),
            @Result(property = "maxScore",     column = "max_score"),
            @Result(property = "avgScore",     column = "avg_score"),
            @Result(property = "highScore",    column = "high_score"),
            @Result(property = "lowScore",     column = "low_score"),
            @Result(property = "answeredCount",column = "answered_count")
    })
    @Select("<script>" +
            "SELECT " +
            "  i.sort_order                    AS sort_order, " +
            "  i.item_name                     AS item_name, " +
            "  i.question_type                 AS question_type, " +
            "  i.max_score                     AS max_score, " +
            "  IFNULL(AVG(r.score), 0)         AS avg_score, " +
            "  IFNULL(MAX(r.score), 0)         AS high_score, " +
            "  IFNULL(MIN(r.score), 0)         AS low_score, " +
            "  COUNT(r.student_item_id)        AS answered_count " +
            "FROM t_experiment_item i " +
            "LEFT JOIN t_student_item r " +
            "  ON r.item_id = i.experiment_item_id " +
            "  AND r.score IS NOT NULL " +
            "  AND r.score_flag = 2 " +
            "  <if test='clazzNo != null and clazzNo.length() > 0'>" +
            "  AND r.student_id IN (SELECT student_id FROM t_student WHERE class_no = #{clazzNo}) " +
            "  </if>" +
            "WHERE i.experiment_id = #{experimentId} " +
            "  AND i.item_status = 1 " +
            "GROUP BY i.experiment_item_id, i.sort_order, i.item_name, i.question_type, i.max_score " +
            "ORDER BY i.sort_order ASC" +
            "</script>")
    List<StepScoreAnalysisDTO> selectStepScoreAnalysis(
            @Param("experimentId") Long experimentId,
            @Param("clazzNo") String clazzNo);

    // ==================== 全局统计 ====================

    @Select("SELECT COUNT(*) FROM t_experiment WHERE state = 1")
    Integer countPublishedExperiments();

    @Select("SELECT COUNT(DISTINCT i.experiment_id) " +
            "FROM t_student_item r " +
            "JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "JOIN t_experiment e ON i.experiment_id = e.experiment_id " +
            "WHERE e.state = 1 AND r.score_flag IN (1, 2)")
    Integer countActiveExperiments();

    @Select("SELECT COUNT(DISTINCT i.experiment_id) " +
            "FROM t_student_item r " +
            "JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "JOIN t_experiment e ON i.experiment_id = e.experiment_id " +
            "WHERE e.state = 1 AND r.score_flag = 2 AND r.score IS NOT NULL")
    Integer countGloballyGradedExperiments();

    /**
     * 查询各类型实验数量
     */
    @Select("SELECT experiment_type AS typeCode, COUNT(*) AS experimentCount " +
            "FROM t_experiment WHERE state = 1 GROUP BY experiment_type")
    List<ExperimentTypeCountDTO> selectExperimentTypeCounts();

    /**
     * 查询各类型实验已提交实验数（有 score_flag IN (1,2) 的实验）
     */
    @Select("SELECT e.experiment_type AS typeCode, COUNT(DISTINCT e.experiment_id) AS submittedCount " +
            "FROM t_experiment e " +
            "JOIN t_experiment_item i ON i.experiment_id = e.experiment_id " +
            "JOIN t_student_item r ON r.item_id = i.experiment_item_id " +
            "WHERE e.state = 1 AND r.score_flag IN (1, 2) " +
            "GROUP BY e.experiment_type")
    List<ExperimentTypeCountDTO> selectExperimentTypeSubmittedCounts();

    /**
     * 查询各类型实验已批改实验数（score_flag = 2）
     */
    @Select("SELECT e.experiment_type AS typeCode, COUNT(DISTINCT e.experiment_id) AS gradedCount " +
            "FROM t_experiment e " +
            "JOIN t_experiment_item i ON i.experiment_id = e.experiment_id " +
            "JOIN t_student_item r ON r.item_id = i.experiment_item_id " +
            "WHERE e.state = 1 AND r.score_flag = 2 AND r.score IS NOT NULL " +
            "GROUP BY e.experiment_type")
    List<ExperimentTypeCountDTO> selectExperimentTypeGradedCounts();

    /**
     * 查询各类型实验已批改学生的总提交次数（去重学生数 * 提交次数）
     */
    @Select("SELECT e.experiment_type AS typeCode, COUNT(DISTINCT r.student_id) AS totalSubmissions " +
            "FROM t_experiment e " +
            "JOIN t_experiment_item i ON i.experiment_id = e.experiment_id " +
            "JOIN t_student_item r ON r.item_id = i.experiment_item_id " +
            "WHERE e.state = 1 AND r.score_flag IN (1, 2) " +
            "GROUP BY e.experiment_type")
    List<ExperimentTypeCountDTO> selectExperimentTypeTotalSubmissions();

    /**
     * 查询各类型实验的平均分
     */
    @Select("SELECT e.experiment_type AS typeCode, IFNULL(AVG(student_totals.exp_score), 0) AS averageScore " +
            "FROM t_experiment e " +
            "JOIN t_experiment_item i ON i.experiment_id = e.experiment_id " +
            "JOIN (" +
            "  SELECT item_id, student_id, SUM(score) AS exp_score " +
            "  FROM t_student_item " +
            "  WHERE score_flag = 2 AND score IS NOT NULL " +
            "  GROUP BY item_id, student_id" +
            ") r ON r.item_id = i.experiment_item_id " +
            "JOIN (" +
            "  SELECT experiment_id, student_id, SUM(exp_score) AS exp_score " +
            "  FROM t_experiment_item i2 " +
            "  JOIN (" +
            "    SELECT item_id, student_id, SUM(score) AS exp_score " +
            "    FROM t_student_item " +
            "    WHERE score_flag = 2 AND score IS NOT NULL " +
            "    GROUP BY item_id, student_id" +
            "  ) s ON s.item_id = i2.experiment_item_id " +
            "  GROUP BY experiment_id, student_id" +
            ") student_totals ON student_totals.experiment_id = e.experiment_id " +
            "WHERE e.state = 1 " +
            "GROUP BY e.experiment_type")
    List<ExperimentTypeCountDTO> selectExperimentTypeAverageScores();
}
