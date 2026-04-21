package com.springboot.mapper.experiment;

import com.springboot.model.dto.experiment.ExperimentScoreDistributionDTO;
import com.springboot.model.dto.experiment.ExperimentTypeCountDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
