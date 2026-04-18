package com.springboot.utils;

import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 实验文档模板生成工具
 * 动态生成符合解析格式的docx模板文件
 */
public class DocxTemplateGenerator {

    /**
     * 生成实验文档模板的字节数组
     */
    public static byte[] generateTemplate() throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 1. 添加标题
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("实验指导书模板");
            titleRun.setBold(true);
            titleRun.setFontSize(20);

            // 2. 添加空行
            document.createParagraph();

            // 3. 添加实验基本信息区域标题
            XWPFParagraph sectionTitle = document.createParagraph();
            XWPFRun sectionRun = sectionTitle.createRun();
            sectionRun.setText("一、实验基本信息");
            sectionRun.setBold(true);
            sectionRun.setFontSize(14);

            // 4. 添加实验名称
            addLabelLine(document, "【实验名称】", "请在此处填写实验名称，例如：Java基础语法练习");

            // 5. 添加实验类型
            addLabelLine(document, "【实验类型】", "请填写实验类型编号：1-基础实验，2-综合实验，3-设计实验");
            document.createParagraph().setAlignment(ParagraphAlignment.LEFT);

            // 6. 添加实验要求
            XWPFParagraph reqTitle = document.createParagraph();
            XWPFRun reqRun = reqTitle.createRun();
            reqRun.setText("【实验要求】");
            reqRun.setBold(true);
            XWPFRun reqContent = reqTitle.createRun();
            reqContent.setText("请在此处填写实验要求描述，说明本次实验的学习目标和掌握内容。");

            document.createParagraph();

            // 7. 添加题目列表区域标题
            XWPFParagraph questionSectionTitle = document.createParagraph();
            XWPFRun qstRun = questionSectionTitle.createRun();
            qstRun.setText("二、题目列表");
            qstRun.setBold(true);
            qstRun.setFontSize(14);

            // 8. 添加说明
            XWPFParagraph note = document.createParagraph();
            XWPFRun noteRun = note.createRun();
            noteRun.setText("提示：每道题目使用「## 题目N」开头，题目属性使用【】标记。题目类型：1=选择题，2=填空题，3=编程题，4=简答题");
            noteRun.setItalic(true);

            document.createParagraph();

            // 9. 添加示例题目1 - 选择题
            addQuestionHeader(document, "1");
            addLabelLine(document, "【题目名称】", "示例选择题");
            addLabelLine(document, "【题目类型】", "1");
            addLabelLine(document, "【题目内容】", "以下哪个选项正确声明了一个整型变量？");
            addLabelLine(document, "【选项】", "A. int a = 100;    B. String a = 100;    C. int a == 100;    D. var a = 100;");
            addLabelLine(document, "【标准答案】", "A");
            addLabelLine(document, "【答案解析】", "int是Java中的整型关键字，用于声明整数类型的变量。");
            addLabelLine(document, "【难度】", "1");
            addLabelLine(document, "【分值】", "10");

            document.createParagraph();

            // 10. 添加示例题目2 - 填空题
            addQuestionHeader(document, "2");
            addLabelLine(document, "【题目名称】", "示例填空题");
            addLabelLine(document, "【题目类型】", "2");
            addLabelLine(document, "【题目内容】", "在Java中，用于输出内容的语句是_______。");
            addLabelLine(document, "【标准答案】", "System.out.println()");
            addLabelLine(document, "【答案解析】", "System.out.println()是Java标准输出语句，用于在控制台打印信息。");
            addLabelLine(document, "【难度】", "1");
            addLabelLine(document, "【分值】", "10");

            document.createParagraph();

            // 11. 添加示例题目3 - 编程题
            addQuestionHeader(document, "3");
            addLabelLine(document, "【题目名称】", "示例编程题");
            addLabelLine(document, "【题目类型】", "3");
            addLabelLine(document, "【题目内容】", "编写一个方法，计算并返回n的阶乘（n!）。要求：n为非负整数，当n<=1时返回1。");
            addLabelLine(document, "【标准答案】",
                    "public static int factorial(int n) {\n" +
                    "    if (n <= 1) return 1;\n" +
                    "    return n * factorial(n - 1);\n" +
                    "}");
            addLabelLine(document, "【参考代码】",
                    "// 参考实现（仅供参考）\n" +
                    "public static int factorial(int n) {\n" +
                    "    int result = 1;\n" +
                    "    for (int i = 2; i <= n; i++) {\n" +
                    "        result *= i;\n" +
                    "    }\n" +
                    "    return result;\n" +
                    "}");
            addLabelLine(document, "【难度】", "2");
            addLabelLine(document, "【分值】", "20");

            document.createParagraph();

            // 12. 添加示例题目4 - 简答题
            addQuestionHeader(document, "4");
            addLabelLine(document, "【题目名称】", "示例简答题");
            addLabelLine(document, "【题目类型】", "4");
            addLabelLine(document, "【题目内容】", "请简述面向对象编程的三大特性及其作用。");
            addLabelLine(document, "【标准答案】",
                    "1. 封装：将数据和操作封装在类中，隐藏内部实现细节，提供统一接口。\n" +
                    "2. 继承：子类继承父类的属性和方法，实现代码复用和多态。\n" +
                    "3. 多态：同一操作作用于不同对象产生不同结果，包括重载和重写。");
            addLabelLine(document, "【答案解析】", "面向对象的三大特性是封装、继承和多态，是面向对象编程的核心概念。");
            addLabelLine(document, "【难度】", "2");
            addLabelLine(document, "【分值】", "15");

            document.createParagraph();

            // 13. 添加附录说明
            XWPFParagraph appendixTitle = document.createParagraph();
            XWPFRun appRun = appendixTitle.createRun();
            appRun.setText("三、题目类型参考");
            appRun.setBold(true);
            appRun.setFontSize(14);

            addLabelLine(document, "选择题（类型码：1）", "适用于客观题，有明确选项的题目");
            addLabelLine(document, "填空题（类型码：2）", "适用于需要填写具体数值或表达式的题目");
            addLabelLine(document, "编程题（类型码：3）", "适用于需要编写代码的题目，可提供参考代码");
            addLabelLine(document, "简答题（类型码：4）", "适用于开放性题目，需要文字描述答案");

            document.createParagraph();

            XWPFParagraph diffTitle = document.createParagraph();
            XWPFRun diffRun = diffTitle.createRun();
            diffRun.setText("难度等级参考");
            diffRun.setBold(true);
            diffRun.setFontSize(14);

            addLabelLine(document, "简单（难度码：1）", "基础知识点的直接应用");
            addLabelLine(document, "中等（难度码：2）", "需要一定理解和综合应用");
            addLabelLine(document, "困难（难度码：3）", "需要深入理解和创新应用");

            // 保存到输出流
            document.write(out);
            return out.toByteArray();
        }
    }

    /**
     * 添加题目标题行
     */
    private static void addQuestionHeader(XWPFDocument document, String number) {
        XWPFParagraph para = document.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("## 题目" + number);
        run.setBold(true);
        run.setFontSize(12);
    }

    /**
     * 添加标签行（标签 + 内容）
     */
    private static void addLabelLine(XWPFDocument document, String label, String content) {
        XWPFParagraph para = document.createParagraph();
        XWPFRun labelRun = para.createRun();
        labelRun.setText(label);
        labelRun.setBold(true);
        labelRun.setFontSize(11);

        XWPFRun contentRun = para.createRun();
        contentRun.setText(content);
        contentRun.setFontSize(11);
    }
}
