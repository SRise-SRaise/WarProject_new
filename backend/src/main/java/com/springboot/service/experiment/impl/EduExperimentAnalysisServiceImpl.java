package com.springboot.service.experiment.impl;

import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.experiment.EduExperimentAnalysisMapper;
import com.springboot.mapper.experiment.EduExperimentMapper;
import com.springboot.model.dto.experiment.ExperimentAnalysisRequest;
import com.springboot.model.dto.experiment.ExperimentScoreDistributionDTO;
import com.springboot.model.dto.experiment.ExperimentTypeCountDTO;
import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.enums.experiment.ExperimentTypeEnum;
import com.springboot.model.vo.experiment.EduExperimentAnalysisVO;
import com.springboot.model.vo.experiment.EduExperimentAnalysisVO.ExperimentTypeAnalysisItem;
import com.springboot.model.vo.experiment.EduExperimentAnalysisVO.ScoreDistributionItem;
import com.springboot.service.experiment.EduExperimentAnalysisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教师端实验数据分析 Service 实现
 */
@Slf4j
@Service
public class EduExperimentAnalysisServiceImpl implements EduExperimentAnalysisService {

    @Resource
    private EduExperimentAnalysisMapper analysisMapper;

    @Resource
    private EduExperimentMapper eduExperimentMapper;

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

    @Override
    public EduExperimentAnalysisVO getExperimentAnalysis(ExperimentAnalysisRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }

        Long experimentId = request.getExperimentId();
        String clazzNo = request.getClazzNo();

        log.info("[EduExperimentAnalysis] 开始分析实验数据: experimentId={}, clazzNo={}", experimentId, clazzNo);

