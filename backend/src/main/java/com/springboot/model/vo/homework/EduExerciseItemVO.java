package com.springboot.model.vo.homework;

import com.springboot.model.entity.homework.EduExerciseItem;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EduExerciseItemVO implements Serializable {

    private Long id;

    private Long exerciseId;

    private String question;

    private String optionsText;

    private String standardAnswer;

    private Integer questionType;

    private Integer maxScore;

    private Long questionBankId;

    private String analysis;

    private Integer difficulty;

    private Date createdAt;

    private Date updatedAt;

    public static EduExerciseItemVO objToVo(EduExerciseItem entity) {
        if (entity == null) {
            return null;
        }
        EduExerciseItemVO vo = new EduExerciseItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static EduExerciseItem voToObj(EduExerciseItemVO vo) {
        if (vo == null) {
            return null;
        }
        EduExerciseItem entity = new EduExerciseItem();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<EduExerciseItemVO> objToVo(List<EduExerciseItem> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduExerciseItemVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
