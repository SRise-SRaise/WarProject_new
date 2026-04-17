package com.springboot.service.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.exam.EduPaperQueryRequest;
import com.springboot.model.entity.exam.EduPaper;
import com.springboot.model.vo.exam.EduPaperVO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface EduPaperService extends IService<EduPaper> {

    void validEduPaper(EduPaper eduPaper, boolean add);

    QueryWrapper<EduPaper> getQueryWrapper(EduPaperQueryRequest queryRequest);

    EduPaperVO getEduPaperVO(EduPaper eduPaper, HttpServletRequest request);

    Page<EduPaperVO> getEduPaperVOPage(Page<EduPaper> entityPage, HttpServletRequest request);

    Map<String, Object> listPaperPage(EduPaperQueryRequest queryRequest);

    Map<String, Object> getPaperDetail(Long paperId);

    List<Map<String, Object>> listAllPaperOptions();
}
