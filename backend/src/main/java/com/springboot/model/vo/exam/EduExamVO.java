package com.springboot.model.vo.exam;

import com.springboot.model.entity.exam.EduExam;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EduExamVO implements Serializable {

    private Long id;

    private String examName;

    private Integer durationMin;

    private Date startTime;

    private Boolean isPublished;

    private Date createdAt;

    private Date updatedAt;

    public static EduExamVO objToVo(EduExam entity) {
        if (entity == null) {
            return null;
        }
        EduExamVO vo = new EduExamVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static EduExam voToObj(EduExamVO vo) {
        if (vo == null) {
            return null;
        }
        EduExam entity = new EduExam();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<EduExamVO> objToVo(List<EduExam> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduExamVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
