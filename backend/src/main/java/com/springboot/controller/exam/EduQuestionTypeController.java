package com.springboot.controller.exam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.exam.EduQuestionTypeAddRequest;
import com.springboot.model.dto.exam.EduQuestionTypeQueryRequest;
import com.springboot.model.dto.exam.EduQuestionTypeUpdateRequest;
import com.springboot.model.entity.exam.EduQuestionType;
import com.springboot.model.vo.exam.EduQuestionTypeVO;
import com.springboot.service.exam.EduQuestionTypeService;
import jakarta.annotation.Resource;
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
@RequestMapping("/exam/eduQuestionType")
public class EduQuestionTypeController {

    @Resource
    private EduQuestionTypeService eduQuestionTypeService;

    @GetMapping("/list/all")
    public BaseResponse<List<Map<String, Object>>> listAllEduQuestionTypes() {
        return ResultUtils.success(eduQuestionTypeService.listAllTypes());
    }

    @PostMapping("/add")
    public BaseResponse<Boolean> addEduQuestionType(@RequestBody EduQuestionTypeAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduQuestionType entity = new EduQuestionType();
        BeanUtils.copyProperties(addRequest, entity);
        eduQuestionTypeService.validEduQuestionType(entity, true);
        boolean result = eduQuestionTypeService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEduQuestionType(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduQuestionTypeService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateEduQuestionType(@RequestBody EduQuestionTypeUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduQuestionType entity = new EduQuestionType();
        BeanUtils.copyProperties(updateRequest, entity);
        eduQuestionTypeService.validEduQuestionType(entity, false);
        boolean result = eduQuestionTypeService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<EduQuestionTypeVO> getEduQuestionTypeVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduQuestionType entity = eduQuestionTypeService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduQuestionTypeService.getEduQuestionTypeVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<EduQuestionType>> listEduQuestionTypeByPage(@RequestBody EduQuestionTypeQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<EduQuestionType> page = eduQuestionTypeService.page(new Page<>(current, size), eduQuestionTypeService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EduQuestionTypeVO>> listEduQuestionTypeVOByPage(@RequestBody EduQuestionTypeQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<EduQuestionType> page = eduQuestionTypeService.page(new Page<>(current, size), eduQuestionTypeService.getQueryWrapper(queryRequest));
        return ResultUtils.success(eduQuestionTypeService.getEduQuestionTypeVOPage(page, null));
    }
}
