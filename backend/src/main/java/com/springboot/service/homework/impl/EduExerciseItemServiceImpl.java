package com.springboot.service.homework.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.homework.EduExerciseItemMapper;
import com.springboot.mapper.homework.EduExerciseMapper;
import com.springboot.mapper.homework.RelExerciseItemMapper;
import com.springboot.mapper.homework.ResExerciseRecordMapper;
import com.springboot.model.dto.homework.EduExerciseItemQueryRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.RelExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.vo.homework.EduExerciseItemVO;
import com.springboot.service.homework.EduExerciseItemService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EduExerciseItemServiceImpl extends ServiceImpl<EduExerciseItemMapper, EduExerciseItem> implements EduExerciseItemService {

    @Resource
    private ResExerciseRecordMapper resExerciseRecordMapper;

    @Resource
    private RelExerciseItemMapper relExerciseItemMapper;

    @Resource
    private EduExerciseMapper eduExerciseMapper;

    @Override
    public void validEduExerciseItem(EduExerciseItem eduExerciseItem, boolean add) {
        ServiceMethodSupport.validEntity(eduExerciseItem);
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
    public Page<EduExerciseItemVO> listExerciseItemVOByExerciseId(Long exerciseId, long current, long size) {
        QueryWrapper<RelExerciseItem> relQuery = new QueryWrapper<>();
        relQuery.eq("exercise_id", exerciseId).orderByAsc("item_order").orderByAsc("id");
        Page<RelExerciseItem> relPage = relExerciseItemMapper.selectPage(new Page<>(current, size), relQuery);
        if (relPage.getRecords().isEmpty()) {
            return new Page<>(current, size, 0);
        }

        List<Long> itemIds = relPage.getRecords().stream().map(RelExerciseItem::getItemId).collect(Collectors.toList());
        List<EduExerciseItem> items = this.listByIds(itemIds);
        Map<Long, EduExerciseItem> itemMap = new HashMap<>();
        for (EduExerciseItem item : items) {
            itemMap.put(item.getId(), item);
        }

        List<EduExerciseItemVO> voList = new ArrayList<>();
        for (RelExerciseItem rel : relPage.getRecords()) {
            EduExerciseItem item = itemMap.get(rel.getItemId());
            if (item == null) {
                continue;
            }
            EduExerciseItemVO vo = EduExerciseItemVO.objToVo(item);
            vo.setExerciseId(rel.getExerciseId());
            if (rel.getItemScore() != null) {
                vo.setMaxScore(rel.getItemScore());
            }
            voList.add(vo);
        }

        Page<EduExerciseItemVO> voPage = new Page<>(current, size, relPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Boolean addExerciseItemToExercise(Long exerciseId, Long itemId, Integer itemScore) {
        if (exerciseId == null || exerciseId <= 0 || itemId == null || itemId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "作业或题目参数无效");
        }
        if (eduExerciseMapper.selectById(exerciseId) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "作业不存在");
        }
        if (this.getById(itemId) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        QueryWrapper<RelExerciseItem> existsQuery = new QueryWrapper<>();
        existsQuery.eq("exercise_id", exerciseId).eq("item_id", itemId);
        RelExerciseItem exists = relExerciseItemMapper.selectOne(existsQuery);
        if (exists != null) {
            if (itemScore != null) {
                exists.setItemScore(itemScore);
                relExerciseItemMapper.updateById(exists);
            }
            return true;
        }

        int nextOrder = 1;
        RelExerciseItem latest = relExerciseItemMapper.selectOne(
                new QueryWrapper<RelExerciseItem>()
                        .eq("exercise_id", exerciseId)
                        .orderByDesc("item_order")
                        .last("limit 1")
        );
        if (latest != null && latest.getItemOrder() != null) {
            nextOrder = latest.getItemOrder() + 1;
        }

        RelExerciseItem rel = new RelExerciseItem();
        rel.setExerciseId(exerciseId);
        rel.setItemId(itemId);
        rel.setItemOrder(nextOrder);
        rel.setItemScore(itemScore);
        return relExerciseItemMapper.insert(rel) > 0;
    }

    @Override
    public Boolean removeExerciseItemFromExercise(Long exerciseId, Long itemId) {
        if (exerciseId == null || exerciseId <= 0 || itemId == null || itemId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "作业或题目参数无效");
        }
        QueryWrapper<RelExerciseItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exercise_id", exerciseId).eq("item_id", itemId);
        return relExerciseItemMapper.delete(queryWrapper) > 0;
    }

    @Override
    public Boolean removeExerciseItemById(Long id) {
        long recordCount = resExerciseRecordMapper.selectCount(new QueryWrapper<ResExerciseRecord>().eq("item_id", id));
        if (recordCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该题目已有学生作答，不能移除");
        }
        relExerciseItemMapper.delete(new QueryWrapper<RelExerciseItem>().eq("item_id", id));
        return this.removeById(id);
    }
}
