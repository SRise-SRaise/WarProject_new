package com.springboot.model.vo.homework;

import com.springboot.model.entity.homework.EduExercise;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EduExerciseVO implements Serializable {

    private Long id;

    private Integer sortOrder;

    private String taskName;

    private Long relateExpId;

    private Integer interactMode;

    private String description;

    private Integer publishStatus;

    private Long teacherId;

    private Date startTime;

    private Date endTime;

    private Date createdAt;

    private Date updatedAt;

    /**
     * 已布置班级编号
     */
    private List<String> classCodes;

    /**
     * 已提交人数（按学生去重）
     */
    private Integer submissionCount;

    /**
     * 已完成批阅人数（按学生去重）
     */
    private Integer reviewedCount;

    public static EduExerciseVO objToVo(EduExercise entity) {
        if (entity == null) {
            return null;
        }
        EduExerciseVO vo = new EduExerciseVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static EduExercise voToObj(EduExerciseVO vo) {
        if (vo == null) {
            return null;
        }
        EduExercise entity = new EduExercise();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<EduExerciseVO> objToVo(List<EduExercise> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduExerciseVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
