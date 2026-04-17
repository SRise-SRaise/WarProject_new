package com.springboot.service.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.exam.RelPaperQuestionQueryRequest;
import com.springboot.model.entity.exam.RelPaperQuestion;
import com.springboot.model.vo.exam.RelPaperQuestionVO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface RelPaperQuestionService extends IService<RelPaperQuestion> {

    void validRelPaperQuestion(RelPaperQuestion relPaperQuestion, boolean add);

    QueryWrapper<RelPaperQuestion> getQueryWrapper(RelPaperQuestionQueryRequest queryRequest);

    RelPaperQuestionVO getRelPaperQuestionVO(RelPaperQuestion relPaperQuestion, HttpServletRequest request);

    Page<RelPaperQuestionVO> getRelPaperQuestionVOPage(Page<RelPaperQuestion> entityPage, HttpServletRequest request);

    List<RelPaperQuestion> listByPaperId(Long paperId);

    boolean addQuestionToPaper(Long paperId, Long questionId, Integer score, Integer questionOrder, String sectionName);

    boolean updatePaperQuestion(Long id, Integer score, String sectionName);

    boolean removeQuestionFromPaper(Long id);

    boolean reorderPaperQuestions(Long paperId, List<Long> questionIds);
}
