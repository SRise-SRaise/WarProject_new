package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.exam.ResScoreSummaryMapper;
import com.springboot.model.dto.exam.ResScoreSummaryQueryRequest;
import com.springboot.model.entity.exam.ResScoreSummary;
import com.springboot.model.vo.exam.ResScoreSummaryVO;
import com.springboot.service.exam.ResScoreSummaryService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class ResScoreSummaryServiceImpl extends ServiceImpl<ResScoreSummaryMapper, ResScoreSummary> implements ResScoreSummaryService {

    @Override
    public void validResScoreSummary(ResScoreSummary resScoreSummary, boolean add) {
        ServiceMethodSupport.validEntity(resScoreSummary);
    }

    @Override
    public QueryWrapper<ResScoreSummary> getQueryWrapper(ResScoreSummaryQueryRequest queryRequest) {
        if (queryRequest == null) {
            return ServiceMethodSupport.buildQueryWrapper(null);
        }
        queryRequest.setSortField(mapSortField(queryRequest.getSortField()));
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public ResScoreSummaryVO getResScoreSummaryVO(ResScoreSummary resScoreSummary, HttpServletRequest request) {
        return ResScoreSummaryVO.objToVo(resScoreSummary);
    }

    @Override
    public Page<ResScoreSummaryVO> getResScoreSummaryVOPage(Page<ResScoreSummary> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, ResScoreSummaryVO::objToVo);
    }

    private String mapSortField(String sortField) {
        if (sortField == null) {
            return null;
        }
        return switch (sortField) {
            case "id" -> "score_id";
            case "studentId" -> "student_id";
            case "experimentId" -> "experiment_id";
            case "totalScore" -> "score";
            default -> sortField;
        };
    }
}
