package com.springboot.service.experiment;

import com.springboot.model.dto.experiment.StudentExperimentAnalysisRequest;
import com.springboot.model.vo.experiment.StudentExperimentAnalysisVO;

/**
 * 学生实验数据分析 Service 接口
 */
public interface StudentExperimentAnalysisService {

    /**
     * 获取学生实验数据分析
     *
     * @param request 包含学生ID的请求
     * @return 学生实验数据分析结果
     */
    StudentExperimentAnalysisVO getStudentExperimentAnalysis(StudentExperimentAnalysisRequest request);
}
