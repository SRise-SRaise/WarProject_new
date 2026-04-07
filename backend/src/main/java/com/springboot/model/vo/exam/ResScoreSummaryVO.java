package com.springboot.model.vo.exam;

import com.springboot.model.entity.exam.ResScoreSummary;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ResScoreSummaryVO implements Serializable {

    private Long id;

    private Long studentId;

    private Long experimentId;

    private Integer totalScore;

    private Date createdAt;

    private Date updatedAt;

    public static ResScoreSummaryVO objToVo(ResScoreSummary entity) {
        if (entity == null) {
            return null;
        }
        ResScoreSummaryVO vo = new ResScoreSummaryVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static ResScoreSummary voToObj(ResScoreSummaryVO vo) {
        if (vo == null) {
            return null;
        }
        ResScoreSummary entity = new ResScoreSummary();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<ResScoreSummaryVO> objToVo(List<ResScoreSummary> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(ResScoreSummaryVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