        if (experimentId != null) {
            return buildSingleExperimentAnalysis(experimentId, clazzNo);
        } else {
            return buildGlobalAnalysis();
        }
    }

    /**
     * 单个实验分析
     */
    private EduExperimentAnalysisVO buildSingleExperimentAnalysis(Long experimentId, String clazzNo) {
        // 查询实验信息
        EduExperiment experiment = eduExperimentMapper.selectById(experimentId);
        if (experiment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "实验不存在");
        }

        String typeName = ExperimentTypeEnum.getTextByValue(experiment.getCategoryId());

        // 各项统计
        Integer totalStudents = safeInt(analysisMapper.countTotalStudents(experimentId, clazzNo));
        Integer submittedStudents = safeInt(analysisMapper.countSubmittedStudents(experimentId, clazzNo));
        Integer gradedStudents = safeInt(analysisMapper.countGradedStudents(experimentId, clazzNo));
        Double avgScore = analysisMapper.avgScore(experimentId, clazzNo);
        Integer maxScore = safeInt(analysisMapper.maxScore(experimentId, clazzNo));
        Integer minScore = safeInt(analysisMapper.minScore(experimentId, clazzNo));

        // 计算提交率
        Double submissionRate = 0.0;
        if (totalStudents > 0) {
            submissionRate = Math.round(submittedStudents * 100.0 / totalStudents * 100.0) / 100.0;
        }

        // 处理平均分
        if (avgScore == null) {
            avgScore = 0.0;
        }
        avgScore = Math.round(avgScore * 100.0) / 100.0;

        // 查询得分分布
        List<ScoreDistributionItem> scoreDistribution = buildScoreDistribution(experimentId, clazzNo, submittedStudents);

        log.info("[EduExperimentAnalysis] 单实验分析完成: experimentId={}, 应交={}, 已交={}, 已批改={}, 平均分={}",
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
     * 全局分析
     */
    private EduExperimentAnalysisVO buildGlobalAnalysis() {
        Integer totalExperiments = safeInt(analysisMapper.countPublishedExperiments());
        Integer activeExperiments = safeInt(analysisMapper.countActiveExperiments());
        Integer gradedExperiments = safeInt(analysisMapper.countGloballyGradedExperiments());

        // 查询各类型实验数量
        List<ExperimentTypeCountDTO> countList = analysisMapper.selectExperimentTypeCounts();
        Map<Integer, ExperimentTypeCountDTO> typeMap = countList.stream()
                .collect(Collectors.toMap(ExperimentTypeCountDTO::getTypeCode, dto -> dto, (a, b) -> a));

        // 查询各类型已提交数
        List<ExperimentTypeCountDTO> submittedList = analysisMapper.selectExperimentTypeSubmittedCounts();
        for (ExperimentTypeCountDTO dto : submittedList) {
            ExperimentTypeCountDTO existing = typeMap.get(dto.getTypeCode());
            if (existing != null) {
                existing.setSubmittedCount(dto.getSubmittedCount());
            } else {
                dto.setExperimentCount(0);
                dto.setGradedCount(0);
                dto.setTotalSubmissions(0);
                dto.setAverageScore(0.0);
                typeMap.put(dto.getTypeCode(), dto);
            }
        }

        // 查询各类型已批改数
        List<ExperimentTypeCountDTO> gradedList = analysisMapper.selectExperimentTypeGradedCounts();
        for (ExperimentTypeCountDTO dto : gradedList) {
            ExperimentTypeCountDTO existing = typeMap.get(dto.getTypeCode());
            if (existing != null) {
                existing.setGradedCount(dto.getGradedCount());
            }
        }

        // 查询各类型总提交数
        List<ExperimentTypeCountDTO> subList = analysisMapper.selectExperimentTypeTotalSubmissions();
        for (ExperimentTypeCountDTO dto : subList) {
            ExperimentTypeCountDTO existing = typeMap.get(dto.getTypeCode());
            if (existing != null) {
                existing.setTotalSubmissions(dto.getTotalSubmissions());
            }
        }

        // 构造类型分析列表
        List<ExperimentTypeAnalysisItem> typeAnalysisItems = new ArrayList<>();
        for (ExperimentTypeEnum typeEnum : ExperimentTypeEnum.values()) {
            Integer typeCode = typeEnum.getValue();
            ExperimentTypeCountDTO dto = typeMap.get(typeCode);

            int expCount = dto != null ? safeInt(dto.getExperimentCount()) : 0;
            int submittedCount = dto != null ? safeInt(dto.getSubmittedCount()) : 0;
            int gradedCount = dto != null ? safeInt(dto.getGradedCount()) : 0;
            int totalSubmissions = dto != null ? safeInt(dto.getTotalSubmissions()) : 0;
            double avgScore = dto != null && dto.getAverageScore() != null ? dto.getAverageScore() : 0.0;

            ExperimentTypeAnalysisItem item = ExperimentTypeAnalysisItem.builder()
                    .typeCode(typeCode)
                    .typeName(typeEnum.getText())
                    .experimentCount(expCount)
                    .submittedCount(submittedCount)
                    .gradedCount(gradedCount)
                    .totalSubmissions(totalSubmissions)
                    .averageScore(Math.round(avgScore * 100.0) / 100.0)
                    .build();
            typeAnalysisItems.add(item);
        }

        log.info("[EduExperimentAnalysis] 全局分析完成: 实验总数={}, 有提交={}, 已批改={}",
                totalExperiments, activeExperiments, gradedExperiments);

        return EduExperimentAnalysisVO.builder()
                .totalExperiments(totalExperiments)
                .activeExperiments(activeExperiments)
                .gradedExperiments(gradedExperiments)
                .experimentTypeAnalysis(typeAnalysisItems)
                .build();
    }

    /**
     * 构建得分分布
     */
    private List<ScoreDistributionItem> buildScoreDistribution(Long experimentId, String clazzNo, int totalGraded) {
        List<ExperimentScoreDistributionDTO> scores = analysisMapper.selectStudentScores(experimentId, clazzNo);
        if (scores == null || scores.isEmpty()) {
            // 返回空分布
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

        int[] counts = new int[SCORE_RANGES.length];
        for (ExperimentScoreDistributionDTO dto : scores) {
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

    private Integer safeInt(Integer value) {
        return value != null ? value : 0;
    }
}
