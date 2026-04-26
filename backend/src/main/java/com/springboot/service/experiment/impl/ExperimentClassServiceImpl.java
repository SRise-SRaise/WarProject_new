package com.springboot.service.experiment.impl;

import com.springboot.mapper.experiment.RelExperimentClassMapper;
import com.springboot.mapper.user.AuthClassMapper;
import com.springboot.model.entity.experiment.RelExperimentClass;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.service.experiment.ExperimentClassService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExperimentClassServiceImpl implements ExperimentClassService {

    @Resource
    private RelExperimentClassMapper relExperimentClassMapper;

    @Resource
    private AuthClassMapper authClassMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindClasses(Long experimentId, List<String> classCodes) {
        if (experimentId == null || classCodes == null || classCodes.isEmpty()) {
            log.warn("[ExperimentClass] 绑定班级失败：参数异常 experimentId={}, classCodes={}", experimentId, classCodes);
            return;
        }
        log.info("[ExperimentClass] 绑定班级: experimentId={}, classCodes={}", experimentId, classCodes);

        // 先删除旧关联
        relExperimentClassMapper.deleteByExperimentId(experimentId);

        // 批量插入新关联
        List<RelExperimentClass> records = classCodes.stream()
                .distinct()
                .map(code -> {
                    RelExperimentClass rec = new RelExperimentClass();
                    rec.setExperimentId(experimentId);
                    rec.setClassCode(code);
                    return rec;
                })
                .collect(Collectors.toList());

        if (!records.isEmpty()) {
            relExperimentClassMapper.batchInsert(records);
        }
        log.info("[ExperimentClass] 绑定完成: experimentId={}, 共{}个班级", experimentId, records.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindClasses(Long experimentId) {
        if (experimentId == null) {
            return;
        }
        relExperimentClassMapper.deleteByExperimentId(experimentId);
        log.info("[ExperimentClass] 解绑班级: experimentId={}", experimentId);
    }

    @Override
    public List<String> getClassCodesByExperiment(Long experimentId) {
        if (experimentId == null) {
            return Collections.emptyList();
        }
        return relExperimentClassMapper.selectClassCodesByExperimentId(experimentId);
    }

    @Override
    public List<Long> getExperimentIdsByClass(String classCode) {
        if (classCode == null || classCode.isBlank()) {
            return Collections.emptyList();
        }
        return relExperimentClassMapper.selectExperimentIdsByClassCode(classCode);
    }

    @Override
    public List<String> getClassNamesByExperiment(Long experimentId) {
        List<String> codes = getClassCodesByExperiment(experimentId);
        if (codes.isEmpty()) {
            return Collections.emptyList();
        }
        List<AuthClass> classes = authClassMapper.selectBatchIds(codes);
        return classes.stream()
                .map(c -> c.getClassCode() + "(" + (c.getHeadmasterName() != null ? c.getHeadmasterName() : "") + ")")
                .collect(Collectors.toList());
    }

    @Override
    public int getClassCount(Long experimentId) {
        return getClassCodesByExperiment(experimentId).size();
    }
}
