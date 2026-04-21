package com.springboot.model.vo.experiment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 学生实验数据分析结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentExperimentAnalysisVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== 1. 实验报告完成情况统计 ==========

    /**
     * 应完成实验总数（已发布的实验）
     */
    private Integer totalPublishedExperiments;

    /**
     * 已完成实验数（至少有提交记录的实验）
     */
    private Integer completedExperiments;

    /**
     * 完成率（百分比，如 75.0 表示 75%）
     */
    private Double completionRate;

    /**
     * 已批改实验数
     */
    private Integer gradedExperiments;

    // ========== 2. 成绩统计 ==========

    /**
     * 总得分（所有已批改题目的得分之和）
     */
    private Integer totalScore;

    /**
     * 已批改题目总分（满分）
     */
    private Integer totalMaxScore;

    /**
     * 平均分（已批改实验的平均得分）
     */
    private Double averageScore;

    /**
     * 最高分
     */
    private Integer maxScore;

    /**
     * 最低分
     */
    private Integer minScore;

    // ========== 3. 实验类型维度得分分布 ==========

    /**
     * 各类型实验的得分统计列表
     */
    private List<ExperimentTypeScoreVO> experimentTypeScores;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperimentTypeScoreVO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 实验类型编码（1-6）
         */
        private Integer typeCode;

        /**
         * 实验类型名称（编程实践、设计实现、数据库等）
         */
        private String typeName;

        /**
         * 参与该类型实验的数量
         */
        private Integer experimentCount;

        /**
         * 该类型实验的总得分
         */
        private Integer totalScore;

        /**
         * 该类型实验的平均得分
         */
        private Double averageScore;
    }
}
