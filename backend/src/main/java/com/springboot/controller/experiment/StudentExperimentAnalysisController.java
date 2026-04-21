package com.springboot.controller.experiment;

import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.experiment.StudentExperimentAnalysisRequest;
import com.springboot.model.vo.experiment.StudentExperimentAnalysisVO;
import com.springboot.service.experiment.StudentExperimentAnalysisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生实验数据分析 Controller
 */
@Slf4j
@RestController
@RequestMapping("/experiment/analysis")
public class StudentExperimentAnalysisController {

    @Resource
    private StudentExperimentAnalysisService studentExperimentAnalysisService;

    /**
     * 获取学生实验数据分析
     *
     * @param studentId 学生ID
     * @return 学生实验数据分析结果
     */
    @GetMapping("/student")
    public BaseResponse<StudentExperimentAnalysisVO> getStudentExperimentAnalysis(
            @RequestParam Long studentId) {
        log.info("[StudentExperimentAnalysis] 获取学生实验数据分析: studentId={}", studentId);

        if (studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生ID不能为空");
        }

        StudentExperimentAnalysisRequest request = new StudentExperimentAnalysisRequest();
        request.setStudentId(studentId);

        StudentExperimentAnalysisVO result = studentExperimentAnalysisService.getStudentExperimentAnalysis(request);
        return ResultUtils.success(result);
    }
}
