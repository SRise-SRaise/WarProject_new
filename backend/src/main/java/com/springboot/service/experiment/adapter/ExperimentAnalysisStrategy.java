package com.springboot.service.experiment.adapter;

import com.springboot.mapper.experiment.EduExperimentMapper;
import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.enums.experiment.ExperimentTypeEnum;
import com.springboot.model.vo.experiment.EduExperimentAnalysisVO;
import com.springboot.model.vo.experiment.EduExperimentAnalysisVO.ExperimentTypeAnalysisItem;
import com.springboot.model.vo.experiment.EduExperimentAnalysisVO.ScoreDistributionItem;
import com.springboot.model.vo.experiment.StudentExperimentAnalysisVO;
import com.springboot.model.vo.experiment.StudentExperimentAnalysisVO.ExperimentTypeScoreVO;
import com.springboot.service.experiment.adapter.ScoreDataProvider.HasExperimentScore;
import com.springboot.service.experiment.adapter.ScoreDataProvider.HasTypeCode;
import com.springboot.service.experiment.adapter.ScoreDataProvider.HasTypeCodeAndScore;
import com.springboot.service.experiment.adapter.ScoreDataProvider.HasTypeCodeEx;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 实验分析策略类（适配器模式的 Strategy / 使用方）。
 *
 * <p>所有实验分析的核心业务逻辑均在此类中实现，逻辑与具体数据源完全解耦。
 * 当数据源通过 {@link ScoreDataProvider} 切换时，此类无需任何改动。</p>
 *
 * <p>该类是 Spring 组件，通过构造器持有 {@link ScoreDataProvider} 实例。</p>
 *
 * <p>典型调用链：</p>
 * <pre>
 *   Controller → EduExperimentAnalysisServiceImpl → ExperimentAnalysisStrategy → ScoreDataProvider
 *   Controller → StudentExperimentAnalysisServiceImpl → ExperimentAnalysisStrategy → ScoreDataProvider
 * </pre>
 *
 * @see ScoreDataProvider
 * @see TStudentItemDataProvider
 */
@Slf4j
@Component
public class ExperimentAnalysisStrategy {

    @Resource
    private ScoreDataProvider dataProvider;

    @Resource
    private EduExperimentMapper eduExperimentMapper;

    // ==================== 教师端分析 ====================

    /**
     * 构建单个实验的分析数据。
     */
    public EduExperimentAnalysisVO buildSingleExperimentAnalysis(Long experimentId, String clazzNo) {
        EduExperiment experiment = eduExperimentMapper.selectById(experimentId);
        if (experiment == null) {
            return null;
        }

        String typeName = ExperimentTypeEnum.getTextByValue(experiment.getCategoryId());

        int totalStudents = safeInt(dataProvider.countTotalStudents(experimentId, clazzNo));
        int submittedStudents = safeInt(dataProvider.countSubmittedStudents(experimentId, clazzNo));
        int gradedStudents = safeInt(dataProvider.countGradedStudents(experimentId, clazzNo));
        Double avgScore = dataProvider.avgScore(experimentId, clazzNo);
        int maxScore = safeInt(dataProvider.maxScore(experimentId, clazzNo));
        int minScore = safeInt(dataProvider.minScore(experimentId, clazzNo));

        double submissionRate = totalStudents > 0
                ? round2(submittedStudents * 100.0 / totalStudents)
                : 0.0;

        avgScore = avgScore == null ? 0.0 : round2(avgScore);

        List<? extends HasExperimentScore> scoreList =
                dataProvider.listStudentTotalScores(experimentId, clazzNo);
        List<ScoreDistributionItem> scoreDistribution =
                buildScoreDistribution(scoreList, submittedStudents);

        log.info("[Strategy] 单实验分析: experimentId={}, 应交={}, 已交={}, 已批改={}, 平均分={}",
                experimentId, totalStudents, submittedStudents, gradedStudents, avgScore);

        return EduExperimentAnalysisVO.builder()
                .experimentId(experimentId)
                .experimentName(experiment.getName())
                .experimentTypeName(typeName)
                .clazzNo(clazzNo)
                .totalStudents(totalStudents)
                .submittedStudents(submittedStudents)
                .gradedStudents(gradedStudents)
                .submissionRate(submissionRate)
                .classAverageScore(avgScore)
                .classMaxScore(maxScore)
                .classMinScore(minScore)
                .scoreDistribution(scoreDistribution)
                .build();
    }

