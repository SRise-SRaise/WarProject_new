package com.springboot.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * docx实验文档解析工具
 * <p>
 * 文档模板格式说明：
 * <pre>
 * 一、实验基本信息
 * 【实验名称】实验一  Java基础语法练习
 * 【实验类型】1
 * 【实验要求】掌握Java基本语法、数据类型、控制结构等
 *
 * 二、题目列表
 * ## 题目1
 * 【题目名称】变量声明与赋值
 * 【题目类型】1
 * 【题目内容】请声明一个int类型的变量并赋值为100。
 * 【选项】A. int a = 100;  B. String a = 100;  C. int a == 100;  D. var a = 100;
 * 【标准答案】A
 * 【答案解析】int是Java中的整型关键字，用于声明整数类型变量。
 * 【难度】1
 * 【分值】10
 *
 * ## 题目2
 * 【题目名称】条件判断
 * 【题目类型】4
 * 【题目内容】简述if-else语句的执行流程。
 * 【标准答案】先判断条件，条件为true则执行if块，否则执行else块。
 * 【答案解析】if-else是最基本的条件控制语句。
 * 【难度】2
 * 【分值】15
 *
 * ## 题目3
 * 【题目名称】编程练习-阶乘计算
 * 【题目类型】3
 * 【题目内容】编写一个方法，计算n的阶乘（n!）。
 * 【标准答案】
 * public static int factorial(int n) {
 *     if (n <= 1) return 1;
 *     return n * factorial(n - 1);
 * }
 * 【参考代码】// 可提供参考实现
 * 【难度】2
 * 【分值】20
 * </pre>
 */
public class DocxParserUtil {

    /**
     * 题目类型映射
     */
    private static final Map<String, Integer> QUESTION_TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> QUESTION_TYPE_NAME_MAP = new HashMap<>();

    static {
        QUESTION_TYPE_MAP.put("选择题", 1);
        QUESTION_TYPE_MAP.put("1", 1);
        QUESTION_TYPE_MAP.put("填空题", 2);
        QUESTION_TYPE_MAP.put("2", 2);
        QUESTION_TYPE_MAP.put("编程题", 3);
        QUESTION_TYPE_MAP.put("3", 3);
        QUESTION_TYPE_MAP.put("简答题", 4);
        QUESTION_TYPE_MAP.put("4", 4);
        QUESTION_TYPE_MAP.put("单选", 1);
        QUESTION_TYPE_MAP.put("多选", 1);
        QUESTION_TYPE_MAP.put("判断", 4);

        QUESTION_TYPE_NAME_MAP.put(1, "选择题");
        QUESTION_TYPE_NAME_MAP.put(2, "填空题");
        QUESTION_TYPE_NAME_MAP.put(3, "编程题");
        QUESTION_TYPE_NAME_MAP.put(4, "简答题");
    }

    /**
     * 难度映射
     */
    private static final Map<String, Integer> DIFFICULTY_MAP = new HashMap<>();

    static {
        DIFFICULTY_MAP.put("简单", 1);
        DIFFICULTY_MAP.put("1", 1);
        DIFFICULTY_MAP.put("中等", 2);
        DIFFICULTY_MAP.put("2", 2);
        DIFFICULTY_MAP.put("困难", 3);
        DIFFICULTY_MAP.put("3", 3);
        DIFFICULTY_MAP.put("难", 3);
    }

    /**
     * 解析结果内部类
     */
    public static class ParseResult {
        private String experimentName;
        private String requirement;
        private Integer categoryId;
        private List<QuestionInfo> questions;

        public String getExperimentName() { return experimentName; }
        public void setExperimentName(String experimentName) { this.experimentName = experimentName; }
        public String getRequirement() { return requirement; }
        public void setRequirement(String requirement) { this.requirement = requirement; }
        public Integer getCategoryId() { return categoryId; }
        public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
        public List<QuestionInfo> getQuestions() { return questions; }
        public void setQuestions(List<QuestionInfo> questions) { this.questions = questions; }
    }

