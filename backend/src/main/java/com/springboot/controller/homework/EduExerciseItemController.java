package com.springboot.controller.homework;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.homework.EduExerciseItemAddRequest;
import com.springboot.model.dto.homework.EduExerciseItemQueryRequest;
import com.springboot.model.dto.homework.EduExerciseItemUpdateRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.vo.homework.EduExerciseItemVO;
import com.springboot.service.homework.EduExerciseItemService;
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
@RequestMapping("/homework/eduExerciseItem")
public class EduExerciseItemController {

    @Resource
    private EduExerciseItemService eduExerciseItemService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addEduExerciseItem(@RequestBody EduExerciseItemAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (addRequest.getExerciseId() != null) {
            Long itemId = addRequest.getQuestionBankId();
            if (itemId == null) {
                EduExerciseItem newItem = new EduExerciseItem();
                BeanUtils.copyProperties(addRequest, newItem);
                eduExerciseItemService.validEduExerciseItem(newItem, true);
                boolean saveResult = eduExerciseItemService.save(newItem);
                ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);
                itemId = newItem.getId();
            }
            boolean result = eduExerciseItemService.addExerciseItemToExercise(addRequest.getExerciseId(), itemId, addRequest.getMaxScore());
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return ResultUtils.success(true);
        }

        EduExerciseItem item = new EduExerciseItem();
        BeanUtils.copyProperties(addRequest, item);
        eduExerciseItemService.validEduExerciseItem(item, true);
        boolean result = eduExerciseItemService.save(item);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEduExerciseItem(@RequestParam String id,
                                                       @RequestParam(required = false) Long exerciseId) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long itemId;
        try {
            itemId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目ID格式错误");
        }
        boolean result;
        if (exerciseId != null) {
            result = eduExerciseItemService.removeExerciseItemFromExercise(exerciseId, itemId);
        } else {
            result = eduExerciseItemService.removeExerciseItemById(itemId);
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateEduExerciseItem(@RequestBody EduExerciseItemUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExerciseItem entity = new EduExerciseItem();
        BeanUtils.copyProperties(updateRequest, entity);
        eduExerciseItemService.validEduExerciseItem(entity, false);
        boolean result = eduExerciseItemService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<EduExerciseItemVO> getEduExerciseItemVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long itemId;
        try {
            itemId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目ID格式错误");
        }
        EduExerciseItem entity = eduExerciseItemService.getById(itemId);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduExerciseItemService.getEduExerciseItemVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<EduExerciseItem>> listEduExerciseItemByPage(@RequestBody EduExerciseItemQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<EduExerciseItem> page = eduExerciseItemService.page(new Page<>(current, size), eduExerciseItemService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EduExerciseItemVO>> listEduExerciseItemVOByPage(@RequestBody EduExerciseItemQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        if (queryRequest.getExerciseId() != null) {
            return ResultUtils.success(eduExerciseItemService.listExerciseItemVOByExerciseId(queryRequest.getExerciseId(), current, size));
        }
        Page<EduExerciseItem> page = eduExerciseItemService.page(new Page<>(current, size), eduExerciseItemService.getQueryWrapper(queryRequest));
        return ResultUtils.success(eduExerciseItemService.getEduExerciseItemVOPage(page, null));
    }
}
