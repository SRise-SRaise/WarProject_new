package com.springboot.service.experiment;

import com.springboot.model.dto.experiment.ExperimentAnalysisRequest;
import com.springboot.model.vo.experiment.EduExperimentAnalysisVO;

/**
 * 教师端实验数据分析 Service 接口
 */
public interface EduExperimentAnalysisService {

    /**
     * 获取实验数据分析
     *
     * @param request 包含 experimentId（可选）和 clazzNo（可选）的请求
     * @return 实验数据分析结果
     */
    EduExperimentAnalysisVO getExperimentAnalysis(ExperimentAnalysisRequest request);
}
