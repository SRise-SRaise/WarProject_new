package com.springboot.model.vo.experiment;

import com.springboot.model.entity.experiment.EduExperimentQuestion;
import com.springboot.model.enums.experiment.ExperimentDifficultyEnum;
import com.springboot.model.enums.experiment.ExperimentQuestionTypeEnum;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 实验题库视图对象
 */
@Data
public class EduExperimentQuestionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目主键ID
     */
    private Long id;

    /**
     * 题目名称/标题
     */
    private String questionName;

    /**
     * 题目内容/题干
     */
    private String questionContent;

    /**
     * 题目类型
     */
    private Integer questionType;

    /**
     * 题目类型描述
     */
    private String questionTypeDesc;

    /**
     * 难度等级
     */
    private Integer difficulty;

    /**
     * 难度描述
     */
    private String difficultyDesc;

    /**
     * 所属知识点/标签
     */
    private String tag;

    /**
     * 选项内容（JSON格式，适用选择题）
     */
    private String options;

    /**
     * 标准答案
     */
    private String standardAnswer;

    /**
     * 答案解析
     */
    private String answerAnalysis;

    /**
     * 参考代码（适用编程题）
     */
    private String referenceCode;

    /**
     * 题目分值
     */
    private Integer score;

    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 
     */
    private Date createdAt;

    /**
     * 
     */
    private Date updatedAt;

    /**
     * 实体转VO
     */
    public static EduExperimentQuestionVO objToVo(EduExperimentQuestion entity) {
        if (entity == null) {
            return null;
        }
        EduExperimentQuestionVO vo = new EduExperimentQuestionVO();
        BeanUtils.copyProperties(entity, vo);

        // 设置题目类型描述
        ExperimentQuestionTypeEnum typeEnum = ExperimentQuestionTypeEnum.getEnumByCode(entity.getQuestionType());
        if (typeEnum != null) {
            vo.setQuestionTypeDesc(typeEnum.getDescription());
        }

        // 设置难度描述
        ExperimentDifficultyEnum difficultyEnum = ExperimentDifficultyEnum.getEnumByCode(entity.getDifficulty());
        if (difficultyEnum != null) {
            vo.setDifficultyDesc(difficultyEnum.getDescription());
        }

        return vo;
    }

    /**
     * VO转实体
     */
    public static EduExperimentQuestion voToObj(EduExperimentQuestionVO vo) {
        if (vo == null) {
            return null;
        }
        EduExperimentQuestion entity = new EduExperimentQuestion();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }

    /**
     * 实体列表转VO列表
     */
    public static List<EduExperimentQuestionVO> objToVo(List<EduExperimentQuestion> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduExperimentQuestionVO::objToVo).collect(Collectors.toList());
    }
}
