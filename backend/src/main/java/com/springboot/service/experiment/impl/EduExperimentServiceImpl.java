package com.springboot.service.experiment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.springboot.mapper.experiment.EduExperimentMapper;
import com.springboot.model.dto.experiment.EduExperimentQueryRequest;
import com.springboot.model.entity.experiment.EduExperiment;
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
import java.util.Map;

@Slf4j
@Service
public class EduExperimentServiceImpl extends ServiceImpl<EduExperimentMapper, EduExperiment> implements EduExperimentService {

    @Resource
    private EduExperimentMapper eduExperimentMapper;

    @Resource
    private ExperimentClassService experimentClassService;

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
        return vo;
    }

    @Override
    public Page<EduExperimentVO> getEduExperimentVOPage(Page<EduExperiment> entityPage, HttpServletRequest request) {
        log.debug("[EduExperiment] 分页转换为VO: current={}, size={}", entityPage.getCurrent(), entityPage.getSize());
        Page<EduExperimentVO> voPage = ServiceMethodSupport.toVOPage(entityPage, entity -> {
            try {
                EduExperimentVO vo = EduExperimentVO.objToVo(entity);
                enrichClassInfo(vo, entity.getId());
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
        // 单独从数据库读取 instruction_url（因实体字段标记为 exist=false，BeanUtils.copyProperties 无法自动填充）
        try {
            List<Map<String, Object>> rows = eduExperimentMapper.selectMaps(
                Wrappers.<EduExperiment>query()
                    .select("instruction_url")
                    .eq("experiment_id", experimentId)
            );
            if (!rows.isEmpty()) {
                Object url = rows.get(0).get("instruction_url");
                vo.setInstructionUrl(url != null ? url.toString() : null);
            }
        } catch (Exception e) {
            // instruction_url 列不存在时安静跳过，不影响其他数据返回
            log.debug("[EduExperiment] 读取 instruction_url 失败（列可能不存在）: experimentId={}, error={}", experimentId, e.getMessage());
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
