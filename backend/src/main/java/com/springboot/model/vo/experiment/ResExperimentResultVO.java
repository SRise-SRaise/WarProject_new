package com.springboot.model.vo.experiment;

import com.springboot.model.entity.experiment.ResExperimentResult;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ResExperimentResultVO implements Serializable {

    private Long id;

    private Long studentId;

    private Long itemId;

    private String subContent;

    private Integer score;

    private Date submittedAt;

    private Integer gradingStatus;

    private Date createdAt;

    private Date updatedAt;

    public static ResExperimentResultVO objToVo(ResExperimentResult entity) {
        if (entity == null) {
            return null;
        }
        ResExperimentResultVO vo = new ResExperimentResultVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static ResExperimentResult voToObj(ResExperimentResultVO vo) {
        if (vo == null) {
            return null;
        }
        ResExperimentResult entity = new ResExperimentResult();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<ResExperimentResultVO> objToVo(List<ResExperimentResult> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(ResExperimentResultVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