    /**
     * 题目信息内部类
     */
    public static class QuestionInfo {
        private String questionName;
        private String questionContent;
        private Integer questionType;
        private String questionTypeName;
        private Integer difficulty;
        private Integer score;
        private String standardAnswer;
        private String answerAnalysis;
        private String referenceCode;
        private String options;

        public String getQuestionName() { return questionName; }
        public void setQuestionName(String questionName) { this.questionName = questionName; }
        public String getQuestionContent() { return questionContent; }
        public void setQuestionContent(String questionContent) { this.questionContent = questionContent; }
        public Integer getQuestionType() { return questionType; }
        public void setQuestionType(Integer questionType) { this.questionType = questionType; }
        public String getQuestionTypeName() { return questionTypeName; }
        public void setQuestionTypeName(String questionTypeName) { this.questionTypeName = questionTypeName; }
        public Integer getDifficulty() { return difficulty; }
        public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        public String getStandardAnswer() { return standardAnswer; }
        public void setStandardAnswer(String standardAnswer) { this.standardAnswer = standardAnswer; }
        public String getAnswerAnalysis() { return answerAnalysis; }
        public void setAnswerAnalysis(String answerAnalysis) { this.answerAnalysis = answerAnalysis; }
        public String getReferenceCode() { return referenceCode; }
        public void setReferenceCode(String referenceCode) { this.referenceCode = referenceCode; }
        public String getOptions() { return options; }
        public void setOptions(String options) { this.options = options; }
    }

