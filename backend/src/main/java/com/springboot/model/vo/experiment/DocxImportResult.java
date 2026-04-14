package com.springboot.model.vo.experiment;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * docx文档导入结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocxImportResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 导入提示信息
     */
    private String message;

    /**
     * 创建的实验ID
     */
    private Long experimentId;

    /**
     * 实验名称
     */
    private String experimentName;

    /**
     * 成功导入的题目数量
     */
    private Integer successCount;

    /**
     * 失败的题目数量
     */
    private Integer failCount;

    /**
     * 失败的题目详情（key=题目序号，value=失败原因）
     */
    private Map<Integer, String> failDetails;

    /**
     * 导入的题目列表预览（仅包含基本信息）
     */
    private List<QuestionPreview> questionPreviews;

    /**
     * 题目预览信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionPreview implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 题目序号
         */
        private Integer index;

        /**
         * 题目名称
         */
        private String questionName;

        /**
         * 题目类型描述
         */
        private String questionTypeName;

        /**
         * 题目内容摘要（截取前50字符）
         */
        private String contentSummary;
    }
}
