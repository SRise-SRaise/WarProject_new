package com.springboot.utils;

import cn.hutool.http.HttpRequest;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * docx实验文档解析工具
 *
 * <p>采用"静态入口 + 单例委托"模式：
 * <ul>
 *   <li>所有公开方法保留 {@code public static} 签名，向后兼容现有调用方；</li>
 *   <li>内部将调用转发至 {@link #getInstance()} 返回的单例实例，实例持有映射表等可变状态。</li>
 * </ul>
 * 单例通过 Bill Pugh Holder 模式实现——借助 JVM 类加载机制保证线程安全且延迟初始化。</p>
 */
public class DocxParserUtil {

    // ==================== 单例基础设施 ====================

    private static final class Holder {
        private static final DocxParserUtil INSTANCE = new DocxParserUtil();
    }

    public static DocxParserUtil getInstance() {
        return Holder.INSTANCE;
    }

    private DocxParserUtil() {
        initMappings();
    }

    // ==================== 实例字段（映射数据，可通过 resetMappings 热重置） ====================

    private final Map<String, Integer> questionTypeMap = new HashMap<>();
    private final Map<Integer, String> questionTypeNameMap = new HashMap<>();
    private final Map<String, Integer> difficultyMap = new HashMap<>();

    private void initMappings() {
        questionTypeMap.put("选择题", 1); questionTypeMap.put("1", 1);
        questionTypeMap.put("填空题", 2); questionTypeMap.put("2", 2);
        questionTypeMap.put("编程题", 3); questionTypeMap.put("3", 3);
        questionTypeMap.put("简答题", 4); questionTypeMap.put("4", 4);
        questionTypeMap.put("单选", 1); questionTypeMap.put("多选", 1); questionTypeMap.put("判断", 4);

        questionTypeNameMap.put(1, "选择题"); questionTypeNameMap.put(2, "填空题");
        questionTypeNameMap.put(3, "编程题"); questionTypeNameMap.put(4, "简答题");

        difficultyMap.put("简单", 1); difficultyMap.put("1", 1);
        difficultyMap.put("中等", 2); difficultyMap.put("2", 2);
        difficultyMap.put("困难", 3); difficultyMap.put("3", 3); difficultyMap.put("难", 3);
    }

    public synchronized void resetMappings() {
        questionTypeMap.clear(); questionTypeNameMap.clear(); difficultyMap.clear();
        initMappings();
    }

    // ==================== 公开静态入口（向后兼容，内部委托给单例） ====================

    public static ParseResult parseFromMultipartFile(MultipartFile file) {
        return getInstance().doParseFromMultipartFile(file);
    }

    public static ParseResult parseFromUrl(String fileUrl) {
        return getInstance().doParseFromUrl(fileUrl);
    }

    public static ParseResult parseFromFilePath(String filePath) {
        return getInstance().doParseFromFilePath(filePath);
    }

    public static String getQuestionTypeName(Integer type) {
        return getInstance().doGetQuestionTypeName(type);
    }

    // ==================== 实例方法（内部实现） ====================

    public ParseResult doParseFromMultipartFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".docx")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持.docx格式的文件");
        }
        try (InputStream is = file.getInputStream()) {
            return doParseFromInputStream(is);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取文件失败: " + e.getMessage());
        }
    }

    public ParseResult doParseFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件URL不能为空");
        }
        try {
            byte[] bytes = HttpRequest.get(fileUrl).timeout(30000).execute().bodyBytes();
            try (InputStream is = new java.io.ByteArrayInputStream(bytes)) {
                return doParseFromInputStream(is);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载或解析文件失败: " + e.getMessage());
        }
    }

    public ParseResult doParseFromFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件路径不能为空");
        }
        try (InputStream is = new java.io.FileInputStream(filePath)) {
            return doParseFromInputStream(is);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取文件失败: " + e.getMessage());
        }
    }

    public String doGetQuestionTypeName(Integer type) {
        return questionTypeNameMap.getOrDefault(type, "未知");
    }

    // ==================== 内部解析逻辑 ====================

    private ParseResult doParseFromInputStream(InputStream is) {
        ParseResult result = new ParseResult();
        List<QuestionInfo> questions = new ArrayList<>();
        try (XWPFDocument document = new XWPFDocument(is)) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            List<String> allText = new ArrayList<>();
            for (XWPFParagraph p : paragraphs) { allText.add(p.getText()); }
            doParseBasicInfo(allText, result);
            doParseQuestions(allText, questions);
            result.setQuestions(questions);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解析docx文档失败: " + e.getMessage());
        }
        return result;
    }

    private void doParseBasicInfo(List<String> allText, ParseResult result) {
        StringBuilder requirementBuilder = new StringBuilder();
        String experimentName = null;
        Integer categoryId = null;

        for (String line : allText) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (trimmed.startsWith("【实验名称】") || trimmed.startsWith("实验名称：") || trimmed.startsWith("实验名称:")) {
                experimentName = doExtractValue(trimmed, "实验名称");
            } else if (trimmed.startsWith("【实验类型】") || trimmed.startsWith("实验类型：") || trimmed.startsWith("实验类型:")) {
                String value = doExtractValue(trimmed, "实验类型");
                if (value != null) { try { categoryId = Integer.parseInt(value.trim()); } catch (NumberFormatException ignored) {} }
            } else if (trimmed.startsWith("【实验要求】") || trimmed.startsWith("实验要求：") || trimmed.startsWith("实验要求:")) {
                String value = doExtractValue(trimmed, "实验要求");
                if (value != null && !value.trim().isEmpty()) { requirementBuilder.append(value.trim()); }
            } else if (experimentName == null && trimmed.length() > 1 && trimmed.length() < 100
                    && !trimmed.startsWith("#") && !trimmed.startsWith("##")
                    && !isQuestionHeader(trimmed) && !isOptionLine(trimmed)) {
                if (requirementBuilder.length() == 0 && !trimmed.contains("【")
                        && !trimmed.contains("】") && !containsMultipleLabels(trimmed) && trimmed.length() > 2) {
                    experimentName = trimmed;
                }
            }
        }

        for (int i = 0; i < allText.size(); i++) {
            String line = allText.get(i).trim();
            if (line.startsWith("【实验要求】") || line.startsWith("实验要求：") || line.startsWith("实验要求:")) {
                String value = doExtractValue(line, "实验要求");
                if (value != null && !value.trim().isEmpty()) { requirementBuilder.append(value.trim()); }
                for (int j = i + 1; j < allText.size(); j++) {
                    String nextLine = allText.get(j).trim();
                    if (nextLine.isEmpty()) break;
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

    private void doParseQuestions(List<String> allText, List<QuestionInfo> questions) {
        QuestionInfo currentQuestion = null;
        for (String line : allText) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            boolean isNewQuestion = false;
            // 严格匹配题目分隔格式，避免将附录说明（如"二、题目列表"、"三、题目类型参考"）误识别为题目
            // 只匹配 ## 题目N 格式（模板标准格式）
            if (trimmed.matches("^##\\s*题目\\s*\\d+.*") || trimmed.matches("^##\\s*[题Q]\\s*\\d+.*") || trimmed.matches("^##\\s*\\d+[.、:]?\\s*$")) {
                isNewQuestion = true;
            } else if (trimmed.matches("^题目\\s*\\d+\\s*$") || trimmed.matches("^[题Q]\\s*\\d+\\s*$")) {
                // 仅当整行只有"题目N"或"题 N"时才识别
                isNewQuestion = true;
            }
            // 注意：不再匹配 "一、题目..."、"二、题目列表" 等格式，避免将章节标题误判为题目

            if (isNewQuestion) {
                if (currentQuestion != null) { questions.add(currentQuestion); }
                currentQuestion = new QuestionInfo();
                // 提取题目名称：移除 ##、题目N、题 N 等前缀
                String qName = trimmed.replaceFirst("^##\\s*", "")
                        .replaceFirst("^题目\\d+[.、:]?\\s*", "")
                        .replaceFirst("^[题Q]\\s*\\d+[.、:]?\\s*", "")
                        .replaceFirst("^\\d+[.、:]?\\s*", "")
                        .replaceFirst("^[一二三四五六七八九十]+[、.、:、\\s]*", "");
                if (!qName.trim().isEmpty()) { currentQuestion.setQuestionName(qName.trim()); }
                continue;
            }

            if (currentQuestion == null) continue;
            doParseQuestionProperty(trimmed, currentQuestion);
        }
        if (currentQuestion != null) { questions.add(currentQuestion); }
    }

    private void doParseQuestionProperty(String line, QuestionInfo question) {
        String trimmed = line.trim();
        if (trimmed.startsWith("【题目名称】")) {
            String v = doExtractValue(trimmed, "题目名称");
            if (v != null) question.setQuestionName(v.trim());
        } else if (trimmed.startsWith("【题目类型】")) {
            String v = doExtractValue(trimmed, "题目类型");
            if (v != null) { Integer t = doParseQuestionType(v.trim()); question.setQuestionType(t); question.setQuestionTypeName(questionTypeNameMap.get(t)); }
        } else if (trimmed.startsWith("【题目内容】")) {
            String v = doExtractValue(trimmed, "题目内容");
            if (v != null) question.setQuestionContent(v.trim());
        } else if (trimmed.startsWith("【选项】")) {
            String v = doExtractValue(trimmed, "选项");
            if (v != null) question.setOptions(doFormatOptionsToJson(v.trim()));
        } else if (trimmed.startsWith("【标准答案】")) {
            String v = doExtractValue(trimmed, "标准答案");
            if (v != null) question.setStandardAnswer(v.trim());
        } else if (trimmed.startsWith("【答案解析】")) {
            String v = doExtractValue(trimmed, "答案解析");
            if (v != null) question.setAnswerAnalysis(v.trim());
        } else if (trimmed.startsWith("【参考代码】")) {
            String v = doExtractValue(trimmed, "参考代码");
            if (v != null) question.setReferenceCode(v.trim());
        } else if (trimmed.startsWith("【难度】")) {
            String v = doExtractValue(trimmed, "难度");
            if (v != null) question.setDifficulty(doParseDifficulty(v.trim()));
        } else if (trimmed.startsWith("【分值】") || trimmed.startsWith("【分数】")) {
            String v = doExtractValue(trimmed, "分值");
            if (v == null) v = doExtractValue(trimmed, "分数");
            if (v != null) { try { question.setScore(Integer.parseInt(v.trim().replaceAll("[^0-9]", ""))); } catch (NumberFormatException ignored) {} }
        }
    }

    private String doExtractValue(String line, String key) {
        String p1 = "【" + key + "】"; int i1 = line.indexOf(p1);
        if (i1 >= 0) return line.substring(i1 + p1.length()).trim();
        String p2 = key + "："; int i2 = line.indexOf(p2);
        if (i2 >= 0) return line.substring(i2 + p2.length()).trim();
        String p3 = key + ":"; int i3 = line.indexOf(p3);
        if (i3 >= 0) return line.substring(i3 + p3.length()).trim();
        return null;
    }

    private Integer doParseQuestionType(String value) {
        String t = value.trim();
        Integer mapped = questionTypeMap.get(t);
        if (mapped != null) return mapped;
        for (Map.Entry<String, Integer> e : questionTypeMap.entrySet()) {
            if (t.contains(e.getKey())) return e.getValue();
        }
        return 1;
    }

    private Integer doParseDifficulty(String value) {
        String t = value.trim();
        Integer mapped = difficultyMap.get(t);
        if (mapped != null) return mapped;
        for (Map.Entry<String, Integer> e : difficultyMap.entrySet()) {
            if (t.contains(e.getKey())) return e.getValue();
        }
        return 2;
    }

    private static boolean isQuestionHeader(String line) {
        String t = line.trim();
        return t.startsWith("##") || t.matches("^[题Q]\\s*\\d+[.、:]?.*") || t.matches("^\\d+[.、:、\\s].*");
    }

    private static boolean isOptionLine(String line) {
        String t = line.trim();
        return t.matches("^[A-Da-d][.、:、\\s].*") || t.matches("^\\([A-Da-d]\\).*")
                || t.startsWith("【选项】") || t.startsWith("选项：") || t.startsWith("选项:");
    }

    private static boolean containsMultipleLabels(String text) {
        int count = 0;
        for (String label : new String[]{"【实验名称", "【实验类型", "【实验要求", "【题目名称", "【题目类型",
                "【题目内容", "【标准答案", "【答案解析", "【难度", "【分值", "【参考代码", "【选项"}) {
            if (text.contains(label)) count++;
        }
        return count > 1;
    }

    private String doFormatOptionsToJson(String optionsText) {
        List<Map<String, String>> options = new ArrayList<>();
        for (String line : optionsText.split("\n")) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            Matcher m = Pattern.compile("^([A-Da-d])[.、:、\\s、]?\\s*(.+)$").matcher(t);
            if (m.matches()) {
                Map<String, String> opt = new HashMap<>();
                opt.put("option", m.group(1).toUpperCase()); opt.put("content", m.group(2).trim());
                options.add(opt);
            } else {
                Matcher m2 = Pattern.compile("^([A-Da-d])\\.(.+)$").matcher(t);
                if (m2.matches()) {
                    Map<String, String> opt = new HashMap<>();
                    opt.put("option", m2.group(1).toUpperCase()); opt.put("content", m2.group(2).trim());
                    options.add(opt);
                }
            }
        }
        if (options.isEmpty()) return optionsText;
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < options.size(); i++) {
            Map<String, String> opt = options.get(i);
            if (i > 0) json.append(",");
            json.append("{\"option\":\"").append(opt.get("option"))
                    .append("\",\"content\":\"").append(doEscapeJson(opt.get("content"))).append("\"}");
        }
        json.append("]");
        return json.toString();
    }

    private static String doEscapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    // ==================== 数据类 ====================

    public static class ParseResult {
        private String experimentName; private String requirement; private Integer categoryId;
        private List<QuestionInfo> questions;
        public String getExperimentName() { return experimentName; }
        public void setExperimentName(String n) { this.experimentName = n; }
        public String getRequirement() { return requirement; }
        public void setRequirement(String r) { this.requirement = r; }
        public Integer getCategoryId() { return categoryId; }
        public void setCategoryId(Integer c) { this.categoryId = c; }
        public List<QuestionInfo> getQuestions() { return questions; }
        public void setQuestions(List<QuestionInfo> q) { this.questions = q; }
    }

    public static class QuestionInfo {
        private String questionName; private String questionContent; private Integer questionType;
        private String questionTypeName; private Integer difficulty; private Integer score;
        private String standardAnswer; private String answerAnalysis; private String referenceCode;
        private String options;
        public String getQuestionName() { return questionName; }
        public void setQuestionName(String n) { this.questionName = n; }
        public String getQuestionContent() { return questionContent; }
        public void setQuestionContent(String c) { this.questionContent = c; }
        public Integer getQuestionType() { return questionType; }
        public void setQuestionType(Integer t) { this.questionType = t; }
        public String getQuestionTypeName() { return questionTypeName; }
        public void setQuestionTypeName(String n) { this.questionTypeName = n; }
        public Integer getDifficulty() { return difficulty; }
        public void setDifficulty(Integer d) { this.difficulty = d; }
        public Integer getScore() { return score; }
        public void setScore(Integer s) { this.score = s; }
        public String getStandardAnswer() { return standardAnswer; }
        public void setStandardAnswer(String a) { this.standardAnswer = a; }
        public String getAnswerAnalysis() { return answerAnalysis; }
        public void setAnswerAnalysis(String a) { this.answerAnalysis = a; }
        public String getReferenceCode() { return referenceCode; }
        public void setReferenceCode(String c) { this.referenceCode = c; }
        public String getOptions() { return options; }
        public void setOptions(String o) { this.options = o; }
    }
}