    /**
     * 构建全局分析数据（不指定 experimentId）。
     */
    public EduExperimentAnalysisVO buildGlobalAnalysis() {
        int totalExperiments = safeInt(dataProvider.countPublishedExperiments());
        int activeExperiments = safeInt(dataProvider.countActiveExperiments());
        int gradedExperiments = safeInt(dataProvider.countGloballyGradedExperiments());

        Map<Integer, GlobalTypeStats> typeStats = buildInitialTypeStats();

        mergeGlobalCounts(dataProvider.listExperimentTypeCounts(), typeStats,
                GlobalTypeStats::setExperimentCount);
        mergeGlobalCounts(dataProvider.listExperimentTypeSubmittedCounts(), typeStats,
                GlobalTypeStats::setSubmittedCount);
        mergeGlobalCounts(dataProvider.listExperimentTypeGradedCounts(), typeStats,
                GlobalTypeStats::setGradedCount);
        mergeGlobalCounts(dataProvider.listExperimentTypeTotalSubmissions(), typeStats,
                GlobalTypeStats::setTotalSubmissions);
        mergeGlobalAvgScores(dataProvider.listExperimentTypeAverageScores(), typeStats);

        List<ExperimentTypeAnalysisItem> typeAnalysisItems = new ArrayList<>();
        for (ExperimentTypeEnum typeEnum : ExperimentTypeEnum.values()) {
            GlobalTypeStats s = typeStats.get(typeEnum.getValue());
            typeAnalysisItems.add(ExperimentTypeAnalysisItem.builder()
                    .typeCode(typeEnum.getValue())
                    .typeName(typeEnum.getText())
                    .experimentCount(s != null ? s.experimentCount : 0)
                    .submittedCount(s != null ? s.submittedCount : 0)
                    .gradedCount(s != null ? s.gradedCount : 0)
                    .totalSubmissions(s != null ? s.totalSubmissions : 0)
                    .averageScore(s != null ? s.averageScore : 0.0)
                    .build());
        }

        log.info("[Strategy] 全局分析: 实验总数={}, 有提交={}, 已批改={}",
                totalExperiments, activeExperiments, gradedExperiments);

        return EduExperimentAnalysisVO.builder()
                .totalExperiments(totalExperiments)
                .activeExperiments(activeExperiments)
                .gradedExperiments(gradedExperiments)
                .experimentTypeAnalysis(typeAnalysisItems)
                .build();
    }

    // ==================== 学生端分析 ====================

    /**
     * 构建学生实验分析数据。
     */
    public StudentExperimentAnalysisVO buildStudentAnalysis(Long studentId) {
        int totalPublished = safeInt(dataProvider.countTotalPublishedExperiments());
        int completedExperiments = safeInt(dataProvider.countCompletedExperiments(studentId));

        double completionRate = totalPublished > 0
                ? round2(completedExperiments * 100.0 / totalPublished)
                : 0.0;

        int gradedExperiments = safeInt(dataProvider.countGradedExperiments(studentId));
        int totalScore = safeInt(dataProvider.sumTotalScore(studentId));
        int totalMaxScore = safeInt(dataProvider.sumTotalMaxScore(studentId));
        int maxScore = safeInt(dataProvider.getMaxScore(studentId));
        int minScore = safeInt(dataProvider.getMinScore(studentId));

        double averageScore = completedExperiments > 0
                ? round2(totalScore * 1.0 / completedExperiments)
                : 0.0;

        List<Integer> allTypeCodes = ExperimentTypeEnum.getAllValues();
        List<? extends HasTypeCodeAndScore> typeScoreList =
                dataProvider.listStudentScoreByTypes(studentId, allTypeCodes);

        Map<Integer, StudentTypeStats> typeScoreMap = new HashMap<>();
        for (HasTypeCodeAndScore dto : typeScoreList) {
            StudentTypeStats s = new StudentTypeStats();
            s.typeCode = dto.getTypeCode();
            s.experimentCount = safeInt(dto.getExperimentCount());
            s.totalScore = safeInt(dto.getTotalScore());
            typeScoreMap.put(dto.getTypeCode(), s);
        }

        List<ExperimentTypeScoreVO> typeScores = new ArrayList<>();
        for (ExperimentTypeEnum typeEnum : ExperimentTypeEnum.values()) {
            StudentTypeStats s = typeScoreMap.get(typeEnum.getValue());

            int expCount = s != null ? s.experimentCount : 0;
            int score = s != null ? s.totalScore : 0;
            double avg = s != null && s.experimentCount > 0
                    ? round2(s.totalScore * 1.0 / s.experimentCount)
                    : 0.0;

            typeScores.add(ExperimentTypeScoreVO.builder()
                    .typeCode(typeEnum.getValue())
                    .typeName(typeEnum.getText())
                    .experimentCount(expCount)
                    .totalScore(score)
                    .averageScore(avg)
                    .build());
        }

        log.info("[Strategy] 学生分析完成: studentId={}, 已完成={}/{}, 平均分={}",
                studentId, completedExperiments, totalPublished, averageScore);

        return StudentExperimentAnalysisVO.builder()
                .totalPublishedExperiments(totalPublished)
                .completedExperiments(completedExperiments)
                .completionRate(completionRate)
                .gradedExperiments(gradedExperiments)
                .totalScore(totalScore)
                .totalMaxScore(totalMaxScore)
                .averageScore(averageScore)
                .maxScore(maxScore)
                .minScore(minScore)
                .experimentTypeScores(typeScores)
                .build();
    }

