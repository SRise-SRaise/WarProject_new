package com.springboot.service.experiment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.experiment.EduExperimentQueryRequest;
import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.vo.experiment.EduExperimentVO;
import jakarta.servlet.http.HttpServletRequest;

public interface EduExperimentService extends IService<EduExperiment> {

    void validEduExperiment(EduExperiment eduExperiment, boolean add);

    QueryWrapper<EduExperiment> getQueryWrapper(EduExperimentQueryRequest queryRequest);

    EduExperimentVO getEduExperimentVO(EduExperiment eduExperiment, HttpServletRequest request);

    Page<EduExperimentVO> getEduExperimentVOPage(Page<EduExperiment> entityPage, HttpServletRequest request);
}
