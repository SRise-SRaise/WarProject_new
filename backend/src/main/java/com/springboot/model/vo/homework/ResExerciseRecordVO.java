package com.springboot.model.vo.homework;

import com.springboot.model.entity.homework.ResExerciseRecord;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ResExerciseRecordVO implements Serializable {

    private Long id;

    private Long itemId;

    private Long studentId;

    private String choiceAnswer;

    private String textContent;

    private Integer score;

    private Date submittedAt;

    private Date createdAt;

    private Date updatedAt;

    public static ResExerciseRecordVO objToVo(ResExerciseRecord entity) {
        if (entity == null) {
            return null;
        }
        ResExerciseRecordVO vo = new ResExerciseRecordVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static ResExerciseRecord voToObj(ResExerciseRecordVO vo) {
        if (vo == null) {
            return null;
        }
        ResExerciseRecord entity = new ResExerciseRecord();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<ResExerciseRecordVO> objToVo(List<ResExerciseRecord> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(ResExerciseRecordVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
