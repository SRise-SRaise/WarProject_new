package com.springboot.model.vo.experiment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 教师端实验数据分析结果 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EduExperimentAnalysisVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== 1. 单个实验统计（experimentId 非空时返回） ==========

    /**
     * 实验ID
     */
    private Long experimentId;

    /**
     * 实验名称
     */
    private String experimentName;

    /**
     * 实验类型名称
     */
    private String experimentTypeName;

    /**
     * 班级编号
     */
    private String clazzNo;

    /**
     * 应交人数（选了该实验的学生总数）
     */
    private Integer totalStudents;

    /**
     * 已提交人数
     */
    private Integer submittedStudents;

    /**
     * 已批改人数
     */
    private Integer gradedStudents;

    /**
     * 提交率（百分比）
     */
    private Double submissionRate;

    /**
     * 班级平均分
     */
    private Double classAverageScore;

    /**
     * 班级最高分
     */
    private Integer classMaxScore;

    /**
     * 班级最低分
     */
    private Integer classMinScore;

    /**
     * 得分分布（各分段人数）
     */
    private List<ScoreDistributionItem> scoreDistribution;

    /**
     * 各步骤得分分析（每道题目的平均分、满分、得分率等）
     */
    private List<StepScoreAnalysisItem> stepScoreAnalysis;

    // ========== 2. 全局统计（experimentId 为空时返回） ==========

    /**
     * 已发布实验总数
     */
    private Integer totalExperiments;

    /**
     * 有学生提交的实验数
     */
    private Integer activeExperiments;

    /**
     * 已批改实验数
     */
    private Integer gradedExperiments;

    /**
     * 各类型实验统计
     */
    private List<ExperimentTypeAnalysisItem> experimentTypeAnalysis;

    /**
     * 得分分布项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreDistributionItem implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 分段名称，如 "90-100"
         */
        private String label;

        /**
         * 分段起始分（包含）
         */
        private Integer rangeStart;

        /**
         * 分段结束分（包含）
         */
        private Integer rangeEnd;

        /**
         * 该分段人数
         */
        private Integer count;

        /**
         * 占总人数百分比
         */
        private Double percentage;
    }

    /**
     * 步骤（题目）得分分析项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepScoreAnalysisItem implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 步骤序号 */
        private Integer sortOrder;

        /** 步骤/题目名称 */
        private String itemName;

        /** 题目类型（1选择 2填空 3编程 4简答 7实验小结） */
        private Integer questionType;

        /** 满分 */
        private Integer maxScore;

        /** 平均得分 */
        private Double avgScore;

        /** 最高得分 */
        private Integer highScore;

        /** 最低得分 */
        private Integer lowScore;

        /** 得分率（平均分/满分，百分比） */
        private Double scoreRate;

        /** 作答人数 */
        private Integer answeredCount;
    }

    /**
     * 实验类型统计项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperimentTypeAnalysisItem implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 实验类型编码
         */
        private Integer typeCode;

        /**
         * 实验类型名称
         */
        private String typeName;

        /**
         * 该类型实验数量
         */
        private Integer experimentCount;

        /**
         * 已提交实验数量
         */
        private Integer submittedCount;

        /**
         * 已批改实验数量
         */
        private Integer gradedCount;

        /**
         * 该类型总提交人数
         */
        private Integer totalSubmissions;

        /**
         * 该类型平均分
         */
        private Double averageScore;
    }
}
