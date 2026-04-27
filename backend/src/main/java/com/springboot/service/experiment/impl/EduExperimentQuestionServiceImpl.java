package com.springboot.service.experiment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.experiment.EduExperimentQuestionMapper;
import com.springboot.model.dto.experiment.DocxImportRequest;
import com.springboot.model.dto.experiment.EduExperimentQuestionQueryRequest;
import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.entity.experiment.EduExperimentItem;
import com.springboot.model.entity.experiment.EduExperimentQuestion;
import com.springboot.model.vo.experiment.DocxImportResult;
import com.springboot.utils.DocxParserUtil;
import com.springboot.model.enums.experiment.ExperimentDifficultyEnum;
import com.springboot.model.enums.experiment.ExperimentQuestionTypeEnum;
import com.springboot.model.vo.experiment.EduExperimentQuestionVO;
import com.springboot.service.experiment.EduExperimentItemService;
import com.springboot.service.experiment.EduExperimentQuestionService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EduExperimentQuestionServiceImpl extends ServiceImpl<EduExperimentQuestionMapper, EduExperimentQuestion>
        implements EduExperimentQuestionService {

    @Resource
    private EduExperimentItemService eduExperimentItemService;

    @Override
    public void validEduExperimentQuestion(EduExperimentQuestion eduExperimentQuestion, boolean add) {
        if (eduExperimentQuestion == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.debug("[EduExperimentQuestion] 参数校验: add={}", add);
        String questionName = eduExperimentQuestion.getQuestionName();
        String questionContent = eduExperimentQuestion.getQuestionContent();
        Integer questionType = eduExperimentQuestion.getQuestionType();
        Integer difficulty = eduExperimentQuestion.getDifficulty();

        // 新增时校验必填项
        if (add) {
            if (StringUtils.isBlank(questionName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目名称不能为空");
            }
            if (StringUtils.isBlank(questionContent)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目内容不能为空");
            }
            if (questionType == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目类型不能为空");
            }
        }

        // 校验题目类型
        if (questionType != null) {
            ExperimentQuestionTypeEnum typeEnum = ExperimentQuestionTypeEnum.getEnumByCode(questionType);
            if (typeEnum == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目类型不合法");
            }
        }

        // 校验难度等级
        if (difficulty != null) {
            ExperimentDifficultyEnum difficultyEnum = ExperimentDifficultyEnum.getEnumByCode(difficulty);
            if (difficultyEnum == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "难度等级不合法");
            }
        }

        // 校验题目名称长度
        if (StringUtils.isNotBlank(questionName) && questionName.length() > 200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目名称过长");
        }
    }

    @Override
    public QueryWrapper<EduExperimentQuestion> getQueryWrapper(EduExperimentQuestionQueryRequest queryRequest) {
        QueryWrapper<EduExperimentQuestion> queryWrapper = ServiceMethodSupport.buildQueryWrapper(queryRequest);

        if (queryRequest == null) {
            return queryWrapper;
        }

        Long id = queryRequest.getId();
        String questionName = queryRequest.getQuestionName();
        String questionContent = queryRequest.getQuestionContent();
        Integer questionType = queryRequest.getQuestionType();
        Integer difficulty = queryRequest.getDifficulty();
        String tag = queryRequest.getTag();
        Integer status = queryRequest.getStatus();

        // 按ID查询
        queryWrapper.eq(id != null, "id", id);

        // 按题目名称模糊查询
        queryWrapper.like(StringUtils.isNotBlank(questionName), "question_name", questionName);

        // 按题目内容模糊查询
        queryWrapper.like(StringUtils.isNotBlank(questionContent), "question_content", questionContent);

        // 按题目类型精确查询
        queryWrapper.eq(questionType != null, "question_type", questionType);

        // 按难度等级精确查询
        queryWrapper.eq(difficulty != null, "difficulty", difficulty);

        // 按标签精确查询
        queryWrapper.eq(StringUtils.isNotBlank(tag), "tag", tag);

        // 按状态精确查询
        queryWrapper.eq(status != null, "status", status);

        // 默认只查询启用状态的题目
        if (status == null) {
            queryWrapper.eq("status", 1);
        }

        log.debug("[EduExperimentQuestion] 构建查询Wrapper: id={}, questionType={}, difficulty={}",
                id, questionType, difficulty);
        return queryWrapper;
    }

    @Override
    public EduExperimentQuestionVO getEduExperimentQuestionVO(EduExperimentQuestion eduExperimentQuestion, HttpServletRequest request) {
        log.debug("[EduExperimentQuestion] 转换为VO: id={}", eduExperimentQuestion != null ? eduExperimentQuestion.getId() : "null");
        return EduExperimentQuestionVO.objToVo(eduExperimentQuestion);
    }

    @Override
    public Page<EduExperimentQuestionVO> getEduExperimentQuestionVOPage(Page<EduExperimentQuestion> entityPage, HttpServletRequest request) {
        log.debug("[EduExperimentQuestion] 分页转换为VO: current={}, size={}", entityPage.getCurrent(), entityPage.getSize());
        return ServiceMethodSupport.toVOPage(entityPage, EduExperimentQuestionVO::objToVo);
    }

    @Override
    public List<EduExperimentQuestion> selectRandomQuestions(Integer questionType, Integer difficulty, String tag, int count) {
        log.info("[EduExperimentQuestion] 随机选题: questionType={}, difficulty={}, tag={}, count={}",
                questionType, difficulty, tag, count);
        if (count <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "选取数量必须大于0");
        }

        QueryWrapper<EduExperimentQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只选取启用状态的题目

        // 按题目类型筛选
        if (questionType != null) {
            queryWrapper.eq("question_type", questionType);
        }

        // 按难度等级筛选
        if (difficulty != null) {
            queryWrapper.eq("difficulty", difficulty);
        }

        // 按标签筛选
        if (StringUtils.isNotBlank(tag)) {
            queryWrapper.eq("tag", tag);
        }

        // 查询所有符合条件的题目
        List<EduExperimentQuestion> allQuestions = this.list(queryWrapper);

        if (allQuestions == null || allQuestions.isEmpty()) {
            return Collections.emptyList();
        }

        // 如果题目数量少于要求的数量，返回所有符合条件的题目
        if (allQuestions.size() <= count) {
            return allQuestions;
        }

        // 随机选取指定数量的题目
        Collections.shuffle(allQuestions);
        return allQuestions.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public boolean batchImportQuestions(List<EduExperimentQuestion> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "导入题目列表为空");
        }
        log.info("[EduExperimentQuestion] 批量导入题目: 数量={}", questionList.size());

        // 逐个校验并保存
        for (EduExperimentQuestion question : questionList) {
            validEduExperimentQuestion(question, true);
            // 设置默认状态为启用
            if (question.getStatus() == null) {
                question.setStatus(1);
            }
        }

        return this.saveBatch(questionList);
    }

    @Override
    public DocxImportResult importExperimentFromDocx(DocxImportRequest importRequest, EduExperiment experiment) {
        return importExperimentFromDocx(importRequest, experiment, null);
    }

    @Override
    public DocxImportResult importExperimentFromDocx(DocxImportRequest importRequest, EduExperiment experiment, org.springframework.web.multipart.MultipartFile file) {
        log.info("[EduExperimentQuestion] 开始从DOCX导入实验");
        if (importRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "导入请求不能为空");
        }

        // 解析docx文档：优先使用上传的文件，其次使用fileUrl
        DocxParserUtil.ParseResult parseResult;
        if (file != null && !file.isEmpty()) {
            log.info("[EduExperimentQuestion] 从上传的MultipartFile解析");
            parseResult = DocxParserUtil.parseFromMultipartFile(file);
        } else if (importRequest.getFileUrl() != null && !importRequest.getFileUrl().trim().isEmpty()) {
            log.info("[EduExperimentQuestion] 从fileUrl解析: {}", importRequest.getFileUrl());
            parseResult = DocxParserUtil.parseFromUrl(importRequest.getFileUrl());
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请上传docx文件或提供文件URL");
        }

        // 使用文档中的实验名称（如果请求中指定了实验名称，则使用请求中的）
        String experimentName = importRequest.getExperimentName();
        if (StringUtils.isBlank(experimentName)) {
            experimentName = parseResult.getExperimentName();
        }
        if (StringUtils.isBlank(experimentName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "实验名称不能为空，请在请求中指定或文档中包含【实验名称】");
        }

        // 设置实验信息
        if (experiment == null) {
            experiment = new EduExperiment();
        }
        experiment.setName(experimentName);
        experiment.setRequirement(importRequest.getRequirement() != null ? importRequest.getRequirement() : parseResult.getRequirement());
        if (importRequest.getCategoryId() != null) {
            experiment.setCategoryId(importRequest.getCategoryId());
        } else if (parseResult.getCategoryId() != null) {
            experiment.setCategoryId(parseResult.getCategoryId());
        }
        experiment.setPublishStatus(importRequest.getAutoPublish() != null && importRequest.getAutoPublish() ? 1 : 0);

        // 获取题目列表
        List<DocxParserUtil.QuestionInfo> questionInfoList = parseResult.getQuestions();
        if (questionInfoList == null || questionInfoList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文档中未找到有效题目");
        }

        // 转换题目信息为 EduExperimentItem 实体（使用现有的 t_experiment_item 表）
        Long experimentId = experiment.getId();
        List<EduExperimentItem> items = new java.util.ArrayList<>();
        int sortOrder = 1;
        for (DocxParserUtil.QuestionInfo info : questionInfoList) {
            EduExperimentItem item = new EduExperimentItem();
            item.setExperimentId(experimentId);
            item.setSortOrder(sortOrder++);
            item.setItemName(info.getQuestionName() != null ? info.getQuestionName() : "题目" + (sortOrder - 1));
            item.setQuestionContent(info.getQuestionContent());
            item.setQuestionType(info.getQuestionType() != null ? info.getQuestionType() : 1);
            item.setStandardAnswer(info.getStandardAnswer());
            item.setMaxScore(info.getScore() != null ? info.getScore() : importRequest.getDefaultScore() != null ? importRequest.getDefaultScore() : 10);
            item.setItemStatus(1);
            items.add(item);
        }

        // 批量保存题目到 t_experiment_item 表
        int successCount = 0;
        int failCount = 0;
        Map<Integer, String> failDetails = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            EduExperimentItem item = items.get(i);
            try {
                // 校验必填项
                if (StringUtils.isBlank(item.getQuestionContent())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目内容不能为空");
                }
                eduExperimentItemService.save(item);
                successCount++;
            } catch (Exception e) {
                failCount++;
                failDetails.put(i + 1, e.getMessage());
            }
        }

        // 构建返回结果
        List<DocxImportResult.QuestionPreview> previews = items.stream().limit(20).map(item -> {
            String typeName = DocxParserUtil.getQuestionTypeName(item.getQuestionType());
            String summary = item.getQuestionContent();
            if (summary != null && summary.length() > 50) {
                summary = summary.substring(0, 50) + "...";
            }
            return DocxImportResult.QuestionPreview.builder()
                    .questionName(item.getItemName())
                    .questionTypeName(typeName)
                    .contentSummary(summary)
                    .build();
        }).collect(Collectors.toList());

        // 构建导入结果
        DocxImportResult result = DocxImportResult.builder()
                .success(failCount == 0)
                .experimentName(experimentName)
                .successCount(successCount)
                .failCount(failCount)
                .failDetails(failDetails.isEmpty() ? null : failDetails)
                .questionPreviews(previews)
                .build();

        if (failCount == 0) {
            result.setMessage("导入成功，共导入" + successCount + "道题目");
        } else {
            result.setMessage("部分导入成功，成功" + successCount + "道，失败" + failCount + "道");
        }

        log.info("[EduExperimentQuestion] DOCX导入完成: 实验名称={}, 成功={}, 失败={}",
                experimentName, successCount, failCount);
        return result;
    }
}
