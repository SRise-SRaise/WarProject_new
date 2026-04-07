package com.springboot.service.experiment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.experiment.ResExperimentResultQueryRequest;
import com.springboot.model.entity.experiment.ResExperimentResult;
import com.springboot.model.vo.experiment.ResExperimentResultVO;
import jakarta.servlet.http.HttpServletRequest;

public interface ResExperimentResultService extends IService<ResExperimentResult> {

    void validResExperimentResult(ResExperimentResult resExperimentResult, boolean add);

    QueryWrapper<ResExperimentResult> getQueryWrapper(ResExperimentResultQueryRequest queryRequest);

    ResExperimentResultVO getResExperimentResultVO(ResExperimentResult resExperimentResult, HttpServletRequest request);

    Page<ResExperimentResultVO> getResExperimentResultVOPage(Page<ResExperimentResult> entityPage, HttpServletRequest request);
}
