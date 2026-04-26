package com.springboot.service.experiment.adapter;

import java.util.List;

/**
 * 实验成绩数据访问适配器接口（适配器模式的核心抽象）。
 *
 * <p>将底层数据来源（当前为 t_student_item 表，未来可能迁移到 res_experiment_result 表）
 * 抽象为统一的数据访问接口，使分析策略与具体数据源解耦。</p>
 *
 * <p>实现类示例：</p>
 * <ul>
 *   <li>{@link TStudentItemDataProvider} — 当前 t_student_item 表实现</li>
 *   <li>ResExperimentResultDataProvider — 未来 res_experiment_result 表实现（待新增）</li>
 * </ul>
 */
public interface ScoreDataProvider {

    // ==================== 单实验维度 ====================

    /**
     * 查询应交人数（选了该实验的学生总数）。
     */
    Integer countTotalStudents(Long experimentId, String clazzNo);

    /**
     * 查询已提交人数。
     */
    Integer countSubmittedStudents(Long experimentId, String clazzNo);

    /**
     * 查询已批改人数。
     */
    Integer countGradedStudents(Long experimentId, String clazzNo);

    /**
     * 查询班级平均分。
     */
    Double avgScore(Long experimentId, String clazzNo);

    /**
     * 查询班级最高分。
     */
    Integer maxScore(Long experimentId, String clazzNo);

    /**
     * 查询班级最低分。
     */
    Integer minScore(Long experimentId, String clazzNo);

    /**
     * 查询所有已批改学生的实验总分列表（用于构建分数分布）。
     */
    List<? extends HasExperimentScore> listStudentTotalScores(Long experimentId, String clazzNo);

    // ==================== 全局维度 ====================

    /**
     * 查询已发布实验总数。
     */
    Integer countPublishedExperiments();

    /**
     * 查询有学生提交的实验数。
     */
    Integer countActiveExperiments();

    /**
     * 查询已批改实验数。
     */
    Integer countGloballyGradedExperiments();

    /**
     * 查询各类型实验数量。
     */
    List<? extends HasTypeCode> listExperimentTypeCounts();

    /**
     * 查询各类型实验已提交实验数。
     */
    List<? extends HasTypeCode> listExperimentTypeSubmittedCounts();

    /**
     * 查询各类型实验已批改实验数。
     */
    List<? extends HasTypeCode> listExperimentTypeGradedCounts();

    /**
     * 查询各类型实验总提交人数。
     */
    List<? extends HasTypeCode> listExperimentTypeTotalSubmissions();

    /**
     * 查询各类型实验平均分。
     */
    List<? extends HasTypeCode> listExperimentTypeAverageScores();

    // ==================== 学生维度 ====================

    /**
     * 查询学生已完成的实验数量（至少有提交记录且已批改）。
     */
    Integer countCompletedExperiments(Long studentId);

    /**
     * 查询学生已批改的实验数量。
     */
    Integer countGradedExperiments(Long studentId);

    /**
     * 查询学生总得分（所有已批改题目的得分之和）。
     */
    Integer sumTotalScore(Long studentId);

    /**
     * 查询学生已批改题目的满分总和。
     */
    Integer sumTotalMaxScore(Long studentId);

    /**
     * 查询学生参与实验的最高总分。
     */
    Integer getMaxScore(Long studentId);

    /**
     * 查询学生参与实验的最低总分。
     */
    Integer getMinScore(Long studentId);

    /**
     * 查询学生已发布的实验总数。
     */
    Integer countTotalPublishedExperiments();

    /**
     * 查询学生各类型的得分统计。
     */
    List<? extends HasTypeCodeAndScore> listStudentScoreByTypes(Long studentId, List<Integer> typeCodes);

    // ==================== 内部接口：数据契约 ====================

    /** 拥有实验总分的数据对象（用于构建分数分布） */
    interface HasExperimentScore {
        Integer getScore();
    }

    /** 拥有类型编码的数据对象 */
    interface HasTypeCode {
        Integer getTypeCode();
    }

    /** 拥有类型编码与得分统计的数据对象（学生维度） */
    interface HasTypeCodeAndScore extends HasTypeCode {
        Integer getExperimentCount();
        Integer getTotalScore();
    }

    /** 拥有类型编码的扩展数据对象（教师全局维度，含 experimentCount/submittedCount 等） */
    interface HasTypeCodeEx extends HasTypeCode {
        default Integer getExperimentCount() { return 0; }
        default Integer getSubmittedCount() { return 0; }
        default Integer getGradedCount() { return 0; }
        default Integer getTotalSubmissions() { return 0; }
        default Double getAverageScore() { return 0.0; }
    }
}
