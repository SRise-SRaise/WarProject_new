package com.springboot.model.enums.experiment;

import lombok.Getter;

/**
 * 实验题目类型枚举
 */
@Getter
public enum ExperimentQuestionTypeEnum {

    CHOICE(1, "选择题"),
    FILL_BLANK(2, "填空题"),
    PROGRAMMING(3, "编程题"),
    SHORT_ANSWER(4, "简答题");

    private final Integer code;

    private final String description;

    ExperimentQuestionTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static ExperimentQuestionTypeEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ExperimentQuestionTypeEnum anEnum : ExperimentQuestionTypeEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }
}
