package com.springboot.model.vo.common;

import com.springboot.model.entity.common.EduLecture;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EduLectureVO implements Serializable {

    private Long id;

    private String lectureName;

    private Integer categoryId;

    private String fileExtension;

    private String filePath;

    private Date createdAt;

    private Date updatedAt;

    public static EduLectureVO objToVo(EduLecture entity) {
        if (entity == null) {
            return null;
        }
        EduLectureVO vo = new EduLectureVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static EduLecture voToObj(EduLectureVO vo) {
        if (vo == null) {
            return null;
        }
        EduLecture entity = new EduLecture();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<EduLectureVO> objToVo(List<EduLecture> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduLectureVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
