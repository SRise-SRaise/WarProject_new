package com.springboot.controller.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.experiment.EduExperimentQuestionAddRequest;
import com.springboot.model.dto.experiment.EduExperimentQuestionQueryRequest;
import com.springboot.model.dto.experiment.EduExperimentQuestionUpdateRequest;
import com.springboot.model.entity.experiment.EduExperimentQuestion;
import com.springboot.model.vo.experiment.EduExperimentQuestionVO;
import com.springboot.service.experiment.EduExperimentQuestionService;
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

import java.util.List;

/**
 * 实验题库控制器
 */
@Slf4j
@RestController
@RequestMapping("/experiment/eduExperimentQuestion")
public class EduExperimentQuestionController {

    @Resource
    private EduExperimentQuestionService eduExperimentQuestionService;

    /**
     * 添加题目
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody EduExperimentQuestionAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperimentQuestion entity = new EduExperimentQuestion();
        BeanUtils.copyProperties(addRequest, entity);
        eduExperimentQuestionService.validEduExperimentQuestion(entity, true);
        boolean result = eduExperimentQuestionService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(entity.getId());
    }

    /**
     * 删除题目
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduExperimentQuestionService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新题目
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateQuestion(@RequestBody EduExperimentQuestionUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperimentQuestion entity = new EduExperimentQuestion();
        BeanUtils.copyProperties(updateRequest, entity);
        eduExperimentQuestionService.validEduExperimentQuestion(entity, false);
        boolean result = eduExperimentQuestionService.updateById(entity);
        return ResultUtils.success(result);
    }

    /**
     * 获取题目详情
     */
    @GetMapping("/get/vo")
    public BaseResponse<EduExperimentQuestionVO> getQuestionVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperimentQuestion entity = eduExperimentQuestionService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduExperimentQuestionService.getEduExperimentQuestionVO(entity, null));
    }

    /**
     * 分页查询题目
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<EduExperimentQuestion>> listQuestionByPage(@RequestBody EduExperimentQuestionQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<EduExperimentQuestion> page = eduExperimentQuestionService.page(
                new Page<>(current, size),
                eduExperimentQuestionService.getQueryWrapper(queryRequest)
        );
        return ResultUtils.success(page);
    }

    /**
     * 分页查询题目（返回VO）
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EduExperimentQuestionVO>> listQuestionVOByPage(@RequestBody EduExperimentQuestionQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<EduExperimentQuestion> page = eduExperimentQuestionService.page(
                new Page<>(current, size),
                eduExperimentQuestionService.getQueryWrapper(queryRequest)
        );
        return ResultUtils.success(eduExperimentQuestionService.getEduExperimentQuestionVOPage(page, null));
    }

    /**
     * 随机选题
     */
    @GetMapping("/select/random")
    public BaseResponse<List<EduExperimentQuestion>> selectRandomQuestions(
            @RequestParam(required = false) Integer questionType,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "10") Integer count) {
        log.info("[EduExperimentQuestion] 随机选题: questionType={}, difficulty={}, tag={}, count={}",
                questionType, difficulty, tag, count);
        List<EduExperimentQuestion> questions = eduExperimentQuestionService.selectRandomQuestions(
                questionType, difficulty, tag, count);
        return ResultUtils.success(questions);
    }

    /**
     * 批量导入题目
     */
    @PostMapping("/batch/import")
    public BaseResponse<Boolean> batchImportQuestions(@RequestBody List<EduExperimentQuestionAddRequest> questionList) {
        log.info("[EduExperimentQuestion] 批量导入题目: 数量={}", questionList != null ? questionList.size() : 0);
        if (questionList == null || questionList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目列表为空");
        }
        List<EduExperimentQuestion> entities = questionList.stream().map(request -> {
            EduExperimentQuestion entity = new EduExperimentQuestion();
            BeanUtils.copyProperties(request, entity);
            return entity;
        }).toList();
        boolean result = eduExperimentQuestionService.batchImportQuestions(entities);
        log.info("[EduExperimentQuestion] 批量导入题目完成: success={}", result);
        return ResultUtils.success(result);
    }

    /**
     * 获取所有启用的题目（不分页）
     */
    @GetMapping("/list/all")
    public BaseResponse<List<EduExperimentQuestionVO>> listAllEnabledQuestions() {
        log.debug("[EduExperimentQuestion] 获取所有启用题目");
        EduExperimentQuestionQueryRequest queryRequest = new EduExperimentQuestionQueryRequest();
        queryRequest.setStatus(1);
        List<EduExperimentQuestion> questions = eduExperimentQuestionService.list(
                eduExperimentQuestionService.getQueryWrapper(queryRequest)
        );
        return ResultUtils.success(EduExperimentQuestionVO.objToVo(questions));
    }
}
