package com.springboot.service.homework;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.homework.ResExerciseRecordQueryRequest;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.vo.homework.ResExerciseRecordVO;
import jakarta.servlet.http.HttpServletRequest;

public interface ResExerciseRecordService extends IService<ResExerciseRecord> {

    void validResExerciseRecord(ResExerciseRecord resExerciseRecord, boolean add);

    QueryWrapper<ResExerciseRecord> getQueryWrapper(ResExerciseRecordQueryRequest queryRequest);

    ResExerciseRecordVO getResExerciseRecordVO(ResExerciseRecord resExerciseRecord, HttpServletRequest request);

    Page<ResExerciseRecordVO> getResExerciseRecordVOPage(Page<ResExerciseRecord> entityPage, HttpServletRequest request);
}
