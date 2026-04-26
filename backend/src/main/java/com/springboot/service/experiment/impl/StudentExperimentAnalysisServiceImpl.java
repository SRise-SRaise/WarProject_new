package com.springboot.service.experiment.impl;

import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.experiment.StudentExperimentAnalysisRequest;
import com.springboot.model.vo.experiment.StudentExperimentAnalysisVO;
import com.springboot.service.experiment.StudentExperimentAnalysisService;
import com.springboot.service.experiment.adapter.ExperimentAnalysisStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 学生实验数据分析 Service 实现。
 *
 * <p>职责：参数校验、异常处理。
 * 核心分析逻辑委托给 {@link ExperimentAnalysisStrategy} 处理。</p>
 *
 * @see ExperimentAnalysisStrategy
 */
@Slf4j
@Service
public class StudentExperimentAnalysisServiceImpl implements StudentExperimentAnalysisService {

    @Resource
    private ExperimentAnalysisStrategy analysisStrategy;

    @Override
    public StudentExperimentAnalysisVO getStudentExperimentAnalysis(StudentExperimentAnalysisRequest request) {
        if (request == null || request.getStudentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生ID不能为空");
        }

        Long studentId = request.getStudentId();
        log.info("[StudentExperimentAnalysis] 获取学生实验分析: studentId={}", studentId);

        return analysisStrategy.buildStudentAnalysis(studentId);
    }
}
