package com.springboot.service.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.exam.ResExamRecordQueryRequest;
import com.springboot.model.entity.exam.ResExamRecord;
import com.springboot.model.vo.exam.ResExamRecordVO;
import jakarta.servlet.http.HttpServletRequest;

public interface ResExamRecordService extends IService<ResExamRecord> {

    void validResExamRecord(ResExamRecord resExamRecord, boolean add);

    QueryWrapper<ResExamRecord> getQueryWrapper(ResExamRecordQueryRequest queryRequest);

    ResExamRecordVO getResExamRecordVO(ResExamRecord resExamRecord, HttpServletRequest request);

    Page<ResExamRecordVO> getResExamRecordVOPage(Page<ResExamRecord> entityPage, HttpServletRequest request);
}