    // ==================== 分数分布 ====================

    /**
     * 构建分数分布。
     */
    public List<ScoreDistributionItem> buildScoreDistribution(
            List<? extends HasExperimentScore> scores, int totalGraded) {

        int[][] SCORE_RANGES = {
                {0, 59}, {60, 69}, {70, 79}, {80, 89}, {90, 100}
        };
        String[] SCORE_RANGE_LABELS = {
                "不及格 (0-59)", "及格 (60-69)", "中等 (70-79)", "良好 (80-89)", "优秀 (90-100)"
        };

        if (scores == null || scores.isEmpty()) {
            return buildEmptyScoreDistribution(SCORE_RANGES, SCORE_RANGE_LABELS);
        }

        int[] counts = new int[SCORE_RANGES.length];
        for (HasExperimentScore dto : scores) {
            int score = dto.getScore() != null ? dto.getScore() : 0;
            for (int i = 0; i < SCORE_RANGES.length; i++) {
                if (score >= SCORE_RANGES[i][0] && score <= SCORE_RANGES[i][1]) {
                    counts[i]++;
                    break;
                }
            }
        }

        List<ScoreDistributionItem> distribution = new ArrayList<>();
        for (int i = 0; i < SCORE_RANGES.length; i++) {
            double percentage = totalGraded > 0
                    ? Math.round(counts[i] * 100.0 / totalGraded * 100.0) / 100.0
                    : 0.0;
            distribution.add(ScoreDistributionItem.builder()
                    .label(SCORE_RANGE_LABELS[i])
                    .rangeStart(SCORE_RANGES[i][0])
                    .rangeEnd(SCORE_RANGES[i][1])
                    .count(counts[i])
                    .percentage(percentage)
                    .build());
        }
        return distribution;
    }

    // ==================== 工具方法 ====================

    public int safeInt(Integer value) {
        return value != null ? value : 0;
    }

    public double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private List<ScoreDistributionItem> buildEmptyScoreDistribution(
            int[][] ranges, String[] labels) {
        List<ScoreDistributionItem> empty = new ArrayList<>();
        for (int i = 0; i < ranges.length; i++) {
            empty.add(ScoreDistributionItem.builder()
                    .label(labels[i])
                    .rangeStart(ranges[i][0])
                    .rangeEnd(ranges[i][1])
                    .count(0)
                    .percentage(0.0)
                    .build());
        }
        return empty;
    }

    private Map<Integer, GlobalTypeStats> buildInitialTypeStats() {
        Map<Integer, GlobalTypeStats> map = new HashMap<>();
        for (ExperimentTypeEnum typeEnum : ExperimentTypeEnum.values()) {
            map.put(typeEnum.getValue(), new GlobalTypeStats(typeEnum.getValue()));
        }
        return map;
    }

    @FunctionalInterface
    interface IntSetter<T> {
        void set(T target, int value);
    }

    private <T extends HasTypeCode> void mergeGlobalCounts(
            List<? extends T> dtos,
            Map<Integer, GlobalTypeStats> stats,
            IntSetter<GlobalTypeStats> setter) {
        for (T dto : dtos) {
            GlobalTypeStats s = stats.get(dto.getTypeCode());
            if (s != null) {
                setter.set(s, safeInt(getCount(dto)));
            } else {
                GlobalTypeStats newStats = new GlobalTypeStats(dto.getTypeCode());
                stats.put(dto.getTypeCode(), newStats);
            }
        }
    }

    private void mergeGlobalAvgScores(List<? extends HasTypeCode> dtos, Map<Integer, GlobalTypeStats> stats) {
        for (HasTypeCode dto : dtos) {
            GlobalTypeStats s = stats.get(dto.getTypeCode());
            if (s != null && dto instanceof HasTypeCodeEx ex) {
                s.averageScore = ex.getAverageScore() != null ? ex.getAverageScore() : 0.0;
            }
        }
    }

    private int getCount(HasTypeCode dto) {
        if (dto instanceof HasTypeCodeEx ex) {
            Integer v = ex.getExperimentCount();
            return v != null ? v : 0;
        }
        return 0;
    }

    // ==================== 内部数据容器 ====================

    /** 全局分析：各类型统计结果容器 */
    private static class GlobalTypeStats {
        final Integer typeCode;
        int experimentCount = 0;
        int submittedCount = 0;
        int gradedCount = 0;
        int totalSubmissions = 0;
        double averageScore = 0.0;

        GlobalTypeStats(Integer typeCode) {
            this.typeCode = typeCode;
        }

        void setExperimentCount(int v) { this.experimentCount = v; }
        void setSubmittedCount(int v) { this.submittedCount = v; }
        void setGradedCount(int v) { this.gradedCount = v; }
        void setTotalSubmissions(int v) { this.totalSubmissions = v; }
    }

    /** 学生分析：各类型得分结果容器 */
    private static class StudentTypeStats {
        Integer typeCode;
        int experimentCount = 0;
        int totalScore = 0;
    }
}
