package com.springboot.controller.exam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.constant.UserConstant;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.exam.EduExamAddRequest;
import com.springboot.model.dto.exam.EduExamQueryRequest;
import com.springboot.model.dto.exam.EduExamUpdateRequest;
import com.springboot.model.entity.exam.EduExam;
import com.springboot.model.vo.exam.EduExamVO;
import com.springboot.model.vo.user.LoginPrincipal;
import com.springboot.service.exam.EduExamService;
import com.springboot.service.user.AuthLoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@RequestMapping("/exam/eduExam")
public class EduExamController {

    @Resource
    private EduExamService eduExamService;

    @Resource
    private AuthLoginService authLoginService;

    @PostMapping("/list/page/full")
    public BaseResponse<Map<String, Object>> listExamPageFull(@RequestBody EduExamQueryRequest queryRequest, HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(eduExamService.listExamPage(queryRequest));
    }

    @GetMapping("/stats")
    public BaseResponse<Map<String, Object>> getExamStats(HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(eduExamService.getExamStats());
    }

    @GetMapping("/papers/options")
    public BaseResponse<List<Map<String, Object>>> listPaperOptions(HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(eduExamService.listAllPapersForExam());
    }

    @PostMapping("/custom/add")
    public BaseResponse<Long> addExamCustom(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        assertTeacher(request);
        Long id = eduExamService.createExam(
                toText(requestBody.get("examName")),
                toLong(requestBody.get("paperId")),
                toInteger(requestBody.get("durationMin")),
                toDate(requestBody.get("startTime")),
                toDate(requestBody.get("endTime")),
                toBooleanObject(requestBody.get("isPublished")));
        return ResultUtils.success(id);
    }

    @PostMapping("/custom/update")
    public BaseResponse<Boolean> updateExamCustom(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        assertTeacher(request);
        boolean result = eduExamService.updateExamCustom(
                toLong(requestBody.get("id")),
                toText(requestBody.get("examName")),
                toLong(requestBody.get("paperId")),
                toInteger(requestBody.get("durationMin")),
                toDate(requestBody.get("startTime")),
                toDate(requestBody.get("endTime")),
                toBooleanObject(requestBody.get("isPublished")));
        return ResultUtils.success(result);
    }

    @PostMapping("/publish")
    public BaseResponse<Boolean> publishExam(@RequestParam Long id, HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(eduExamService.publishExam(id));
    }

    @PostMapping("/unpublish")
    public BaseResponse<Boolean> unpublishExam(@RequestParam Long id, HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(eduExamService.unpublishExam(id));
    }

    @GetMapping("/student/list")
    public BaseResponse<List<Map<String, Object>>> listPublishedExamsForStudent(HttpServletRequest request) {
        assertStudent(request);
        return ResultUtils.success(eduExamService.listPublishedExamsForStudent());
    }

    @GetMapping("/student/detail")
    public BaseResponse<Map<String, Object>> getStudentExamDetail(@RequestParam Long id, HttpServletRequest request) {
        assertStudent(request);
        return ResultUtils.success(eduExamService.getStudentExamDetail(id));
    }

    @GetMapping("/grading/list")
    public BaseResponse<List<Map<String, Object>>> listGradingExams(HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(eduExamService.listGradingExams());
    }

    @GetMapping("/admin/get")
    public BaseResponse<Map<String, Object>> getAdminExamCard(@RequestParam Long id, HttpServletRequest request) {
        assertTeacher(request);
        return ResultUtils.success(eduExamService.getAdminExamCard(id));
    }

    @PostMapping("/add")
    public BaseResponse<Boolean> addEduExam(@RequestBody EduExamAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExam entity = new EduExam();
        BeanUtils.copyProperties(addRequest, entity);
        eduExamService.validEduExam(entity, true);
        boolean result = eduExamService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEduExam(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduExamService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateEduExam(@RequestBody EduExamUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExam entity = new EduExam();
        BeanUtils.copyProperties(updateRequest, entity);
        eduExamService.validEduExam(entity, false);
        boolean result = eduExamService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<EduExamVO> getEduExamVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExam entity = eduExamService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduExamService.getEduExamVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<EduExam>> listEduExamByPage(@RequestBody EduExamQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<EduExam> page = eduExamService.page(new Page<>(current, size), eduExamService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EduExamVO>> listEduExamVOByPage(@RequestBody EduExamQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<EduExam> page = eduExamService.page(new Page<>(current, size), eduExamService.getQueryWrapper(queryRequest));
        return ResultUtils.success(eduExamService.getEduExamVOPage(page, null));
    }

    private LoginPrincipal assertTeacher(HttpServletRequest request) {
        LoginPrincipal principal = authLoginService.getLoginPrincipal(request);
        if (!UserConstant.ROLE_TYPE_TEACHER.equals(principal.getRoleType())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅教师可访问考试管理接口");
        }
        return principal;
    }

    private LoginPrincipal assertStudent(HttpServletRequest request) {
        LoginPrincipal principal = authLoginService.getLoginPrincipal(request);
        if (!UserConstant.ROLE_TYPE_STUDENT.equals(principal.getRoleType())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅学生可访问考试接口");
        }
        return principal;
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

    private String toText(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Boolean toBooleanObject(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private Date toDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date date) {
            return date;
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            if (text.contains("T")) {
                return Timestamp.valueOf(text.replace("T", " "));
            }
            if (text.length() == 16) {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(text);
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(text);
        } catch (ParseException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "时间格式错误: " + text);
        }
    }
}
