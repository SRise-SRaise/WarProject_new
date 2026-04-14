package com.springboot.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DocxParserUtil 单元测试
 */
public class DocxParserUtilTest {

    @Test
    public void testQuestionTypeMapping() {
        // 测试题目类型解析
        assertEquals(1, DocxParserUtil.getQuestionTypeName(1));
        assertEquals("选择题", DocxParserUtil.getQuestionTypeName(1));
        assertEquals("填空题", DocxParserUtil.getQuestionTypeName(2));
        assertEquals("编程题", DocxParserUtil.getQuestionTypeName(3));
        assertEquals("简答题", DocxParserUtil.getQuestionTypeName(4));
        assertEquals("未知", DocxParserUtil.getQuestionTypeName(99));
    }

    @Test
    public void testParseFromFile() throws FileNotFoundException {
        // 测试从本地文件解析
        File file = ResourceUtils.getFile("classpath:test_experiment.docx");
        DocxParserUtil.ParseResult result = DocxParserUtil.parseFromFilePath(file.getAbsolutePath());

        assertNotNull(result);
        System.out.println("实验名称: " + result.getExperimentName());
        System.out.println("实验要求: " + result.getRequirement());
        System.out.println("题目数量: " + (result.getQuestions() != null ? result.getQuestions().size() : 0));

        if (result.getQuestions() != null) {
            for (int i = 0; i < result.getQuestions().size(); i++) {
                DocxParserUtil.QuestionInfo q = result.getQuestions().get(i);
                System.out.println("题目" + (i + 1) + ": " + q.getQuestionName());
                System.out.println("  类型: " + q.getQuestionTypeName());
                System.out.println("  内容: " + q.getQuestionContent());
                System.out.println("  答案: " + q.getStandardAnswer());
                System.out.println("  难度: " + q.getDifficulty());
                System.out.println("  分值: " + q.getScore());
                System.out.println();
            }
        }
    }

    @Test
    public void testParseFromUrl() {
        // 这个测试需要有效的docx文件URL
        // 如果没有真实URL，这个测试会被跳过
        String testUrl = "http://example.com/test.docx";
        try {
            DocxParserUtil.ParseResult result = DocxParserUtil.parseFromUrl(testUrl);
            // 如果URL有效，应该能解析
            assertNotNull(result);
        } catch (Exception e) {
            // 预期的异常（URL不可访问）
            assertTrue(e.getMessage().contains("下载或解析文件失败") || e.getMessage().contains("Failed to respond"));
        }
    }

    @Test
    public void testParseWithInvalidUrl() {
        // 测试空URL
        assertThrows(Exception.class, () -> {
            DocxParserUtil.parseFromUrl("");
        });

        assertThrows(Exception.class, () -> {
            DocxParserUtil.parseFromUrl(null);
        });
    }
}
