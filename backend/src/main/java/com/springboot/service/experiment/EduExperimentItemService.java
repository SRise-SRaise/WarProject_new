package com.springboot.service.experiment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.experiment.EduExperimentItemQueryRequest;
import com.springboot.model.entity.experiment.EduExperimentItem;
import com.springboot.model.vo.experiment.EduExperimentItemVO;
import jakarta.servlet.http.HttpServletRequest;

public interface EduExperimentItemService extends IService<EduExperimentItem> {

    void validEduExperimentItem(EduExperimentItem eduExperimentItem, boolean add);

    QueryWrapper<EduExperimentItem> getQueryWrapper(EduExperimentItemQueryRequest queryRequest);

    EduExperimentItemVO getEduExperimentItemVO(EduExperimentItem eduExperimentItem, HttpServletRequest request);

    Page<EduExperimentItemVO> getEduExperimentItemVOPage(Page<EduExperimentItem> entityPage, HttpServletRequest request);
}
