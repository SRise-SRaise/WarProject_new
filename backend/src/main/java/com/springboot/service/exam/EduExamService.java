package com.springboot.service.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.exam.EduExamQueryRequest;
import com.springboot.model.entity.exam.EduExam;
import com.springboot.model.vo.exam.EduExamVO;
import jakarta.servlet.http.HttpServletRequest;

public interface EduExamService extends IService<EduExam> {

    void validEduExam(EduExam eduExam, boolean add);

    QueryWrapper<EduExam> getQueryWrapper(EduExamQueryRequest queryRequest);

    EduExamVO getEduExamVO(EduExam eduExam, HttpServletRequest request);

    Page<EduExamVO> getEduExamVOPage(Page<EduExam> entityPage, HttpServletRequest request);
}
