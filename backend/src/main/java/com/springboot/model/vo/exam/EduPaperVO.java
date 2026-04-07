package com.springboot.model.vo.exam;

import com.springboot.model.entity.exam.EduPaper;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EduPaperVO implements Serializable {

    private Long id;

    private Integer paperCode;

    private String paperName;

    private String description;

    private Date generationTime;

    private Date createdAt;

    private Date updatedAt;

    public static EduPaperVO objToVo(EduPaper entity) {
        if (entity == null) {
            return null;
        }
        EduPaperVO vo = new EduPaperVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static EduPaper voToObj(EduPaperVO vo) {
        if (vo == null) {
            return null;
        }
        EduPaper entity = new EduPaper();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<EduPaperVO> objToVo(List<EduPaper> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduPaperVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
