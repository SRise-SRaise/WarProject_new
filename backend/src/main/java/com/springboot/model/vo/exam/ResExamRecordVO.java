package com.springboot.model.vo.exam;

import com.springboot.model.entity.exam.ResExamRecord;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ResExamRecordVO implements Serializable {

    private Long id;

    private Long studentId;

    private Long questionId;

    private String studentAnswer;

    private Date createdAt;

    private Date updatedAt;

    public static ResExamRecordVO objToVo(ResExamRecord entity) {
        if (entity == null) {
            return null;
        }
        ResExamRecordVO vo = new ResExamRecordVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static ResExamRecord voToObj(ResExamRecordVO vo) {
        if (vo == null) {
            return null;
        }
        ResExamRecord entity = new ResExamRecord();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<ResExamRecordVO> objToVo(List<ResExamRecord> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(ResExamRecordVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
