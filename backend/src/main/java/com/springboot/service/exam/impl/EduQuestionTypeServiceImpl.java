package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.exam.EduQuestionTypeMapper;
import com.springboot.model.dto.exam.EduQuestionTypeQueryRequest;
import com.springboot.model.entity.exam.EduQuestionType;
import com.springboot.model.vo.exam.EduQuestionTypeVO;
import com.springboot.service.exam.EduQuestionTypeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class EduQuestionTypeServiceImpl extends ServiceImpl<EduQuestionTypeMapper, EduQuestionType> implements EduQuestionTypeService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void validEduQuestionType(EduQuestionType eduQuestionType, boolean add) {
        if (eduQuestionType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (eduQuestionType.getTypeId() == null || eduQuestionType.getTypeId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题型编号非法");
        }
        if (StringUtils.isBlank(eduQuestionType.getTypeName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题型名称不能为空");
        }
    }

    @Override
    public QueryWrapper<EduQuestionType> getQueryWrapper(EduQuestionTypeQueryRequest queryRequest) {
        QueryWrapper<EduQuestionType> wrapper = new QueryWrapper<>();
        if (queryRequest == null) {
            return wrapper.orderByAsc("type_id");
        }
        wrapper.eq(queryRequest.getTypeId() != null, "type_id", queryRequest.getTypeId());
        wrapper.like(StringUtils.isNotBlank(queryRequest.getTypeName()), "type_name", queryRequest.getTypeName());
        wrapper.orderByAsc("type_id");
        return wrapper;
    }

    @Override
    public EduQuestionTypeVO getEduQuestionTypeVO(EduQuestionType eduQuestionType, HttpServletRequest request) {
        return EduQuestionTypeVO.objToVo(eduQuestionType);
    }

    @Override
    public Page<EduQuestionTypeVO> getEduQuestionTypeVOPage(Page<EduQuestionType> entityPage, HttpServletRequest request) {
        Page<EduQuestionTypeVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(EduQuestionTypeVO.objToVo(entityPage.getRecords()));
        return voPage;
    }

    @Override
    public List<Map<String, Object>> listAllTypes() {
        return jdbcTemplate.queryForList("SELECT type_id AS typeId, type_name AS typeName FROM t_question_type ORDER BY type_id ASC");
    }
}
