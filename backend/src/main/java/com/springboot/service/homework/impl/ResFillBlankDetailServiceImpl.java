package com.springboot.service.homework.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.homework.ResFillBlankDetailMapper;
import com.springboot.model.dto.homework.ResFillBlankDetailQueryRequest;
import com.springboot.model.entity.homework.ResFillBlankDetail;
import com.springboot.model.vo.homework.ResFillBlankDetailVO;
import com.springboot.service.homework.ResFillBlankDetailService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class ResFillBlankDetailServiceImpl extends ServiceImpl<ResFillBlankDetailMapper, ResFillBlankDetail> implements ResFillBlankDetailService {

    @Override
    public void validResFillBlankDetail(ResFillBlankDetail resFillBlankDetail, boolean add) {
        ServiceMethodSupport.validEntity(resFillBlankDetail);
    }

    @Override
    public QueryWrapper<ResFillBlankDetail> getQueryWrapper(ResFillBlankDetailQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public ResFillBlankDetailVO getResFillBlankDetailVO(ResFillBlankDetail resFillBlankDetail, HttpServletRequest request) {
        return ResFillBlankDetailVO.objToVo(resFillBlankDetail);
    }

    @Override
    public Page<ResFillBlankDetailVO> getResFillBlankDetailVOPage(Page<ResFillBlankDetail> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, ResFillBlankDetailVO::objToVo);
    }
}
