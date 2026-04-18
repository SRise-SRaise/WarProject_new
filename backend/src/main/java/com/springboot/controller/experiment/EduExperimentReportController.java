package com.springboot.controller.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.experiment.EduExperimentReportGradeRequest;
import com.springboot.model.dto.experiment.EduExperimentReportQueryRequest;
import com.springboot.model.dto.experiment.EduExperimentReportSubmitRequest;
import com.springboot.model.vo.experiment.EduExperimentReportListVO;
import com.springboot.model.vo.experiment.EduExperimentReportVO;
import com.springboot.service.experiment.EduExperimentReportService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/experiment/report")
public class EduExperimentReportController {

    @Resource
    private EduExperimentReportService eduExperimentReportService;

    /**
     * 获取学生的实验报告（动态拼接）
     */
    @GetMapping("/get")
    public BaseResponse<EduExperimentReportVO> getStudentReport(
            @RequestParam Long experimentId,
            @RequestParam Long studentId) {
        if (experimentId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperimentReportVO report = eduExperimentReportService.getStudentReport(experimentId, studentId);
        return ResultUtils.success(report);
    }

    /**
     * 提交/保存学生答题
     */
    @PostMapping("/submit")
    public BaseResponse<Boolean> saveStudentAnswer(@RequestBody EduExperimentReportSubmitRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (request.getExperimentId() == null || request.getStudentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "实验ID和学生ID不能为空");
        }
        boolean result = eduExperimentReportService.saveStudentAnswer(request);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存失败");
        return ResultUtils.success(true);
    }

    /**
     * 批改实验报告
     */
    @PostMapping("/grade")
    public BaseResponse<Boolean> gradeReport(@RequestBody EduExperimentReportGradeRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (request.getExperimentId() == null || request.getStudentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "实验ID和学生ID不能为空");
        }
        boolean result = eduExperimentReportService.gradeReport(request);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "批改失败");
        return ResultUtils.success(true);
    }

    /**
     * 获取学生的报告列表
     */
    @GetMapping("/list/student")
    public BaseResponse<List<EduExperimentReportVO>> getStudentReportList(@RequestParam Long studentId) {
        if (studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<EduExperimentReportVO> reports = eduExperimentReportService.getStudentReportList(studentId);
        return ResultUtils.success(reports);
    }

    /**
     * 获取实验的报告列表（教师端）
     */
    @GetMapping("/list/experiment")
    public BaseResponse<List<EduExperimentReportVO>> getExperimentReportList(@RequestParam Long experimentId) {
        if (experimentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<EduExperimentReportVO> reports = eduExperimentReportService.getExperimentReportList(experimentId);
        return ResultUtils.success(reports);
    }

    /**
     * 获取教师端实验报告列表（分页）
     */
    @GetMapping("/list/teacher/page")
    public BaseResponse<Page<EduExperimentReportListVO>> getTeacherReportListPage(EduExperimentReportQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询参数不能为空");
        }
        Page<EduExperimentReportListVO> page = eduExperimentReportService.getExperimentReportListPage(queryRequest);
        return ResultUtils.success(page);
    }
}
