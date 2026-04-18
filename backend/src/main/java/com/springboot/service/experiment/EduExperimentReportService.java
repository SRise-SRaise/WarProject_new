package com.springboot.service.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.model.dto.experiment.EduExperimentReportGradeRequest;
import com.springboot.model.dto.experiment.EduExperimentReportQueryRequest;
import com.springboot.model.dto.experiment.EduExperimentReportSubmitRequest;
import com.springboot.model.vo.experiment.EduExperimentReportListVO;
import com.springboot.model.vo.experiment.EduExperimentReportVO;

import java.util.List;

public interface EduExperimentReportService {

    /**
     * 获取学生的实验报告（动态拼接）
     * @param experimentId 实验ID
     * @param studentId 学生ID
     * @return 报告VO
     */
    EduExperimentReportVO getStudentReport(Long experimentId, Long studentId);

    /**
     * 提交/保存学生实验答题
     * @param request 提交请求
     * @return 是否成功
     */
    boolean saveStudentAnswer(EduExperimentReportSubmitRequest request);

    /**
     * 批改实验报告
     * @param request 批改请求
     * @return 是否成功
     */
    boolean gradeReport(EduExperimentReportGradeRequest request);

    /**
     * 获取学生的所有实验报告列表
     * @param studentId 学生ID
     * @return 报告列表
     */
    List<EduExperimentReportVO> getStudentReportList(Long studentId);

    /**
     * 获取实验的所有报告列表（教师端）
     * @param experimentId 实验ID
     * @return 报告列表
     */
    List<EduExperimentReportVO> getExperimentReportList(Long experimentId);

    /**
     * 获取教师端实验报告列表（分页）
     * @param queryRequest 查询条件
     * @return 分页报告列表
     */
    Page<EduExperimentReportListVO> getExperimentReportListPage(EduExperimentReportQueryRequest queryRequest);
}
