package com.springboot.model.vo.experiment;

import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.enums.experiment.ExperimentTypeEnum;
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

    /**
     * 指导书文件访问路径
     */
    private String instructionUrl;

    private String categoryName;

    private Date createdAt;

    private Date updatedAt;

    /**
     * 绑定的班级编号列表（发布时选择）
     */
    private List<String> classCodes;

    /**
     * 绑定的班级名称列表
     */
    private List<String> classNames;

    /**
     * 绑定的班级数量
     */
    private Integer classCount;

    public static EduExperimentVO objToVo(EduExperiment entity) {
        if (entity == null) {
            return null;
        }
        EduExperimentVO vo = new EduExperimentVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setCategoryName(ExperimentTypeEnum.getTextByValue(entity.getCategoryId()));
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
