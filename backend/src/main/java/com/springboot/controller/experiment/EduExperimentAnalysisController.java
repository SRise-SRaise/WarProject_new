package com.springboot.controller.experiment;

import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.experiment.ExperimentAnalysisRequest;
import com.springboot.model.vo.experiment.EduExperimentAnalysisVO;
import com.springboot.service.experiment.EduExperimentAnalysisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 教师端实验数据分析 Controller
 */
@Slf4j
@RestController
@RequestMapping("/experiment/analysis")
public class EduExperimentAnalysisController {

    @Resource
    private EduExperimentAnalysisService eduExperimentAnalysisService;

    /**
     * 获取实验数据分析
     *
     * @param experimentId 实验ID（可选，不传则返回全局统计）
     * @param clazzNo      班级编号（可选）
     * @return 实验数据分析结果
     */
    @GetMapping("/experiment")
    public BaseResponse<EduExperimentAnalysisVO> getExperimentAnalysis(
            @RequestParam(required = false) Long experimentId,
            @RequestParam(required = false) String clazzNo) {
        log.info("[EduExperimentAnalysis] 获取实验数据分析: experimentId={}, clazzNo={}", experimentId, clazzNo);

        ExperimentAnalysisRequest request = new ExperimentAnalysisRequest();
        request.setExperimentId(experimentId);
        request.setClazzNo(clazzNo);

        EduExperimentAnalysisVO result = eduExperimentAnalysisService.getExperimentAnalysis(request);
        return ResultUtils.success(result);
    }
}
