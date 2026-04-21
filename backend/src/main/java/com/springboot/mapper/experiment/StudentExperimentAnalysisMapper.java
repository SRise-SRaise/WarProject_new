package com.springboot.mapper.experiment;

import com.springboot.model.dto.experiment.ExperimentTypeScoreDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学生实验数据分析 Mapper
 */
@Mapper
public interface StudentExperimentAnalysisMapper {

    /**
     * 查询学生已完成的实验数量（至少有提交记录的实验）
     */
    @Select("SELECT COUNT(DISTINCT e.experiment_id) " +
            "FROM t_experiment e " +
            "JOIN t_experiment_item i ON i.experiment_id = e.experiment_id " +
            "JOIN t_student_item r ON r.item_id = i.experiment_item_id " +
            "WHERE r.student_id = #{studentId} " +
            "  AND e.state = 1 " +
            "  AND r.score_flag = 2")
    Integer countCompletedExperiments(@Param("studentId") Long studentId);

    /**
     * 查询学生已批改的实验数量
     */
    @Select("SELECT COUNT(DISTINCT e.experiment_id) " +
            "FROM t_experiment e " +
            "JOIN t_experiment_item i ON i.experiment_id = e.experiment_id " +
            "JOIN t_student_item r ON r.item_id = i.experiment_item_id " +
            "WHERE r.student_id = #{studentId} " +
            "  AND e.state = 1 " +
            "  AND r.score_flag = 2 " +
            "  AND r.score IS NOT NULL")
    Integer countGradedExperiments(@Param("studentId") Long studentId);

    /**
     * 查询学生总得分（所有已批改的题目得分之和）
     */
    @Select("SELECT IFNULL(SUM(r.score), 0) " +
            "FROM t_student_item r " +
            "JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "JOIN t_experiment e ON i.experiment_id = e.experiment_id " +
            "WHERE r.student_id = #{studentId} " +
            "  AND e.state = 1 " +
            "  AND r.score_flag = 2 " +
            "  AND r.score IS NOT NULL")
    Integer sumTotalScore(@Param("studentId") Long studentId);

    /**
     * 查询学生已批改题目的满分总和
     */
    @Select("SELECT IFNULL(SUM(i.experiment_item_score), 0) " +
            "FROM t_student_item r " +
            "JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "JOIN t_experiment e ON i.experiment_id = e.experiment_id " +
            "WHERE r.student_id = #{studentId} " +
            "  AND e.state = 1 " +
            "  AND r.score_flag = 2 " +
            "  AND r.score IS NOT NULL")
    Integer sumTotalMaxScore(@Param("studentId") Long studentId);

    /**
     * 查询学生参与实验的最高总分
     */
    @Select("SELECT IFNULL(MAX(experiment_score), 0) " +
            "FROM ( " +
            "  SELECT SUM(r.score) AS experiment_score " +
            "  FROM t_student_item r " +
            "  JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "  JOIN t_experiment e ON i.experiment_id = e.experiment_id " +
            "  WHERE r.student_id = #{studentId} " +
            "    AND e.state = 1 " +
            "    AND r.score_flag = 2 " +
            "    AND r.score IS NOT NULL " +
            "  GROUP BY e.experiment_id " +
            ") AS experiment_totals")
    Integer getMaxScore(@Param("studentId") Long studentId);

    /**
     * 查询学生参与实验的最低总分
     */
    @Select("SELECT IFNULL(MIN(experiment_score), 0) " +
            "FROM ( " +
            "  SELECT SUM(r.score) AS experiment_score " +
            "  FROM t_student_item r " +
            "  JOIN t_experiment_item i ON r.item_id = i.experiment_item_id " +
            "  JOIN t_experiment e ON i.experiment_id = e.experiment_id " +
            "  WHERE r.student_id = #{studentId} " +
            "    AND e.state = 1 " +
            "    AND r.score_flag = 2 " +
            "    AND r.score IS NOT NULL " +
            "  GROUP BY e.experiment_id " +
            ") AS experiment_totals")
    Integer getMinScore(@Param("studentId") Long studentId);

    /**
     * 按实验类型分组查询学生的得分统计
     */
    @Select("<script>" +
            "SELECT " +
            "  e.experiment_type AS typeCode, " +
            "  COUNT(DISTINCT e.experiment_id) AS experimentCount, " +
            "  IFNULL(SUM(r.score), 0) AS totalScore " +
            "FROM t_experiment e " +
            "JOIN t_experiment_item i ON i.experiment_id = e.experiment_id " +
            "JOIN t_student_item r ON r.item_id = i.experiment_item_id " +
            "WHERE r.student_id = #{studentId} " +
            "  AND e.state = 1 " +
            "  AND r.score_flag = 2 " +
            "  AND r.score IS NOT NULL " +
            "  AND e.experiment_type IN " +
            "  <foreach item='type' collection='typeCodes' open='(' separator=',' close=')'>" +
            "    #{type}" +
            "  </foreach>" +
            "GROUP BY e.experiment_type" +
            "</script>")
    List<ExperimentTypeScoreDTO> selectScoreGroupByExperimentType(
            @Param("studentId") Long studentId,
            @Param("typeCodes") List<Integer> typeCodes);

    /**
     * 查询已发布的实验总数
     */
    @Select("SELECT COUNT(*) FROM t_experiment WHERE state = 1")
    Integer countTotalPublishedExperiments();
}
