package com.springboot.controller.exam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.exam.ResExamRecordAddRequest;
import com.springboot.model.dto.exam.ResExamRecordQueryRequest;
import com.springboot.model.dto.exam.ResExamRecordUpdateRequest;
import com.springboot.model.entity.exam.ResExamRecord;
import com.springboot.model.vo.exam.ResExamRecordVO;
import com.springboot.service.exam.ResExamRecordService;
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
@RequestMapping("/exam/resExamRecord")
public class ResExamRecordController {

    @Resource
    private ResExamRecordService resExamRecordService;

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
}
