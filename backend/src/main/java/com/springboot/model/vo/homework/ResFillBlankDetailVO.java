package com.springboot.model.vo.homework;

import com.springboot.model.entity.homework.ResFillBlankDetail;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ResFillBlankDetailVO implements Serializable {

    private Long id;

    private Long itemId;

    private Integer blankIndex;

    private String answerContent;

    private String answerHash;

    private Integer submitCount;

    private Boolean isCorrect;

    private Date createdAt;

    private Date updatedAt;

    public static ResFillBlankDetailVO objToVo(ResFillBlankDetail entity) {
        if (entity == null) {
            return null;
        }
        ResFillBlankDetailVO vo = new ResFillBlankDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static ResFillBlankDetail voToObj(ResFillBlankDetailVO vo) {
        if (vo == null) {
            return null;
        }
        ResFillBlankDetail entity = new ResFillBlankDetail();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<ResFillBlankDetailVO> objToVo(List<ResFillBlankDetail> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(ResFillBlankDetailVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
