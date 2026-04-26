package com.springboot.service.experiment.impl;

import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.experiment.ExperimentAnalysisRequest;
import com.springboot.model.vo.experiment.EduExperimentAnalysisVO;
import com.springboot.service.experiment.EduExperimentAnalysisService;
import com.springboot.service.experiment.adapter.ExperimentAnalysisStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 教师端实验数据分析 Service 实现。
 *
 * <p>职责：参数校验、请求分发、异常处理。
 * 核心分析逻辑委托给 {@link ExperimentAnalysisStrategy} 处理。</p>
 *
 * @see ExperimentAnalysisStrategy
 */
@Slf4j
@Service
public class EduExperimentAnalysisServiceImpl implements EduExperimentAnalysisService {

    @Resource
    private ExperimentAnalysisStrategy analysisStrategy;

    @Override
    public EduExperimentAnalysisVO getExperimentAnalysis(ExperimentAnalysisRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }

        Long experimentId = request.getExperimentId();
        String clazzNo = request.getClazzNo();

        log.info("[EduExperimentAnalysis] 获取实验分析: experimentId={}, clazzNo={}", experimentId, clazzNo);

        if (experimentId != null) {
            return analysisStrategy.buildSingleExperimentAnalysis(experimentId, clazzNo);
        } else {
            return analysisStrategy.buildGlobalAnalysis();
        }
    }
}
