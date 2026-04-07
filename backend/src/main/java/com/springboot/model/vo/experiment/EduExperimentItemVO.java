package com.springboot.model.vo.experiment;

import com.springboot.model.entity.experiment.EduExperimentItem;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EduExperimentItemVO implements Serializable {

    private Long id;

    private Integer sortOrder;

    private String itemName;

    private Integer questionType;

    private String questionContent;

    private Long experimentId;

    private String standardAnswer;

    private Integer maxScore;

    private Integer itemStatus;

    private Date createdAt;

    private Date updatedAt;

    public static EduExperimentItemVO objToVo(EduExperimentItem entity) {
        if (entity == null) {
            return null;
        }
        EduExperimentItemVO vo = new EduExperimentItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static EduExperimentItem voToObj(EduExperimentItemVO vo) {
        if (vo == null) {
            return null;
        }
        EduExperimentItem entity = new EduExperimentItem();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<EduExperimentItemVO> objToVo(List<EduExperimentItem> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduExperimentItemVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
