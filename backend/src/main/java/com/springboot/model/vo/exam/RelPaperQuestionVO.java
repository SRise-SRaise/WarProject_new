package com.springboot.model.vo.exam;

import com.springboot.model.entity.exam.RelPaperQuestion;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class RelPaperQuestionVO implements Serializable {

    private Long id;

    private Long paperId;

    private Long questionId;

    private Integer score;

    private Date createdAt;

    private Date updatedAt;

    public static RelPaperQuestionVO objToVo(RelPaperQuestion entity) {
        if (entity == null) {
            return null;
        }
        RelPaperQuestionVO vo = new RelPaperQuestionVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static RelPaperQuestion voToObj(RelPaperQuestionVO vo) {
        if (vo == null) {
            return null;
        }
        RelPaperQuestion entity = new RelPaperQuestion();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<RelPaperQuestionVO> objToVo(List<RelPaperQuestion> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(RelPaperQuestionVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
