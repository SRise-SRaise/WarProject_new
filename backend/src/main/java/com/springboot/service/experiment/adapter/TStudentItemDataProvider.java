package com.springboot.service.experiment.adapter;

import com.springboot.mapper.experiment.EduExperimentAnalysisMapper;
import com.springboot.mapper.experiment.StudentExperimentAnalysisMapper;
import com.springboot.model.dto.experiment.ExperimentTypeCountDTO;
import com.springboot.model.dto.experiment.ExperimentTypeScoreDTO;
import com.springboot.service.experiment.adapter.ScoreDataProvider.HasExperimentScore;
import com.springboot.service.experiment.adapter.ScoreDataProvider.HasTypeCode;
import com.springboot.service.experiment.adapter.ScoreDataProvider.HasTypeCodeAndScore;
import com.springboot.service.experiment.adapter.ScoreDataProvider.HasTypeCodeEx;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * t_student_item 表数据访问适配器实现（适配器模式的具体适配者）。
 *
 * <p>将当前 t_student_item 表的查询逻辑封装为 {@link ScoreDataProvider} 接口实现，
 * 当未来数据源迁移至 res_experiment_result 表时，只需新增
 * {@code ResExperimentResultDataProvider} 实现类即可，分析策略无需改动。</p>
 *
 * @see ScoreDataProvider
 */
@Slf4j
@Component
public class TStudentItemDataProvider implements ScoreDataProvider {

    @Resource
    private EduExperimentAnalysisMapper eduMapper;

    @Resource
    private StudentExperimentAnalysisMapper studentMapper;

    // ==================== 单实验维度 ====================

    @Override
    public Integer countTotalStudents(Long experimentId, String clazzNo) {
        return eduMapper.countTotalStudents(experimentId, clazzNo);
    }

    @Override
    public Integer countSubmittedStudents(Long experimentId, String clazzNo) {
        return eduMapper.countSubmittedStudents(experimentId, clazzNo);
    }

    @Override
    public Integer countGradedStudents(Long experimentId, String clazzNo) {
        return eduMapper.countGradedStudents(experimentId, clazzNo);
    }

    @Override
    public Double avgScore(Long experimentId, String clazzNo) {
        return eduMapper.avgScore(experimentId, clazzNo);
    }

    @Override
    public Integer maxScore(Long experimentId, String clazzNo) {
        return eduMapper.maxScore(experimentId, clazzNo);
    }

    @Override
    public Integer minScore(Long experimentId, String clazzNo) {
        return eduMapper.minScore(experimentId, clazzNo);
    }

    @Override
    public List<? extends HasExperimentScore> listStudentTotalScores(Long experimentId, String clazzNo) {
        return eduMapper.selectStudentScores(experimentId, clazzNo);
    }

    // ==================== 全局维度 ====================

    @Override
    public Integer countPublishedExperiments() {
        return eduMapper.countPublishedExperiments();
    }

    @Override
    public Integer countActiveExperiments() {
        return eduMapper.countActiveExperiments();
    }

    @Override
    public Integer countGloballyGradedExperiments() {
        return eduMapper.countGloballyGradedExperiments();
    }

    @Override
    public List<? extends HasTypeCode> listExperimentTypeCounts() {
        return eduMapper.selectExperimentTypeCounts().stream().toList();
    }

    @Override
    public List<? extends HasTypeCode> listExperimentTypeSubmittedCounts() {
        return eduMapper.selectExperimentTypeSubmittedCounts();
    }

    @Override
    public List<? extends HasTypeCode> listExperimentTypeGradedCounts() {
        return eduMapper.selectExperimentTypeGradedCounts();
    }

    @Override
    public List<? extends HasTypeCode> listExperimentTypeTotalSubmissions() {
        return eduMapper.selectExperimentTypeTotalSubmissions();
    }

    @Override
    public List<? extends HasTypeCode> listExperimentTypeAverageScores() {
        return eduMapper.selectExperimentTypeAverageScores();
    }

    // ==================== 学生维度 ====================

    @Override
    public Integer countCompletedExperiments(Long studentId) {
        return studentMapper.countCompletedExperiments(studentId);
    }

    @Override
    public Integer countGradedExperiments(Long studentId) {
        return studentMapper.countGradedExperiments(studentId);
    }

    @Override
    public Integer sumTotalScore(Long studentId) {
        return studentMapper.sumTotalScore(studentId);
    }

    @Override
    public Integer sumTotalMaxScore(Long studentId) {
        return studentMapper.sumTotalMaxScore(studentId);
    }

    @Override
    public Integer getMaxScore(Long studentId) {
        return studentMapper.getMaxScore(studentId);
    }

    @Override
    public Integer getMinScore(Long studentId) {
        return studentMapper.getMinScore(studentId);
    }

    @Override
    public Integer countTotalPublishedExperiments() {
        return studentMapper.countTotalPublishedExperiments();
    }

    @Override
    public List<? extends HasTypeCodeAndScore> listStudentScoreByTypes(Long studentId, List<Integer> typeCodes) {
        return studentMapper.selectScoreGroupByExperimentType(studentId, typeCodes);
    }
}
