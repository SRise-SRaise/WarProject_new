package com.springboot.service.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.common.EduLectureQueryRequest;
import com.springboot.model.entity.common.EduLecture;
import com.springboot.model.vo.common.EduLectureVO;
import jakarta.servlet.http.HttpServletRequest;

public interface EduLectureService extends IService<EduLecture> {

    void validEduLecture(EduLecture eduLecture, boolean add);

    QueryWrapper<EduLecture> getQueryWrapper(EduLectureQueryRequest queryRequest);

    EduLectureVO getEduLectureVO(EduLecture eduLecture, HttpServletRequest request);

    Page<EduLectureVO> getEduLectureVOPage(Page<EduLecture> entityPage, HttpServletRequest request);
}
