package com.springboot.controller.homework;

import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.homework.*;
import com.springboot.model.vo.homework.*;
import com.springboot.service.homework.EduExerciseSubmissionService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 作业提交评分控制器
 */
@RestController
@RequestMapping("/homework/submission")
public class EduExerciseSubmissionController {

    @Resource
    private EduExerciseSubmissionService eduExerciseSubmissionService;

    /**
     * 批量提交作业答案
     * POST /homework/submission/submit
     */
    @PostMapping("/submit")
    public BaseResponse<SubmissionResultVO> submitAnswers(@RequestBody ExerciseSubmitRequest submitRequest) {
        if (submitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SubmissionResultVO result = eduExerciseSubmissionService.submitAnswers(submitRequest);
        return ResultUtils.success(result);
    }

    /**
     * 暂存答案（不正式提交）
     * POST /homework/submission/saveDraft
     */
    @PostMapping("/saveDraft")
    public BaseResponse<Boolean> saveDraft(@RequestBody ExerciseSubmitRequest saveRequest) {
        if (saveRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = eduExerciseSubmissionService.saveDraft(saveRequest);
        return ResultUtils.success(result);
    }

    /**
     * 获取学生答题进度
     * GET /homework/submission/getProgress
     */
    @GetMapping("/getProgress")
    public BaseResponse<StudentProgressVO> getProgress(
            @RequestParam Long exerciseId,
            @RequestParam Long studentId) {
        if (exerciseId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        StudentProgressVO progress = eduExerciseSubmissionService.getProgress(exerciseId, studentId);
        return ResultUtils.success(progress);
    }

    /**
     * 自动评分（客观题）
     * POST /homework/submission/autoGrade
     */
    @PostMapping("/autoGrade")
    public BaseResponse<Integer> autoGrade(
            @RequestParam Long exerciseId,
            @RequestParam Long studentId) {
        if (exerciseId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer score = eduExerciseSubmissionService.autoGrade(exerciseId, studentId);
        return ResultUtils.success(score);
    }

    /**
     * 批量自动评分
     * POST /homework/submission/batchAutoGrade
     */
    @PostMapping("/batchAutoGrade")
    public BaseResponse<Integer> batchAutoGrade(@RequestParam Long exerciseId) {
        if (exerciseId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer count = eduExerciseSubmissionService.batchAutoGrade(exerciseId);
        return ResultUtils.success(count);
    }

    /**
     * 教师批阅单题
     * POST /homework/submission/reviewItem
     */
    @PostMapping("/reviewItem")
    public BaseResponse<Boolean> reviewItem(@RequestBody ReviewItemRequest reviewRequest) {
        if (reviewRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = eduExerciseSubmissionService.reviewItem(reviewRequest);
        return ResultUtils.success(result);
    }

    /**
     * 教师完成整体批阅
     * POST /homework/submission/completeReview
     */
    @PostMapping("/completeReview")
    public BaseResponse<Boolean> completeReview(@RequestBody CompleteReviewRequest reviewRequest) {
        if (reviewRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result = eduExerciseSubmissionService.completeReview(reviewRequest);
        return ResultUtils.success(result);
    }

    /**
     * 学生查询个人成绩
     * GET /homework/submission/getMyScore
     */
    @GetMapping("/getMyScore")
    public BaseResponse<StudentScoreVO> getMyScore(
            @RequestParam Long exerciseId,
            @RequestParam Long studentId) {
        if (exerciseId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        StudentScoreVO score = eduExerciseSubmissionService.getMyScore(exerciseId, studentId);
        return ResultUtils.success(score);
    }

    /**
     * 获取提交详情（教师批阅用）
     * GET /homework/submission/getDetail
     */
    @GetMapping("/getDetail")
    public BaseResponse<SubmissionDetailVO> getSubmissionDetail(
            @RequestParam Long exerciseId,
            @RequestParam Long studentId) {
        if (exerciseId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SubmissionDetailVO detail = eduExerciseSubmissionService.getSubmissionDetail(exerciseId, studentId);
        return ResultUtils.success(detail);
    }
}