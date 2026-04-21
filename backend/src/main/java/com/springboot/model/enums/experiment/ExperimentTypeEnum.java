package com.springboot.model.enums.experiment;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 实验类型枚举
 */
@Getter
public enum ExperimentTypeEnum {

    PROGRAMMING_PRACTICE("编程实践", 1),
    DESIGN_IMPLEMENTATION("设计实现", 2),
    DATABASE("数据库", 3),
    FRONTEND_DEVELOPMENT("前端开发", 4),
    FRAMEWORK_LEARNING("框架学习", 5),
    COMPREHENSIVE("综合实验", 6);

    private final String text;
    private final Integer value;

    ExperimentTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public static String getTextByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (ExperimentTypeEnum typeEnum : values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum.getText();
            }
        }
        return null;
    }

    public static List<Integer> getAllValues() {
        List<Integer> values = new ArrayList<>();
        for (ExperimentTypeEnum typeEnum : values()) {
            values.add(typeEnum.getValue());
        }
        return values;
    }
}
