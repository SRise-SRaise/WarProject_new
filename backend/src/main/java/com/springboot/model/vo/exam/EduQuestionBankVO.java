package com.springboot.model.vo.exam;

import com.springboot.model.entity.exam.EduQuestionBank;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EduQuestionBankVO implements Serializable {

    private Long id;

    private String questionContent;

    private String standardAnswer;

    private Integer questionType;

    private Date createdAt;

    private Date updatedAt;

    public static EduQuestionBankVO objToVo(EduQuestionBank entity) {
        if (entity == null) {
            return null;
        }
        EduQuestionBankVO vo = new EduQuestionBankVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public static EduQuestionBank voToObj(EduQuestionBankVO vo) {
        if (vo == null) {
            return null;
        }
        EduQuestionBank entity = new EduQuestionBank();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    public static List<EduQuestionBankVO> objToVo(List<EduQuestionBank> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduQuestionBankVO::objToVo).collect(Collectors.toList());
    }

    private static final long serialVersionUID = 1L;
}
