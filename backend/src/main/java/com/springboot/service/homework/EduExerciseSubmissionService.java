package com.springboot.service.homework;

import com.springboot.model.dto.homework.*;
import com.springboot.model.vo.homework.*;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 作业提交评分服务接口
 */
public interface EduExerciseSubmissionService {

    /**
     * 提交作业答案
     * @param submitRequest 提交请求
     * @return 提交结果
     */
    SubmissionResultVO submitAnswers(ExerciseSubmitRequest submitRequest);

    /**
     * 暂存答案（不正式提交）
     * @param saveRequest 暂存请求
     * @return 是否成功
     */
    Boolean saveDraft(ExerciseSubmitRequest saveRequest);

    /**
     * 获取学生答题进度
     * @param exerciseId 作业ID
     * @param studentId 学生ID
     * @return 答题进度
     */
    StudentProgressVO getProgress(Long exerciseId, Long studentId);

    /**
     * 自动评分（客观题）
     * @param exerciseId 作业ID
     * @param studentId 学生ID
     * @return 自动评分结果
     */
    Integer autoGrade(Long exerciseId, Long studentId);

    /**
     * 批量自动评分
     * @param exerciseId 作业ID
     * @return 已评分学生数量
     */
    Integer batchAutoGrade(Long exerciseId);

    /**
     * 教师批阅单题
     * @param reviewRequest 批阅请求
     * @return 是否成功
     */
    Boolean reviewItem(ReviewItemRequest reviewRequest);

    /**
     * 教师完成整体批阅
     * @param reviewRequest 完成批阅请求
     * @return 是否成功
     */
    Boolean completeReview(CompleteReviewRequest reviewRequest);

    /**
     * 学生查询个人成绩
     * @param exerciseId 作业ID
     * @param studentId 学生ID
     * @return 成绩详情
     */
    StudentScoreVO getMyScore(Long exerciseId, Long studentId);

    /**
     * 获取提交详情（教师批阅用）
     * @param exerciseId 作业ID
     * @param studentId 学生ID
     * @return 提交详情
     */
    SubmissionDetailVO getSubmissionDetail(Long exerciseId, Long studentId);
}