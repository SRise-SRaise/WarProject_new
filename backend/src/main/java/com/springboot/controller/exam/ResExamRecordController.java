package com.springboot.controller.exam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.constant.UserConstant;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.exam.ResExamRecordAddRequest;
import com.springboot.model.dto.exam.ResExamRecordQueryRequest;
import com.springboot.model.dto.exam.ResExamRecordUpdateRequest;
import com.springboot.model.entity.exam.ResExamRecord;
import com.springboot.model.vo.exam.ResExamRecordVO;
import com.springboot.model.vo.user.LoginPrincipal;
import com.springboot.service.exam.ResExamRecordService;
import com.springboot.service.user.AuthLoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam/resExamRecord")
public class ResExamRecordController {

    @Resource
    private ResExamRecordService resExamRecordService;

    @Resource
    private AuthLoginService authLoginService;

    @PostMapping("/student/submit")
    public BaseResponse<Map<String, Object>> submitExam(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        LoginPrincipal principal = assertStudent(request);
        return ResultUtils.success(resExamRecordService.submitExam(
                toLong(requestBody.get("examId")),
                principal.getUserId(),
                castMap(requestBody.get("answers"))));
    }

    @GetMapping("/student/result")
    public BaseResponse<Map<String, Object>> getStudentExamResult(@RequestParam Long examId, HttpServletRequest request) {
        LoginPrincipal principal = assertStudent(request);
        return ResultUtils.success(resExamRecordService.getStudentExamResult(examId, principal.getUserId()));
    }

    @GetMapping("/grading/cards")
    public BaseResponse<List<Map<String, Object>>> listExamRecordCards(@RequestParam Long examId, HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(resExamRecordService.listExamRecordCards(examId));
    }

    @GetMapping("/grading/records")
    public BaseResponse<List<Map<String, Object>>> listStudentAnswerRecords(@RequestParam Long examId, HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(resExamRecordService.listStudentAnswerRecords(examId));
    }

    @GetMapping("/grading/detail")
    public BaseResponse<Map<String, Object>> getStudentAnswerRecordDetail(@RequestParam Long recordId, HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(resExamRecordService.getStudentAnswerRecordDetail(recordId));
    }

    @PostMapping("/grading/grade")
    public BaseResponse<Boolean> gradeAnswer(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(resExamRecordService.gradeAnswer(
                toLong(requestBody.get("recordId")),
                toLong(requestBody.get("questionId")),
                toInteger(requestBody.get("score")),
                requestBody.get("comment") == null ? null : String.valueOf(requestBody.get("comment"))));
    }

    @GetMapping("/grading/stats")
    public BaseResponse<Map<String, Object>> getScoreStatistics(@RequestParam Long examId, HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(resExamRecordService.getScoreStatistics(examId));
    }

    @PostMapping("/add")
    public BaseResponse<Boolean> addResExamRecord(@RequestBody ResExamRecordAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResExamRecord entity = new ResExamRecord();
        BeanUtils.copyProperties(addRequest, entity);
        resExamRecordService.validResExamRecord(entity, true);
        boolean result = resExamRecordService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteResExamRecord(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = resExamRecordService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateResExamRecord(@RequestBody ResExamRecordUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResExamRecord entity = new ResExamRecord();
        BeanUtils.copyProperties(updateRequest, entity);
        resExamRecordService.validResExamRecord(entity, false);
        boolean result = resExamRecordService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<ResExamRecordVO> getResExamRecordVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResExamRecord entity = resExamRecordService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(resExamRecordService.getResExamRecordVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<ResExamRecord>> listResExamRecordByPage(@RequestBody ResExamRecordQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<ResExamRecord> page = resExamRecordService.page(new Page<>(current, size), resExamRecordService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ResExamRecordVO>> listResExamRecordVOByPage(@RequestBody ResExamRecordQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<ResExamRecord> page = resExamRecordService.page(new Page<>(current, size), resExamRecordService.getQueryWrapper(queryRequest));
        return ResultUtils.success(resExamRecordService.getResExamRecordVOPage(page, null));
    }

    private LoginPrincipal assertTeacher(HttpServletRequest request) {
        LoginPrincipal principal = authLoginService.getLoginPrincipal(request);
        if (!UserConstant.ROLE_TYPE_TEACHER.equals(principal.getRoleType())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅教师可访问考试批改接口");
        }
        return principal;
    }

    private LoginPrincipal assertStudent(HttpServletRequest request) {
        LoginPrincipal principal = authLoginService.getLoginPrincipal(request);
        if (!UserConstant.ROLE_TYPE_STUDENT.equals(principal.getRoleType())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅学生可提交考试");
        }
        return principal;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
