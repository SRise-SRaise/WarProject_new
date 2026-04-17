package com.springboot.controller.exam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.exam.EduPaperAddRequest;
import com.springboot.model.dto.exam.EduPaperQueryRequest;
import com.springboot.model.dto.exam.EduPaperUpdateRequest;
import com.springboot.model.entity.exam.EduPaper;
import com.springboot.model.vo.exam.EduPaperVO;
import com.springboot.service.exam.EduPaperService;
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
@RequestMapping("/exam/eduPaper")
public class EduPaperController {

    @Resource
    private EduPaperService eduPaperService;

    @GetMapping("/detail")
    public BaseResponse<Map<String, Object>> getPaperDetail(@RequestParam Long id) {
        return ResultUtils.success(eduPaperService.getPaperDetail(id));
    }

    @GetMapping("/list/all")
    public BaseResponse<List<Map<String, Object>>> listAllPapers() {
        return ResultUtils.success(eduPaperService.listAllPaperOptions());
    }

    @PostMapping("/list/page/full")
    public BaseResponse<Map<String, Object>> listPaperPageFull(@RequestBody EduPaperQueryRequest queryRequest) {
        return ResultUtils.success(eduPaperService.listPaperPage(queryRequest));
    }

    @PostMapping("/add")
    public BaseResponse<Boolean> addEduPaper(@RequestBody EduPaperAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduPaper entity = new EduPaper();
        BeanUtils.copyProperties(addRequest, entity);
        eduPaperService.validEduPaper(entity, true);
        boolean result = eduPaperService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEduPaper(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduPaperService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateEduPaper(@RequestBody EduPaperUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduPaper entity = new EduPaper();
        BeanUtils.copyProperties(updateRequest, entity);
        eduPaperService.validEduPaper(entity, false);
        boolean result = eduPaperService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<EduPaperVO> getEduPaperVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduPaper entity = eduPaperService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduPaperService.getEduPaperVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<EduPaper>> listEduPaperByPage(@RequestBody EduPaperQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<EduPaper> page = eduPaperService.page(new Page<>(current, size), eduPaperService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EduPaperVO>> listEduPaperVOByPage(@RequestBody EduPaperQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<EduPaper> page = eduPaperService.page(new Page<>(current, size), eduPaperService.getQueryWrapper(queryRequest));
        return ResultUtils.success(eduPaperService.getEduPaperVOPage(page, null));
    }
}
