package com.springboot.controller.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.common.EduLectureAddRequest;
import com.springboot.model.dto.common.EduLectureQueryRequest;
import com.springboot.model.dto.common.EduLectureUpdateRequest;
import com.springboot.model.entity.common.EduLecture;
import com.springboot.model.vo.common.EduLectureVO;
import com.springboot.service.common.EduLectureService;
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
@RequestMapping("/common/eduLecture")
public class EduLectureController {

    @Resource
    private EduLectureService eduLectureService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addEduLecture(@RequestBody EduLectureAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduLecture entity = new EduLecture();
        BeanUtils.copyProperties(addRequest, entity);
        eduLectureService.validEduLecture(entity, true);
        boolean result = eduLectureService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEduLecture(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduLectureService.removeByIdWithFile(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateEduLecture(@RequestBody EduLectureUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduLecture entity = new EduLecture();
        BeanUtils.copyProperties(updateRequest, entity);
        eduLectureService.validEduLecture(entity, false);
        boolean result = eduLectureService.updateByIdWithFileCleanup(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<EduLectureVO> getEduLectureVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduLecture entity = eduLectureService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduLectureService.getEduLectureVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<EduLecture>> listEduLectureByPage(@RequestBody EduLectureQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<EduLecture> page = eduLectureService.page(new Page<>(current, size), eduLectureService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EduLectureVO>> listEduLectureVOByPage(@RequestBody EduLectureQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<EduLecture> page = eduLectureService.page(new Page<>(current, size), eduLectureService.getQueryWrapper(queryRequest));
        return ResultUtils.success(eduLectureService.getEduLectureVOPage(page, null));
    }
}
