package com.springboot.controller.homework;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.homework.EduExerciseAddRequest;
import com.springboot.model.dto.homework.EduExerciseQueryRequest;
import com.springboot.model.dto.homework.EduExerciseUpdateRequest;
import com.springboot.model.dto.homework.ExercisePublishRequest;
import com.springboot.model.dto.homework.StudentExerciseQueryRequest;
import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.vo.homework.EduExerciseVO;
import com.springboot.model.vo.homework.StudentExerciseVO;
import com.springboot.service.homework.EduExerciseService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/homework/eduExercise")
public class EduExerciseController {

    @Resource
    private EduExerciseService eduExerciseService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addEduExercise(@RequestBody EduExerciseAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExercise entity = new EduExercise();
        BeanUtils.copyProperties(addRequest, entity);
        eduExerciseService.validEduExercise(entity, true);
        boolean result = eduExerciseService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEduExercise(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduExerciseService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateEduExercise(@RequestBody EduExerciseUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExercise entity = new EduExercise();
        BeanUtils.copyProperties(updateRequest, entity);
        eduExerciseService.validEduExercise(entity, false);
        boolean result = eduExerciseService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<EduExerciseVO> getEduExerciseVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExercise entity = eduExerciseService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduExerciseService.getEduExerciseVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<EduExercise>> listEduExerciseByPage(@RequestBody EduExerciseQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<EduExercise> page = eduExerciseService.page(new Page<>(current, size), eduExerciseService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EduExerciseVO>> listEduExerciseVOByPage(@RequestBody EduExerciseQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<EduExercise> page = eduExerciseService.page(new Page<>(current, size), eduExerciseService.getQueryWrapper(queryRequest));
        return ResultUtils.success(eduExerciseService.getEduExerciseVOPage(page, null));
    }

    /**
     * 发布作业到指定班级
     * POST /homework/eduExercise/publish
     */
    @PostMapping("/publish")
    public BaseResponse<Boolean> publishExercise(@RequestBody ExercisePublishRequest publishRequest) {
        if (publishRequest == null || publishRequest.getExerciseId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduExerciseService.publishExercise(publishRequest);
        return ResultUtils.success(result);
    }

    /**
     * 保存布置草稿（不发布）
     * POST /homework/eduExercise/saveDraftAssign
     */
    @PostMapping("/saveDraftAssign")
    public BaseResponse<Boolean> saveDraftAssign(@RequestBody ExercisePublishRequest publishRequest) {
        if (publishRequest == null || publishRequest.getExerciseId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduExerciseService.saveDraftAssign(publishRequest);
        return ResultUtils.success(result);
    }

    /**
     * 关闭作业（停止学生提交）
     * POST /homework/eduExercise/close
     */
    @PostMapping("/close")
    public BaseResponse<Boolean> closeExercise(@RequestParam Long exerciseId) {
        if (exerciseId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduExerciseService.closeExercise(exerciseId);
        return ResultUtils.success(result);
    }

    /**
     * 学生查询可见作业列表
     * POST /homework/eduExercise/listForStudent
     */
    @PostMapping("/listForStudent")
    public BaseResponse<List<StudentExerciseVO>> listForStudent(@RequestBody StudentExerciseQueryRequest queryRequest) {
        if (queryRequest == null || queryRequest.getStudentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<StudentExerciseVO> list = eduExerciseService.listForStudent(queryRequest);
        return ResultUtils.success(list);
    }
}
