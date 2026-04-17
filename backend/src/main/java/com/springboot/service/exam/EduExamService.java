package com.springboot.service.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.exam.EduExamQueryRequest;
import com.springboot.model.entity.exam.EduExam;
import com.springboot.model.vo.exam.EduExamVO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EduExamService extends IService<EduExam> {

    void validEduExam(EduExam eduExam, boolean add);

    QueryWrapper<EduExam> getQueryWrapper(EduExamQueryRequest queryRequest);

    EduExamVO getEduExamVO(EduExam eduExam, HttpServletRequest request);

    Page<EduExamVO> getEduExamVOPage(Page<EduExam> entityPage, HttpServletRequest request);

    Map<String, Object> listExamPage(EduExamQueryRequest queryRequest);

    Map<String, Object> getExamStats();

    List<Map<String, Object>> listAllPapersForExam();

    Long createExam(String examName, Long paperId, Integer durationMin, Date startTime, Date endTime, Boolean isPublished);

    boolean updateExamCustom(Long id, String examName, Long paperId, Integer durationMin, Date startTime, Date endTime, Boolean isPublished);

    boolean publishExam(Long id);

    boolean unpublishExam(Long id);

    List<Map<String, Object>> listPublishedExamsForStudent();

    Map<String, Object> getStudentExamDetail(Long examId);

    List<Map<String, Object>> listGradingExams();

    Map<String, Object> getAdminExamCard(Long examId);
}
