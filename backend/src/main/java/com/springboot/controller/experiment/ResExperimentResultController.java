package com.springboot.controller.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.experiment.ResExperimentResultAddRequest;
import com.springboot.model.dto.experiment.ResExperimentResultQueryRequest;
import com.springboot.model.dto.experiment.ResExperimentResultUpdateRequest;
import com.springboot.model.entity.experiment.ResExperimentResult;
import com.springboot.model.vo.experiment.ResExperimentResultVO;
import com.springboot.service.experiment.ResExperimentResultService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/experiment/resExperimentResult")
public class ResExperimentResultController {

    @Resource
    private ResExperimentResultService resExperimentResultService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addResExperimentResult(@RequestBody ResExperimentResultAddRequest addRequest) {
        log.info("[ResExperimentResult] 新增答题记录");
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResExperimentResult entity = new ResExperimentResult();
        BeanUtils.copyProperties(addRequest, entity);
        resExperimentResultService.validResExperimentResult(entity, true);
        boolean result = resExperimentResultService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        log.info("[ResExperimentResult] 新增答题记录成功: id={}", entity.getId());
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteResExperimentResult(@RequestParam String id) {
        log.info("[ResExperimentResult] 删除答题记录: id={}", id);
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = resExperimentResultService.removeById(id);
        log.info("[ResExperimentResult] 删除答题记录完成: id={}, success={}", id, result);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateResExperimentResult(@RequestBody ResExperimentResultUpdateRequest updateRequest) {
        log.info("[ResExperimentResult] 更新答题记录");
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResExperimentResult entity = new ResExperimentResult();
        BeanUtils.copyProperties(updateRequest, entity);
        resExperimentResultService.validResExperimentResult(entity, false);
        boolean result = resExperimentResultService.updateById(entity);
        log.info("[ResExperimentResult] 更新答题记录完成: id={}, success={}", updateRequest.getId(), result);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<ResExperimentResultVO> getResExperimentResultVOById(@RequestParam String id) {
        log.debug("[ResExperimentResult] 查询答题记录详情: id={}", id);
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResExperimentResult entity = resExperimentResultService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(resExperimentResultService.getResExperimentResultVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<ResExperimentResult>> listResExperimentResultByPage(@RequestBody ResExperimentResultQueryRequest queryRequest) {
        log.debug("[ResExperimentResult] 分页查询答题记录: current={}, size={}",
                queryRequest != null ? queryRequest.getCurrent() : "null",
                queryRequest != null ? queryRequest.getPageSize() : "null");
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<ResExperimentResult> page = resExperimentResultService.page(new Page<>(current, size), resExperimentResultService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ResExperimentResultVO>> listResExperimentResultVOByPage(@RequestBody ResExperimentResultQueryRequest queryRequest) {
        log.debug("[ResExperimentResult] 分页查询答题记录VO: current={}, size={}",
                queryRequest != null ? queryRequest.getCurrent() : "null",
                queryRequest != null ? queryRequest.getPageSize() : "null");
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<ResExperimentResult> page = resExperimentResultService.page(new Page<>(current, size), resExperimentResultService.getQueryWrapper(queryRequest));
        return ResultUtils.success(resExperimentResultService.getResExperimentResultVOPage(page, null));
    }

    /**
     * 保存或更新实验总结
     * 约定：使用 itemId = -experimentId 标识总结记录，避免新增数据库字段
     */
    @PostMapping("/summary/save")
    public BaseResponse<Boolean> saveExperimentSummary(
            @RequestParam Long experimentId,
            @RequestParam Long studentId,
            @RequestParam String summaryContent) {
        log.info("[ResExperimentResult] 保存实验总结: experimentId={}, studentId={}", experimentId, studentId);
        if (experimentId == null || studentId == null || summaryContent == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        // 约定：总结记录的 itemId = -experimentId（负数），便于区分普通答题记录
        long summaryItemId = -experimentId;

        // 检查是否已有总结记录
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ResExperimentResult> qw =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("student_id", studentId).eq("item_id", summaryItemId);
        ResExperimentResult existing = resExperimentResultService.getOne(qw);

        boolean result;
        if (existing != null) {
            existing.setSubContent(summaryContent);
            existing.setFillTime(new java.util.Date());
            result = resExperimentResultService.updateById(existing);
        } else {
            ResExperimentResult summary = new ResExperimentResult();
            summary.setStudentId(studentId);
            summary.setItemId(summaryItemId);
            summary.setSubContent(summaryContent);
            summary.setGradingStatus(0);
            summary.setFillTime(new java.util.Date());
            result = resExperimentResultService.save(summary);
        }
        log.info("[ResExperimentResult] 保存实验总结完成: success={}", result);
        return ResultUtils.success(result);
    }

    /**
     * 获取实验总结
     */
    @GetMapping("/summary/get")
    public BaseResponse<String> getExperimentSummary(
            @RequestParam Long experimentId,
            @RequestParam Long studentId) {
        log.debug("[ResExperimentResult] 获取实验总结: experimentId={}, studentId={}", experimentId, studentId);
        if (experimentId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        long summaryItemId = -experimentId;
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ResExperimentResult> qw =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("student_id", studentId).eq("item_id", summaryItemId);
        ResExperimentResult record = resExperimentResultService.getOne(qw);
        String content = record != null ? record.getSubContent() : "";
        return ResultUtils.success(content);
    }
}
