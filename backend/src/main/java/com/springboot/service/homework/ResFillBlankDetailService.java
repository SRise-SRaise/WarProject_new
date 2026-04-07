package com.springboot.service.homework;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.homework.ResFillBlankDetailQueryRequest;
import com.springboot.model.entity.homework.ResFillBlankDetail;
import com.springboot.model.vo.homework.ResFillBlankDetailVO;
import jakarta.servlet.http.HttpServletRequest;

public interface ResFillBlankDetailService extends IService<ResFillBlankDetail> {

    void validResFillBlankDetail(ResFillBlankDetail resFillBlankDetail, boolean add);

    QueryWrapper<ResFillBlankDetail> getQueryWrapper(ResFillBlankDetailQueryRequest queryRequest);

    ResFillBlankDetailVO getResFillBlankDetailVO(ResFillBlankDetail resFillBlankDetail, HttpServletRequest request);

    Page<ResFillBlankDetailVO> getResFillBlankDetailVOPage(Page<ResFillBlankDetail> entityPage, HttpServletRequest request);
}
