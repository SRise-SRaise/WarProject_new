package com.springboot.controller.exam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.exam.EduQuestionBankAddRequest;
import com.springboot.model.dto.exam.EduQuestionBankQueryRequest;
import com.springboot.model.dto.exam.EduQuestionBankUpdateRequest;
import com.springboot.model.entity.exam.EduQuestionBank;
import com.springboot.model.vo.exam.EduQuestionBankVO;
import com.springboot.service.exam.EduQuestionBankService;
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
@RequestMapping("/exam/eduQuestionBank")
public class EduQuestionBankController {

    @Resource
    private EduQuestionBankService eduQuestionBankService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addEduQuestionBank(@RequestBody EduQuestionBankAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduQuestionBank entity = new EduQuestionBank();
        BeanUtils.copyProperties(addRequest, entity);
        eduQuestionBankService.validEduQuestionBank(entity, true);
        boolean result = eduQuestionBankService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEduQuestionBank(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduQuestionBankService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateEduQuestionBank(@RequestBody EduQuestionBankUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduQuestionBank entity = new EduQuestionBank();
        BeanUtils.copyProperties(updateRequest, entity);
        eduQuestionBankService.validEduQuestionBank(entity, false);
        boolean result = eduQuestionBankService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<EduQuestionBankVO> getEduQuestionBankVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduQuestionBank entity = eduQuestionBankService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduQuestionBankService.getEduQuestionBankVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<EduQuestionBank>> listEduQuestionBankByPage(@RequestBody EduQuestionBankQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<EduQuestionBank> page = eduQuestionBankService.page(new Page<>(current, size), eduQuestionBankService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EduQuestionBankVO>> listEduQuestionBankVOByPage(@RequestBody EduQuestionBankQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<EduQuestionBank> page = eduQuestionBankService.page(new Page<>(current, size), eduQuestionBankService.getQueryWrapper(queryRequest));
        return ResultUtils.success(eduQuestionBankService.getEduQuestionBankVOPage(page, null));
    }
}