    /**
     * 从MultipartFile解析docx文档
     */
    public static ParseResult parseFromMultipartFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".docx")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持.docx格式的文件");
        }
        try (InputStream is = file.getInputStream()) {
            return parseFromInputStream(is);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取文件失败: " + e.getMessage());
        }
    }

    /**
     * 从URL下载并解析docx文档
     */
    public static ParseResult parseFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件URL不能为空");
        }
        try {
            byte[] bytes = HttpRequest.get(fileUrl)
                    .timeout(30000)
                    .execute()
                    .bodyBytes();
            try (InputStream is = new java.io.ByteArrayInputStream(bytes)) {
                return parseFromInputStream(is);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载或解析文件失败: " + e.getMessage());
        }
    }

    /**
     * 从本地文件路径解析docx文档
     */
    public static ParseResult parseFromFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件路径不能为空");
        }
        try (InputStream is = new java.io.FileInputStream(filePath)) {
            return parseFromInputStream(is);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取文件失败: " + e.getMessage());
        }
    }

    /**
     * 核心解析方法
     */
    private static ParseResult parseFromInputStream(InputStream is) {
        ParseResult result = new ParseResult();
        List<QuestionInfo> questions = new ArrayList<>();

        try (XWPFDocument document = new XWPFDocument(is)) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            // 收集所有文本内容
            List<String> allText = new ArrayList<>();
            for (XWPFParagraph p : paragraphs) {
                allText.add(p.getText());
            }

            // 解析基本信息
            parseBasicInfo(allText, result);

            // 解析题目列表
            parseQuestions(allText, questions);

            result.setQuestions(questions);

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解析docx文档失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 解析实验基本信息
     */
    private static void parseBasicInfo(List<String> allText, ParseResult result) {
        StringBuilder requirementBuilder = new StringBuilder();
        String experimentName = null;
        Integer categoryId = null;

        for (String line : allText) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // 解析实验名称
            if (trimmed.startsWith("【实验名称】") || trimmed.startsWith("实验名称：") || trimmed.startsWith("实验名称:")) {
                experimentName = extractValue(trimmed, "实验名称");
            }
            // 解析实验类型/分类ID
            else if (trimmed.startsWith("【实验类型】") || trimmed.startsWith("实验类型：") || trimmed.startsWith("实验类型:")) {
                String value = extractValue(trimmed, "实验类型");
                if (value != null) {
                    try {
                        categoryId = Integer.parseInt(value.trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            // 解析实验要求
            else if (trimmed.startsWith("【实验要求】") || trimmed.startsWith("实验要求：") || trimmed.startsWith("实验要求:")) {
                String value = extractValue(trimmed, "实验要求");
                if (value != null && !value.trim().isEmpty()) {
                    requirementBuilder.append(value.trim());
                }
            }
            // 尝试解析文档标题作为实验名称（如果前面没有找到）
            else if (experimentName == null && trimmed.length() > 1 && trimmed.length() < 100
                    && !trimmed.startsWith("#") && !trimmed.startsWith("##")
                    && !isQuestionHeader(trimmed) && !isOptionLine(trimmed)) {
                // 如果这一行看起来像标题（非标记行），可能是实验名称
                if (requirementBuilder.length() == 0 && !trimmed.contains("【")
                        && !trimmed.contains("】") && !containsMultipleLabels(trimmed)) {
                    // 可能是文档主标题
                    if (experimentName == null && trimmed.length() > 2) {
                        experimentName = trimmed;
                    }
                }
            }
        }

        // 收集【实验要求】后面的连续非空行作为实验要求内容
        for (int i = 0; i < allText.size(); i++) {
            String line = allText.get(i).trim();
            if (line.startsWith("【实验要求】") || line.startsWith("实验要求：") || line.startsWith("实验要求:")) {
                // 获取冒号后面的内容
                String value = extractValue(line, "实验要求");
                if (value != null && !value.trim().isEmpty()) {
                    requirementBuilder.append(value.trim());
                }
                // 收集后续连续的非标记行作为实验要求的一部分
                for (int j = i + 1; j < allText.size(); j++) {
                    String nextLine = allText.get(j).trim();
                    if (nextLine.isEmpty()) break;
                    // 如果遇到题目标记，停止
                    if (isQuestionHeader(nextLine) || nextLine.startsWith("##")) break;
                    requirementBuilder.append("\n").append(nextLine);
                }
                break;
            }
        }

        result.setExperimentName(experimentName);
        result.setCategoryId(categoryId);
        result.setRequirement(requirementBuilder.length() > 0 ? requirementBuilder.toString().trim() : null);
    }

    /**
     * 判断是否是题目头部标记
     */
    private static boolean isQuestionHeader(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("##") || trimmed.matches("^[题Q]\\s*\\d+[.、:]?.*")
                || trimmed.matches("^\\d+[.、:、\\s].*");
    }

    /**
     * 判断是否是选项行
     */
    private static boolean isOptionLine(String line) {
        String trimmed = line.trim();
        return trimmed.matches("^[A-Da-d][.、:、\\s].*")
                || trimmed.matches("^\\([A-Da-d]\\).*")
                || trimmed.startsWith("【选项】") || trimmed.startsWith("选项：") || trimmed.startsWith("选项:");
    }

    /**
     * 判断是否包含多个标签
     */
    private static boolean containsMultipleLabels(String text) {
        int count = 0;
        for (String label : new String[]{"【实验名称", "【实验类型", "【实验要求", "【题目名称", "【题目类型",
                "【题目内容", "【标准答案", "【答案解析", "【难度", "【分值", "【参考代码", "【选项"}) {
            if (text.contains(label)) count++;
        }
        return count > 1;
    }

    /**
     * 解析题目列表
     */
    private static void parseQuestions(List<String> allText, List<QuestionInfo> questions) {
        int currentQuestionIndex = -1;
        QuestionInfo currentQuestion = null;

        for (int i = 0; i < allText.size(); i++) {
            String line = allText.get(i);
            String trimmed = line.trim();

            if (trimmed.isEmpty()) continue;

            // 检测题目开始标记
            boolean isNewQuestion = false;

            // 格式1: ## 题目1 或 ## 题目 1
            if (trimmed.matches("^##\\s*[题Q]\\s*\\d+.*") || trimmed.matches("^##\\s*\\d+[.、:]?.*")) {
                isNewQuestion = true;
            }
            // 格式2: 题目1: 或 题目 1: 或 Q1:
            else if (trimmed.matches("^[题Q]\\s*\\d+[.、:]?.*") && !trimmed.startsWith("【")) {
                isNewQuestion = true;
            }
            // 格式3: 一、二、三 开头的大题
            else if (trimmed.matches("^[一二三四五六七八九十]+[、.、:].*")) {
                isNewQuestion = true;
            }

            if (isNewQuestion) {
                // 保存上一个题目
                if (currentQuestion != null) {
                    questions.add(currentQuestion);
                }
                // 开始新题目
                currentQuestion = new QuestionInfo();
                currentQuestionIndex++;
                // 尝试从标题提取题目名称
                String qName = trimmed.replaceFirst("^##\\s*", "").replaceFirst("^[题Q]\\s*\\d+[.、:]?\\s*", "")
                        .replaceFirst("^[一二三四五六七八九十]+[、.、:、\\s]*", "");
                if (!qName.trim().isEmpty()) {
                    currentQuestion.setQuestionName(qName.trim());
                }
                continue;
            }

            if (currentQuestion == null) {
                // 还没遇到题目标记，跳过
                continue;
            }

            // 解析题目属性
            parseQuestionProperty(trimmed, currentQuestion);
        }

        // 保存最后一个题目
        if (currentQuestion != null) {
            questions.add(currentQuestion);
        }
    }

    /**
     * 解析题目属性
     */
    private static void parseQuestionProperty(String line, QuestionInfo question) {
        String trimmed = line.trim();

        // 【题目名称】
        if (trimmed.startsWith("【题目名称】")) {
            String value = extractValue(trimmed, "题目名称");
            if (value != null) question.setQuestionName(value.trim());
        }
        // 【题目类型】
        else if (trimmed.startsWith("【题目类型】")) {
            String value = extractValue(trimmed, "题目类型");
            if (value != null) {
                Integer type = parseQuestionType(value.trim());
                question.setQuestionType(type);
                question.setQuestionTypeName(QUESTION_TYPE_NAME_MAP.get(type));
            }
        }
        // 【题目内容】
        else if (trimmed.startsWith("【题目内容】")) {
            String value = extractValue(trimmed, "题目内容");
            if (value != null) question.setQuestionContent(value.trim());
        }
        // 【选项】(选择题选项)
        else if (trimmed.startsWith("【选项】")) {
            String value = extractValue(trimmed, "选项");
            if (value != null) {
                // 格式化为JSON数组
                String jsonOptions = formatOptionsToJson(value.trim());
                question.setOptions(jsonOptions);
            }
        }
        // 【标准答案】
        else if (trimmed.startsWith("【标准答案】")) {
            String value = extractValue(trimmed, "标准答案");
            if (value != null) question.setStandardAnswer(value.trim());
        }
        // 【答案解析】
        else if (trimmed.startsWith("【答案解析】")) {
            String value = extractValue(trimmed, "答案解析");
            if (value != null) question.setAnswerAnalysis(value.trim());
        }
        // 【参考代码】
        else if (trimmed.startsWith("【参考代码】")) {
            String value = extractValue(trimmed, "参考代码");
            if (value != null) question.setReferenceCode(value.trim());
        }
        // 【难度】
        else if (trimmed.startsWith("【难度】")) {
            String value = extractValue(trimmed, "难度");
            if (value != null) {
                Integer difficulty = parseDifficulty(value.trim());
                question.setDifficulty(difficulty);
            }
        }
        // 【分值】
        else if (trimmed.startsWith("【分值】") || trimmed.startsWith("【分数】")) {
            String value = extractValue(trimmed, "分值");
            if (value == null) {
                value = extractValue(trimmed, "分数");
            }
            if (value != null) {
                try {
                    question.setScore(Integer.parseInt(value.trim().replaceAll("[^0-9]", "")));
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    /**
     * 从行中提取值（支持多种分隔符）
     */
    private static String extractValue(String line, String key) {
        // 尝试 【key】格式
        String pattern1 = "【" + key + "】";
        int idx1 = line.indexOf(pattern1);
        if (idx1 >= 0) {
            return line.substring(idx1 + pattern1.length()).trim();
        }

        // 尝试 key：格式
        String pattern2 = key + "：";
        int idx2 = line.indexOf(pattern2);
        if (idx2 >= 0) {
            return line.substring(idx2 + pattern2.length()).trim();
        }

        // 尝试 key: 格式
        String pattern3 = key + ":";
        int idx3 = line.indexOf(pattern3);
        if (idx3 >= 0) {
            return line.substring(idx3 + pattern3.length()).trim();
        }

        return null;
    }

    /**
     * 解析题目类型
     */
    private static Integer parseQuestionType(String value) {
        String trimmed = value.trim();
        if (QUESTION_TYPE_MAP.containsKey(trimmed)) {
            return QUESTION_TYPE_MAP.get(trimmed);
        }
        // 尝试模糊匹配
        for (Map.Entry<String, Integer> entry : QUESTION_TYPE_MAP.entrySet()) {
            if (trimmed.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return 1; // 默认选择题
    }

    /**
     * 解析难度
     */
    private static Integer parseDifficulty(String value) {
        String trimmed = value.trim();
        if (DIFFICULTY_MAP.containsKey(trimmed)) {
            return DIFFICULTY_MAP.get(trimmed);
        }
        // 尝试模糊匹配
        for (Map.Entry<String, Integer> entry : DIFFICULTY_MAP.entrySet()) {
            if (trimmed.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return 2; // 默认中等
    }

    /**
     * 将选项格式化为JSON数组
     * 支持格式：
     * A. 选项1  B. 选项2  C. 选项3  D. 选项4
     * (A) 选项1  (B) 选项2
     * A.选项1;B.选项2;C.选项3;D.选项4
     */
    private static String formatOptionsToJson(String optionsText) {
        List<Map<String, String>> options = new ArrayList<>();
        String[] lines = optionsText.split("\n");

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // 匹配 A. xxx 或 (A) xxx 格式
            Pattern pattern = Pattern.compile("^([A-Da-d])[.、:、\\s、]?\\s*(.+)$");
            Matcher matcher = pattern.matcher(trimmed);
            if (matcher.matches()) {
                Map<String, String> option = new HashMap<>();
                option.put("option", matcher.group(1).toUpperCase());
                option.put("content", matcher.group(2).trim());
                options.add(option);
            } else {
                // 尝试验证格式 A.xxx
                Pattern p2 = Pattern.compile("^([A-Da-d])\\.(.+)$");
                Matcher m2 = p2.matcher(trimmed);
                if (m2.matches()) {
                    Map<String, String> option = new HashMap<>();
                    option.put("option", m2.group(1).toUpperCase());
                    option.put("content", m2.group(2).trim());
                    options.add(option);
                }
            }
        }

        if (options.isEmpty()) {
            // 如果没有匹配到标准格式，直接返回原文
            return optionsText;
        }

        // 转换为JSON
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < options.size(); i++) {
            Map<String, String> opt = options.get(i);
            if (i > 0) json.append(",");
            json.append("{\"option\":\"").append(opt.get("option"))
                    .append("\",\"content\":\"").append(escapeJson(opt.get("content"))).append("\"}");
        }
        json.append("]");
        return json.toString();
    }

    /**
     * JSON字符串转义
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 获取题目类型名称
     */
    public static String getQuestionTypeName(Integer type) {
        return QUESTION_TYPE_NAME_MAP.getOrDefault(type, "未知");
    }
}
