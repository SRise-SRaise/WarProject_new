package com.springboot.controller.exam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.exam.RelPaperQuestionAddRequest;
import com.springboot.model.dto.exam.RelPaperQuestionQueryRequest;
import com.springboot.model.dto.exam.RelPaperQuestionUpdateRequest;
import com.springboot.model.entity.exam.RelPaperQuestion;
import com.springboot.model.vo.exam.RelPaperQuestionVO;
import com.springboot.service.exam.RelPaperQuestionService;
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
@RequestMapping("/exam/relPaperQuestion")
public class RelPaperQuestionController {

    @Resource
    private RelPaperQuestionService relPaperQuestionService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addRelPaperQuestion(@RequestBody RelPaperQuestionAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        RelPaperQuestion entity = new RelPaperQuestion();
        BeanUtils.copyProperties(addRequest, entity);
        relPaperQuestionService.validRelPaperQuestion(entity, true);
        boolean result = relPaperQuestionService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteRelPaperQuestion(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = relPaperQuestionService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateRelPaperQuestion(@RequestBody RelPaperQuestionUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        RelPaperQuestion entity = new RelPaperQuestion();
        BeanUtils.copyProperties(updateRequest, entity);
        relPaperQuestionService.validRelPaperQuestion(entity, false);
        boolean result = relPaperQuestionService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<RelPaperQuestionVO> getRelPaperQuestionVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        RelPaperQuestion entity = relPaperQuestionService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(relPaperQuestionService.getRelPaperQuestionVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<RelPaperQuestion>> listRelPaperQuestionByPage(@RequestBody RelPaperQuestionQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<RelPaperQuestion> page = relPaperQuestionService.page(new Page<>(current, size), relPaperQuestionService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<RelPaperQuestionVO>> listRelPaperQuestionVOByPage(@RequestBody RelPaperQuestionQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<RelPaperQuestion> page = relPaperQuestionService.page(new Page<>(current, size), relPaperQuestionService.getQueryWrapper(queryRequest));
        return ResultUtils.success(relPaperQuestionService.getRelPaperQuestionVOPage(page, null));
    }
}
