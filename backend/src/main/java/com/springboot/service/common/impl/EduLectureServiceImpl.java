package com.springboot.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.common.EduLectureMapper;
import com.springboot.model.dto.common.EduLectureQueryRequest;
import com.springboot.model.entity.common.EduLecture;
import com.springboot.model.vo.common.EduLectureVO;
import com.springboot.service.common.EduLectureService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class EduLectureServiceImpl extends ServiceImpl<EduLectureMapper, EduLecture> implements EduLectureService {

    @Override
    public void validEduLecture(EduLecture eduLecture, boolean add) {
        ServiceMethodSupport.validEntity(eduLecture);
    }

    @Override
    public QueryWrapper<EduLecture> getQueryWrapper(EduLectureQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public EduLectureVO getEduLectureVO(EduLecture eduLecture, HttpServletRequest request) {
        return EduLectureVO.objToVo(eduLecture);
    }

    @Override
    public Page<EduLectureVO> getEduLectureVOPage(Page<EduLecture> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduLectureVO::objToVo);
    }
}
