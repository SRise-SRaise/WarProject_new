package com.springboot.service.homework;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.homework.ResSubmissionLogQueryRequest;
import com.springboot.model.entity.homework.ResSubmissionLog;
import com.springboot.model.vo.homework.ResSubmissionLogVO;
import jakarta.servlet.http.HttpServletRequest;

public interface ResSubmissionLogService extends IService<ResSubmissionLog> {

    void validResSubmissionLog(ResSubmissionLog resSubmissionLog, boolean add);

    QueryWrapper<ResSubmissionLog> getQueryWrapper(ResSubmissionLogQueryRequest queryRequest);

    ResSubmissionLogVO getResSubmissionLogVO(ResSubmissionLog resSubmissionLog, HttpServletRequest request);

    Page<ResSubmissionLogVO> getResSubmissionLogVOPage(Page<ResSubmissionLog> entityPage, HttpServletRequest request);
}
