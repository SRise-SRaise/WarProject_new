package com.springboot.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.constant.UserConstant;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.user.AuthStudentAddRequest;
import com.springboot.model.dto.user.AuthStudentQueryRequest;
import com.springboot.model.dto.user.AuthStudentUpdateRequest;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.user.AuthStudentVO;
import com.springboot.model.vo.user.LoginPrincipal;
import com.springboot.service.user.AuthClassService;
import com.springboot.service.user.AuthLoginService;
import com.springboot.service.user.AuthStudentService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/authStudent")
public class AuthStudentController {

    @Resource
    private AuthStudentService authStudentService;

    @Resource
    private AuthLoginService authLoginService;

    @Resource
    private AuthClassService authClassService;

    /**
     * 添加学生
     * @param addRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addAuthStudent(@RequestBody AuthStudentAddRequest addRequest, HttpServletRequest request) {
        LoginPrincipal loginPrincipal = authLoginService.getLoginPrincipal(request);
        boolean canOperate = UserConstant.ADMIN_ROLE.equals(loginPrincipal.getRoleCode())
                || UserConstant.ROLE_TYPE_TEACHER.equals(loginPrincipal.getRoleType());
        if (!canOperate) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员或教师可创建学生");
        }
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthStudent entity = new AuthStudent();
        BeanUtils.copyProperties(addRequest, entity);
        authStudentService.validAuthStudent(entity, true);
        boolean result = authStudentService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 删除学生
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAuthStudent(@RequestParam String id, HttpServletRequest request) {
        LoginPrincipal loginPrincipal = authLoginService.getLoginPrincipal(request);
        boolean canOperate = UserConstant.ADMIN_ROLE.equals(loginPrincipal.getRoleCode())
                || UserConstant.ROLE_TYPE_TEACHER.equals(loginPrincipal.getRoleType());
        if (!canOperate) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员或教师可删除学生");
        }
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = authStudentService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新学生（仅管理员或教师可操作，与新增、删除一致）
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateAuthStudent(@RequestBody AuthStudentUpdateRequest updateRequest,
            HttpServletRequest request) {
        LoginPrincipal loginPrincipal = authLoginService.getLoginPrincipal(request);
        boolean canOperate = UserConstant.ADMIN_ROLE.equals(loginPrincipal.getRoleCode())
                || UserConstant.ROLE_TYPE_TEACHER.equals(loginPrincipal.getRoleType());
        if (!canOperate) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员或教师可更新学生");
        }
        if (updateRequest == null || updateRequest.getId() == null || updateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生主键 id 无效");
        }
        AuthStudent existing = authStudentService.getById(updateRequest.getId());
        ThrowUtils.throwIf(existing == null, ErrorCode.NOT_FOUND_ERROR, "学生不存在");
        // 以数据库记录为基准做部分更新，避免 JSON 里空字符串把字段误改成空或破坏外键
        AuthStudent entity = new AuthStudent();
        BeanUtils.copyProperties(existing, entity);
        if (updateRequest.getStudentCode() != null) {
            String studentCode = StringUtils.trimToEmpty(updateRequest.getStudentCode());
            if (StringUtils.isBlank(studentCode)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "学号不能为空");
            }
            if (!studentCode.equals(existing.getStudentCode())) {
                long dup = authStudentService.lambdaQuery()
                        .eq(AuthStudent::getStudentCode, studentCode)
                        .ne(AuthStudent::getId, existing.getId())
                        .count();
                if (dup > 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "学号已被占用");
                }
            }
            entity.setStudentCode(studentCode);
        }
        if (updateRequest.getStudentName() != null) {
            String studentName = StringUtils.trimToEmpty(updateRequest.getStudentName());
            if (StringUtils.isBlank(studentName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生姓名不能为空");
            }
            entity.setStudentName(studentName);
        }
        if (updateRequest.getPasswordMd5() != null) {
            String pwd = StringUtils.trimToNull(updateRequest.getPasswordMd5());
            if (pwd != null) {
                entity.setPasswordMd5(pwd);
            }
        }
        if (updateRequest.getClassCode() != null) {
            String classCode = StringUtils.trimToEmpty(updateRequest.getClassCode());
            if (StringUtils.isBlank(classCode)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "班级编号不能为空");
            }
            if (authClassService.getById(classCode) == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "班级编号不存在");
            }
            entity.setClassCode(classCode);
        }
        if (updateRequest.getRemark() != null) {
            entity.setRemark(updateRequest.getRemark());
        }
        if (updateRequest.getAccountStatus() != null) {
            entity.setAccountStatus(updateRequest.getAccountStatus());
        }
        if (updateRequest.getLoginFailCount() != null) {
            entity.setLoginFailCount(updateRequest.getLoginFailCount());
        }
        if (updateRequest.getLastLoginIp() != null) {
            entity.setLastLoginIp(StringUtils.trimToNull(updateRequest.getLastLoginIp()));
        }
        authStudentService.validAuthStudent(entity, false);
        boolean result = authStudentService.updateById(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取学生详情（封装 VO）
     * 权限：管理员、教师可查任意学生；学生仅可查本人（id 与当前登录 userId 一致）。
     */
    @GetMapping("/get/vo")
    public BaseResponse<AuthStudentVO> getAuthStudentVOById(@RequestParam String id, HttpServletRequest request) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生 id 不能为空");
        }
        long studentId;
        try {
            studentId = Long.parseLong(id.trim());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生 id 格式错误");
        }
        if (studentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生 id 无效");
        }
        LoginPrincipal loginPrincipal = authLoginService.getLoginPrincipal(request);
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginPrincipal.getRoleCode());
        boolean isTeacher = UserConstant.ROLE_TYPE_TEACHER.equals(loginPrincipal.getRoleType());
        boolean isSelfStudent = UserConstant.ROLE_TYPE_STUDENT.equals(loginPrincipal.getRoleType())
                && loginPrincipal.getUserId() != null
                && loginPrincipal.getUserId() == studentId;
        if (!isAdmin && !isTeacher && !isSelfStudent) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看该学生信息");
        }
        AuthStudent entity = authStudentService.getById(studentId);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR, "学生不存在");
        return ResultUtils.success(authStudentService.getAuthStudentVO(entity, request));
    }

    /**
     * 分页查询学生（实体）。
     * 条件（均为可选，未传或空字符串不参与筛选）：学号精确、姓名模糊、班级精确、账号状态精确，以及 id、登录失败次数、IP、创建/更新时间等，见 {@link AuthStudentQueryRequest}。
     * 权限：仅管理员或教师。
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<AuthStudent>> listAuthStudentByPage(@RequestBody AuthStudentQueryRequest queryRequest,
            HttpServletRequest request) {
        LoginPrincipal loginPrincipal = authLoginService.getLoginPrincipal(request);
        boolean canOperate = UserConstant.ADMIN_ROLE.equals(loginPrincipal.getRoleCode())
                || UserConstant.ROLE_TYPE_TEACHER.equals(loginPrincipal.getRoleType());
        if (!canOperate) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员或教师可查询学生列表");
        }
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ThrowUtils.throwIf(queryRequest.getCurrent() < 1, ErrorCode.PARAMS_ERROR, "页码必须大于等于 1");
        ThrowUtils.throwIf(queryRequest.getPageSize() < 1, ErrorCode.PARAMS_ERROR, "每页条数必须大于等于 1");
        ThrowUtils.throwIf(queryRequest.getPageSize() > 50, ErrorCode.PARAMS_ERROR, "每页条数不能超过 50");
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<AuthStudent> page = authStudentService.page(new Page<>(current, size), authStudentService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    /**
     * 分页获取学生列表（封装类）
     * 筛选条件同 {@link #listAuthStudentByPage}。
     * 权限：仅管理员或教师。
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AuthStudentVO>> listAuthStudentVOByPage(@RequestBody AuthStudentQueryRequest queryRequest,
            HttpServletRequest request) {
        LoginPrincipal loginPrincipal = authLoginService.getLoginPrincipal(request);
        boolean canOperate = UserConstant.ADMIN_ROLE.equals(loginPrincipal.getRoleCode())
                || UserConstant.ROLE_TYPE_TEACHER.equals(loginPrincipal.getRoleType());
        if (!canOperate) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员或教师可查询学生列表");
        }
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ThrowUtils.throwIf(queryRequest.getCurrent() < 1, ErrorCode.PARAMS_ERROR, "页码必须大于等于 1");
        ThrowUtils.throwIf(queryRequest.getPageSize() < 1, ErrorCode.PARAMS_ERROR, "每页条数必须大于等于 1");
        ThrowUtils.throwIf(queryRequest.getPageSize() > 50, ErrorCode.PARAMS_ERROR, "每页条数不能超过 50");
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<AuthStudent> page = authStudentService.page(new Page<>(current, size), authStudentService.getQueryWrapper(queryRequest));
        return ResultUtils.success(authStudentService.getAuthStudentVOPage(page, request));
    }
}
