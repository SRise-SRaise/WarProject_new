package com.springboot.controller.exam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.exam.EduExamAddRequest;
import com.springboot.model.dto.exam.EduExamQueryRequest;
import com.springboot.model.dto.exam.EduExamUpdateRequest;
import com.springboot.model.entity.exam.EduExam;
import com.springboot.model.vo.exam.EduExamVO;
import com.springboot.service.exam.EduExamService;
import jakarta.annotation.Resource;
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
}
