package com.springboot.service.homework.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.homework.EduExerciseItemMapper;
import com.springboot.mapper.homework.ResExerciseRecordMapper;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.homework.EduExerciseItemQueryRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.vo.homework.EduExerciseItemVO;
import com.springboot.service.homework.EduExerciseItemService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class EduExerciseItemServiceImpl extends ServiceImpl<EduExerciseItemMapper, EduExerciseItem> implements EduExerciseItemService {

    @Resource
    private ResExerciseRecordMapper resExerciseRecordMapper;

    @Override
    public void validEduExerciseItem(EduExerciseItem eduExerciseItem, boolean add) {
        ServiceMethodSupport.validEntity(eduExerciseItem);
        if (add && eduExerciseItem.getExerciseId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "作业ID不能为空");
        }
        if (StringUtils.isBlank(eduExerciseItem.getQuestion())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题干不能为空");
        }
        if (eduExerciseItem.getQuestionType() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题型不能为空");
        }
        if (StringUtils.isBlank(eduExerciseItem.getStandardAnswer())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标准答案不能为空");
        }
    }

    @Override
    public QueryWrapper<EduExerciseItem> getQueryWrapper(EduExerciseItemQueryRequest queryRequest) {
        QueryWrapper<EduExerciseItem> queryWrapper = ServiceMethodSupport.buildQueryWrapper(queryRequest);
        if (queryRequest.getId() != null) {
            queryWrapper.eq("id", queryRequest.getId());
        }
        if (queryRequest.getExerciseId() != null) {
            queryWrapper.eq("exercise_id", queryRequest.getExerciseId());
        }
        if (queryRequest.getQuestionType() != null) {
            queryWrapper.eq("question_type", queryRequest.getQuestionType());
        }
        if (StringUtils.isNotBlank(queryRequest.getQuestion())) {
            queryWrapper.like("question", queryRequest.getQuestion().trim());
        }
        return queryWrapper;
    }

    @Override
    public EduExerciseItemVO getEduExerciseItemVO(EduExerciseItem eduExerciseItem, HttpServletRequest request) {
        return EduExerciseItemVO.objToVo(eduExerciseItem);
    }

    @Override
    public Page<EduExerciseItemVO> getEduExerciseItemVOPage(Page<EduExerciseItem> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduExerciseItemVO::objToVo);
    }

    @Override
    public Boolean removeExerciseItemById(Long id) {
        long recordCount = resExerciseRecordMapper.selectCount(new QueryWrapper<ResExerciseRecord>().eq("item_id", id));
        if (recordCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该题目已有学生作答，不能移除");
        }
        return this.removeById(id);
    }
}
