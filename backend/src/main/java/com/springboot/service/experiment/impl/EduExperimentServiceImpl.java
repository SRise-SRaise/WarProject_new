package com.springboot.service.experiment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.experiment.EduExperimentAttachmentMapper;
import com.springboot.mapper.experiment.EduExperimentMapper;
import com.springboot.model.dto.experiment.EduExperimentQueryRequest;
import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.entity.experiment.EduExperimentAttachment;
import com.springboot.model.vo.experiment.EduExperimentVO;
import com.springboot.service.experiment.EduExperimentService;
import com.springboot.service.experiment.ExperimentClassService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class EduExperimentServiceImpl extends ServiceImpl<EduExperimentMapper, EduExperiment> implements EduExperimentService {

    @Resource
    private ExperimentClassService experimentClassService;

    @Resource
    private EduExperimentAttachmentMapper attachmentMapper;

    @Override
    public void validEduExperiment(EduExperiment eduExperiment, boolean add) {
        log.debug("[EduExperiment] 参数校验: add={}", add);
        ServiceMethodSupport.validEntity(eduExperiment);
    }

    @Override
    public QueryWrapper<EduExperiment> getQueryWrapper(EduExperimentQueryRequest queryRequest) {
        if (queryRequest == null) {
            log.debug("[EduExperiment] 查询请求为null，返回空Wrapper");
            return new QueryWrapper<>();
        }
        queryRequest.setSortField(mapSortField(queryRequest.getSortField()));
        QueryWrapper<EduExperiment> queryWrapper = new QueryWrapper<>();

        // 按班级过滤：只返回该班级绑定的已发布实验
        String classCode = queryRequest.getClassCode();
        if (classCode != null && !classCode.isBlank()) {
            List<Long> allowedIds = experimentClassService.getExperimentIdsByClass(classCode);
            if (allowedIds.isEmpty()) {
                // 该班级没有任何绑定实验，返回空
                queryWrapper.apply("1 = 0");
            } else {
                queryWrapper.in("experiment_id", allowedIds);
            }
        }

        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        if (sortField != null && !sortField.isEmpty()) {
            boolean isAsc = "ascend".equals(sortOrder);
            queryWrapper.orderBy(true, isAsc, sortField);
        }
        log.debug("[EduExperiment] 构建查询Wrapper: sortField={}, sortOrder={}, classCode={}", sortField, sortOrder, classCode);
        return queryWrapper;
    }

    @Override
    public EduExperimentVO getEduExperimentVO(EduExperiment eduExperiment, HttpServletRequest request) {
        if (eduExperiment == null) {
            return null;
        }
        log.debug("[EduExperiment] 转换为VO: id={}", eduExperiment.getId());
        EduExperimentVO vo = EduExperimentVO.objToVo(eduExperiment);
        enrichClassInfo(vo, eduExperiment.getId());
        // 补充指导书URL：查询教师上传的附件（studentId=0）
        enrichInstructionUrl(vo, eduExperiment.getId());
        return vo;
    }

    @Override
    public Page<EduExperimentVO> getEduExperimentVOPage(Page<EduExperiment> entityPage, HttpServletRequest request) {
        log.debug("[EduExperiment] 分页转换为VO: current={}, size={}", entityPage.getCurrent(), entityPage.getSize());
        Page<EduExperimentVO> voPage = ServiceMethodSupport.toVOPage(entityPage, entity -> {
            try {
                EduExperimentVO vo = EduExperimentVO.objToVo(entity);
                enrichClassInfo(vo, entity.getId());
                enrichInstructionUrl(vo, entity.getId());
                return vo;
            } catch (Exception e) {
                log.error("[EduExperiment] VO转换失败: id={}, error={}", entity.getId(), e.getMessage(), e);
                // 转换失败时返回基础VO，不影响整体分页结果
                EduExperimentVO fallback = EduExperimentVO.objToVo(entity);
                fallback.setClassCodes(Collections.emptyList());
                fallback.setClassNames(Collections.emptyList());
                fallback.setClassCount(0);
                return fallback;
            }
        });
        return voPage;
    }

    private void enrichClassInfo(EduExperimentVO vo, Long experimentId) {
        if (vo == null || experimentId == null) {
            return;
        }
        try {
            List<String> classCodes = experimentClassService.getClassCodesByExperiment(experimentId);
            List<String> classNames = experimentClassService.getClassNamesByExperiment(experimentId);
            vo.setClassCodes(classCodes);
            vo.setClassNames(classNames);
            vo.setClassCount(classCodes.size());
        } catch (Exception e) {
            log.warn("[EduExperiment] 补全班级信息失败: experimentId={}, error={}", experimentId, e.getMessage(), e);
            vo.setClassCodes(Collections.emptyList());
            vo.setClassNames(Collections.emptyList());
            vo.setClassCount(0);
        }
    }

    /**
     * 补充指导书URL
     * 指导书存储在 edu_experiment_attachment 表，通过 resultId=实验ID 和 studentId=0 标识
     */
    private void enrichInstructionUrl(EduExperimentVO vo, Long experimentId) {
        if (vo == null || experimentId == null) {
            return;
        }
        try {
            // 查询教师上传的指导书附件：resultId=实验ID, studentId=0, 未删除
            QueryWrapper<EduExperimentAttachment> qw = new QueryWrapper<>();
            qw.eq("result_id", experimentId)
              .eq("student_id", 0L)
              .eq("is_deleted", 0)
              .eq("upload_status", 1)
              .orderByDesc("created_at")
              .last("LIMIT 1");
            EduExperimentAttachment attachment = attachmentMapper.selectOne(qw);
            if (attachment != null && attachment.getObsUrl() != null) {
                vo.setInstructionUrl(attachment.getObsUrl());
                log.debug("[EduExperiment] 补全指导书URL: experimentId={}, url={}", experimentId, attachment.getObsUrl());
            }
        } catch (Exception e) {
            log.warn("[EduExperiment] 补全指导书URL失败: experimentId={}, error={}", experimentId, e.getMessage(), e);
        }
    }

    private String mapSortField(String sortField) {
        if (sortField == null) {
            return null;
        }
        String result = switch (sortField) {
            case "id" -> "experiment_id";
            case "sortOrder" -> "experiment_no";
            case "name" -> "experiment_name";
            case "categoryId" -> "experiment_type";
            case "fileType" -> "instruction_type";
            case "requirement" -> "experiment_requirement";
            case "contentDesc" -> "experiment_content";
            case "publishStatus" -> "state";
            default -> sortField;
        };
        log.trace("[EduExperiment] 映射排序字段: {} -> {}", sortField, result);
        return result;
    }
}
