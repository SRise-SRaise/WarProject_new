package com.springboot.model.enums.experiment;

import lombok.Getter;

/**
 * 题目难度枚举
 */
@Getter
public enum ExperimentDifficultyEnum {

    EASY(1, "简单"),
    MEDIUM(2, "中等"),
    HARD(3, "困难");

    private final Integer code;

    private final String description;

    ExperimentDifficultyEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     */
    public static ExperimentDifficultyEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ExperimentDifficultyEnum anEnum : ExperimentDifficultyEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }
}
