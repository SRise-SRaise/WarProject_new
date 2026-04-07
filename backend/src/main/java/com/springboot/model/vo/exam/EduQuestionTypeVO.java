package com.springboot.model.vo.exam;

import com.springboot.model.entity.exam.EduQuestionType;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EduQuestionTypeVO implements Serializable {

    private Integer typeId;

    private String typeName;

    private Date createdAt;

    public static EduQuestionTypeVO objToVo(EduQuestionType entity) {
        if (entity == null) {
            return null;
        }
        EduQuestionTypeVO vo = new EduQuestionTypeVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static EduQuestionType voToObj(EduQuestionTypeVO vo) {
        if (vo == null) {
            return null;
        }
        EduQuestionType entity = new EduQuestionType();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<EduQuestionTypeVO> objToVo(List<EduQuestionType> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduQuestionTypeVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
