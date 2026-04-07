package com.springboot.model.vo.experiment;

import com.springboot.model.entity.experiment.EduExperiment;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EduExperimentVO implements Serializable {

    private Long id;

    private Integer sortOrder;

    private String name;

    private Integer categoryId;

    private String fileType;

    private String requirement;

    private String contentDesc;

    private Integer publishStatus;

    private Date createdAt;

    private Date updatedAt;

    public static EduExperimentVO objToVo(EduExperiment entity) {
        if (entity == null) {
            return null;
        }
        EduExperimentVO vo = new EduExperimentVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static EduExperiment voToObj(EduExperimentVO vo) {
        if (vo == null) {
            return null;
        }
        EduExperiment entity = new EduExperiment();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<EduExperimentVO> objToVo(List<EduExperiment> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduExperimentVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
