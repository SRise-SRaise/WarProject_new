package com.springboot.service.experiment;

import com.springboot.model.vo.experiment.EduExperimentAnalysisVO.ScoreDistributionItem;
import com.springboot.service.experiment.adapter.ScoreDataProvider.HasExperimentScore;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * 实验分析辅助工具类（UtilityClass 单例模式）。
 *
 * <p>集中管理分数段常量与分析辅助方法，供 {@code ExperimentAnalysisStrategy} 使用。</p>
 */
@UtilityClass
public class ExperimentAnalysisHelper {

    /** 分数区间：[start, end] */
    private static final int[][] SCORE_RANGES = {
            {0, 59},
            {60, 69},
            {70, 79},
            {80, 89},
            {90, 100}
    };

    private static final String[] SCORE_RANGE_LABELS = {
            "不及格 (0-59)",
            "及格 (60-69)",
            "中等 (70-79)",
            "良好 (80-89)",
            "优秀 (90-100)"
    };

    /**
     * 构建空的分数分布（当无评分数据时返回全零分布）。
     */
    public static List<ScoreDistributionItem> buildEmptyScoreDistribution() {
        List<ScoreDistributionItem> empty = new ArrayList<>();
        for (int i = 0; i < SCORE_RANGES.length; i++) {
            empty.add(ScoreDistributionItem.builder()
                    .label(SCORE_RANGE_LABELS[i])
                    .rangeStart(SCORE_RANGES[i][0])
                    .rangeEnd(SCORE_RANGES[i][1])
                    .count(0)
                    .percentage(0.0)
                    .build());
        }
        return empty;
    }

    /**
     * 根据实际分数列表计算分数分布。
     *
     * @param scores       学生成绩列表（可为 null）
     * @param totalGraded 已批改学生总数（用于计算百分比）
     */
    public static List<ScoreDistributionItem> buildScoreDistribution(
            List<? extends HasExperimentScore> scores, int totalGraded) {

        if (scores == null || scores.isEmpty()) {
            return buildEmptyScoreDistribution();
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

    /**
     * 安全的整数转换（null → 0）。
     */
    public static int safeInt(Integer value) {
        return value != null ? value : 0;
    }

    /**
     * 四舍五入保留两位小数。
     */
    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
