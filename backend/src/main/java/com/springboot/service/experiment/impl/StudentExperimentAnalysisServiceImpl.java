package com.springboot.service.experiment.impl;

import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.experiment.StudentExperimentAnalysisMapper;
import com.springboot.model.dto.experiment.ExperimentTypeScoreDTO;
import com.springboot.model.dto.experiment.StudentExperimentAnalysisRequest;
import com.springboot.model.enums.experiment.ExperimentTypeEnum;
import com.springboot.model.vo.experiment.StudentExperimentAnalysisVO;
import com.springboot.service.experiment.StudentExperimentAnalysisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生实验数据分析 Service 实现
 */
@Slf4j
@Service
public class StudentExperimentAnalysisServiceImpl implements StudentExperimentAnalysisService {

    @Resource
    private StudentExperimentAnalysisMapper analysisMapper;

    @Override
    public StudentExperimentAnalysisVO getStudentExperimentAnalysis(StudentExperimentAnalysisRequest request) {
        if (request == null || request.getStudentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生ID不能为空");
        }
        Long studentId = request.getStudentId();
        log.info("[StudentExperimentAnalysis] 开始分析学生实验数据: studentId={}", studentId);

        // 获取已发布实验总数
        Integer totalPublished = analysisMapper.countTotalPublishedExperiments();
        if (totalPublished == null) {
            totalPublished = 0;
        }

        // 获取已完成实验数（已批改的）
        Integer completedExperiments = analysisMapper.countCompletedExperiments(studentId);
        if (completedExperiments == null) {
            completedExperiments = 0;
        }

        // 计算完成率
        Double completionRate = 0.0;
        if (totalPublished > 0) {
            completionRate = Math.round(completedExperiments * 100.0 / totalPublished * 100.0) / 100.0;
        }

        // 获取已批改实验数
        Integer gradedExperiments = analysisMapper.countGradedExperiments(studentId);
        if (gradedExperiments == null) {
            gradedExperiments = 0;
        }

        // 获取总分
        Integer totalScore = analysisMapper.sumTotalScore(studentId);
        if (totalScore == null) {
            totalScore = 0;
        }

        // 获取满分
        Integer totalMaxScore = analysisMapper.sumTotalMaxScore(studentId);
        if (totalMaxScore == null) {
            totalMaxScore = 0;
        }

        // 计算平均分（以实验为单位）
        Double averageScore = 0.0;
        if (completedExperiments > 0) {
            // 用各实验总分 / 实验数来计算平均分
            Double avgViaExperiments = Math.round(totalScore * 1.0 / completedExperiments * 100.0) / 100.0;
            averageScore = avgViaExperiments;
        }

        // 获取最高分、最低分
        Integer maxScore = analysisMapper.getMaxScore(studentId);
        Integer minScore = analysisMapper.getMinScore(studentId);
        if (maxScore == null) {
            maxScore = 0;
        }
        if (minScore == null) {
            minScore = 0;
        }

        // ========== 按实验类型维度查询得分 ==========
        List<Integer> allTypeCodes = ExperimentTypeEnum.getAllValues();
        List<ExperimentTypeScoreDTO> typeScoreList = analysisMapper.selectScoreGroupByExperimentType(studentId, allTypeCodes);

        // 建立类型code到统计结果的映射
        Map<Integer, ExperimentTypeScoreDTO> typeScoreMap = typeScoreList.stream()
                .collect(Collectors.toMap(ExperimentTypeScoreDTO::getTypeCode, dto -> dto, (a, b) -> a));

        // 构造完整类型列表，未参与的类型填充0
        List<StudentExperimentAnalysisVO.ExperimentTypeScoreVO> typeScores = new ArrayList<>();
        for (ExperimentTypeEnum typeEnum : ExperimentTypeEnum.values()) {
            Integer typeCode = typeEnum.getValue();
            ExperimentTypeScoreDTO dto = typeScoreMap.get(typeCode);

            StudentExperimentAnalysisVO.ExperimentTypeScoreVO vo = new StudentExperimentAnalysisVO.ExperimentTypeScoreVO();
            vo.setTypeCode(typeCode);
            vo.setTypeName(typeEnum.getText());
            vo.setExperimentCount(dto != null ? dto.getExperimentCount() : 0);
            vo.setTotalScore(dto != null ? dto.getTotalScore() : 0);
            vo.setAverageScore(dto != null && dto.getExperimentCount() > 0
                    ? Math.round(dto.getTotalScore() * 1.0 / dto.getExperimentCount() * 100.0) / 100.0
                    : 0.0);
            typeScores.add(vo);
        }

        log.info("[StudentExperimentAnalysis] 分析完成: studentId={}, 已完成={}/{}, 平均分={}",
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
}
