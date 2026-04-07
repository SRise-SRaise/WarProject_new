package com.springboot.service.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.exam.ResScoreSummaryQueryRequest;
import com.springboot.model.entity.exam.ResScoreSummary;
import com.springboot.model.vo.exam.ResScoreSummaryVO;
import jakarta.servlet.http.HttpServletRequest;

public interface ResScoreSummaryService extends IService<ResScoreSummary> {

    void validResScoreSummary(ResScoreSummary resScoreSummary, boolean add);

    QueryWrapper<ResScoreSummary> getQueryWrapper(ResScoreSummaryQueryRequest queryRequest);

    ResScoreSummaryVO getResScoreSummaryVO(ResScoreSummary resScoreSummary, HttpServletRequest request);

    Page<ResScoreSummaryVO> getResScoreSummaryVOPage(Page<ResScoreSummary> entityPage, HttpServletRequest request);
}
