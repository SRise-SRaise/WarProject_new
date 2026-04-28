package com.springboot.controller.experiment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ResultUtils;
import com.springboot.model.entity.user.SysStudentLog;
import com.springboot.model.vo.experiment.ExperimentLogRiskVO;
import com.springboot.model.vo.experiment.ExperimentLogDetailVO;
import com.springboot.service.user.SysStudentLogService;
import com.springboot.utils.IpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 实验操作日志控制器
 * 用于记录学生实验操作日志和查询风险分析
 */
@RestController
@RequestMapping("/api/experiment/log")
@Slf4j
public class EduExperimentLogController {

    @Resource
    private SysStudentLogService sysStudentLogService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 实验日志操作类型常量 (100-199 为实验模块预留)
    public static final int ACTION_ENTER_EXPERIMENT = 100;      // 进入实验
    public static final int ACTION_SWITCH_QUESTION = 101;       // 切换题目
    public static final int ACTION_SAVE_ANSWER = 102;           // 保存答案
    public static final int ACTION_SUBMIT_EXPERIMENT = 103;     // 提交实验
    public static final int ACTION_LEAVE_PAGE = 104;            // 离开页面
    public static final int ACTION_RETURN_PAGE = 105;           // 返回页面
    public static final int ACTION_COPY = 106;                  // 复制操作
    public static final int ACTION_PASTE = 107;                 // 粘贴操作
    public static final int ACTION_FOCUS_LOST = 108;            // 失去焦点

    /**
     * 记录实验操作日志（学生端调用）
     */
    @PostMapping("/record")
    public BaseResponse<Boolean> recordLog(
            @RequestBody Map<String, Object> logData,
            HttpServletRequest request) {
        try {
            String account = (String) logData.get("account");
            Integer actionType = (Integer) logData.get("actionType");
            
            if (account == null || actionType == null) {
                return ResultUtils.error(400, "缺少必要参数");
            }

            SysStudentLog logEntity = new SysStudentLog();
            logEntity.setAccount(account);
            logEntity.setActionType(actionType);
            logEntity.setActionDetail(objectMapper.writeValueAsString(logData));
            logEntity.setOpTime(new Date());
            logEntity.setClientIp(IpUtils.getIpAddress(request));
            logEntity.setCreatedAt(new Date());

            sysStudentLogService.save(logEntity);
            log.debug("[ExperimentLog] 记录操作日志: account={}, actionType={}", account, actionType);
            return ResultUtils.success(true);
        } catch (Exception e) {
            log.error("[ExperimentLog] 记录日志失败:", e);
            return ResultUtils.error(500, "记录日志失败");
        }
    }

    /**
     * 批量记录实验操作日志（学生端调用，用于页面关闭前批量上传）
     */
    @PostMapping("/record/batch")
    public BaseResponse<Boolean> recordLogBatch(
            @RequestBody List<Map<String, Object>> logList,
            HttpServletRequest request) {
        try {
            String clientIp = IpUtils.getIpAddress(request);
            Date now = new Date();
            
            List<SysStudentLog> entities = new ArrayList<>();
            for (Map<String, Object> logData : logList) {
                String account = (String) logData.get("account");
                Integer actionType = ((Number) logData.get("actionType")).intValue();
                
                if (account == null || actionType == null) continue;

                SysStudentLog logEntity = new SysStudentLog();
                logEntity.setAccount(account);
                logEntity.setActionType(actionType);
                logEntity.setActionDetail(objectMapper.writeValueAsString(logData));
                logEntity.setOpTime(now);
                logEntity.setClientIp(clientIp);
                logEntity.setCreatedAt(now);
                entities.add(logEntity);
            }

            if (!entities.isEmpty()) {
                sysStudentLogService.saveBatch(entities);
                log.debug("[ExperimentLog] 批量记录日志: {} 条", entities.size());
            }
            return ResultUtils.success(true);
        } catch (Exception e) {
            log.error("[ExperimentLog] 批量记录日志失败:", e);
            return ResultUtils.error(500, "批量记录日志失败");
        }
    }

    /**
     * 获取实验学生风险列表（教师端调用）
     */
    @GetMapping("/risk/list")
    public BaseResponse<Page<ExperimentLogRiskVO>> getRiskList(
            @RequestParam Long experimentId,
            @RequestParam(required = false) String clazzNo,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            // 查询该实验的所有日志（actionType 100-108）
            // 注意：前端 experimentId 可能是字符串或数字，需要兼容两种格式
            QueryWrapper<SysStudentLog> qw = new QueryWrapper<>();
            qw.between("action_type", 100, 108)
              .and(wrapper -> wrapper
                  .like("action_detail", "\"experimentId\":\"" + experimentId + "\"")
                  .or()
                  .like("action_detail", "\"experimentId\":" + experimentId + ",")
                  .or()
                  .like("action_detail", "\"experimentId\":" + experimentId + "}"))
              .orderByAsc("op_time");
            
            List<SysStudentLog> allLogs = sysStudentLogService.list(qw);
            
            // 按学生分组分析
            Map<String, List<SysStudentLog>> studentLogs = allLogs.stream()
                    .collect(Collectors.groupingBy(SysStudentLog::getAccount));
            
            List<ExperimentLogRiskVO> riskList = new ArrayList<>();
            for (Map.Entry<String, List<SysStudentLog>> entry : studentLogs.entrySet()) {
                String account = entry.getKey();
                List<SysStudentLog> logs = entry.getValue();
                
                // 过滤班级
                if (clazzNo != null && !clazzNo.isEmpty()) {
                    boolean matchClass = logs.stream().anyMatch(log -> {
                        try {
                            Map<String, Object> detail = objectMapper.readValue(log.getActionDetail(), Map.class);
                            String logClazz = (String) detail.get("clazzNo");
                            return clazzNo.equals(logClazz);
                        } catch (Exception e) {
                            return false;
                        }
                    });
                    if (!matchClass) continue;
                }
                
                ExperimentLogRiskVO riskVO = analyzeStudentRisk(account, logs);
                riskList.add(riskVO);
            }
            
            // 按风险等级排序（高风险在前）
            riskList.sort((a, b) -> {
                int aLevel = "high".equals(a.getRiskLevel()) ? 3 : "medium".equals(a.getRiskLevel()) ? 2 : 1;
                int bLevel = "high".equals(b.getRiskLevel()) ? 3 : "medium".equals(b.getRiskLevel()) ? 2 : 1;
                return bLevel - aLevel;
            });
            
            // 手动分页
            int total = riskList.size();
            int fromIndex = (current - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, total);
            List<ExperimentLogRiskVO> pageList = fromIndex < total ? riskList.subList(fromIndex, toIndex) : new ArrayList<>();
            
            Page<ExperimentLogRiskVO> page = new Page<>(current, pageSize, total);
            page.setRecords(pageList);
            
            return ResultUtils.success(page);
        } catch (Exception e) {
            log.error("[ExperimentLog] 获取风险列表失败:", e);
            return ResultUtils.error(500, "获取风险列表失败");
        }
    }

    /**
     * 获取学生实验操作详情（教师端调用）
     */
    @GetMapping("/detail")
    public BaseResponse<ExperimentLogDetailVO> getStudentLogDetail(
            @RequestParam Long experimentId,
            @RequestParam String account) {
        try {
            QueryWrapper<SysStudentLog> qw = new QueryWrapper<>();
            qw.eq("account", account)
              .between("action_type", 100, 108)
              .and(wrapper -> wrapper
                  .like("action_detail", "\"experimentId\":\"" + experimentId + "\"")
                  .or()
                  .like("action_detail", "\"experimentId\":" + experimentId + ",")
                  .or()
                  .like("action_detail", "\"experimentId\":" + experimentId + "}"))
              .orderByAsc("op_time");
            
            List<SysStudentLog> logs = sysStudentLogService.list(qw);
            
            ExperimentLogDetailVO detailVO = new ExperimentLogDetailVO();
            detailVO.setAccount(account);
            detailVO.setExperimentId(experimentId);
            
            List<ExperimentLogDetailVO.LogItem> logItems = new ArrayList<>();
            for (SysStudentLog log : logs) {
                try {
                    Map<String, Object> detail = objectMapper.readValue(log.getActionDetail(), Map.class);
                    ExperimentLogDetailVO.LogItem item = new ExperimentLogDetailVO.LogItem();
                    item.setActionType(log.getActionType());
                    item.setActionName(getActionName(log.getActionType()));
                    item.setOpTime(log.getOpTime());
                    item.setClientIp(log.getClientIp());
                    // questionId 可能是字符串或数字
                    Object qidObj = detail.get("questionId");
                    if (qidObj != null) {
                        if (qidObj instanceof Number) {
                            item.setQuestionId(((Number) qidObj).longValue());
                        } else if (qidObj instanceof String && !((String) qidObj).isEmpty()) {
                            try {
                                item.setQuestionId(Long.parseLong((String) qidObj));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                    item.setQuestionName((String) detail.get("questionName"));
                    item.setExtra(detail);
                    logItems.add(item);
                } catch (Exception e) {
                    log.warn("[ExperimentLog] 解析日志详情失败: {}", e.getMessage());
                }
            }
            detailVO.setLogs(logItems);
            
            // 计算风险分析
            ExperimentLogRiskVO riskVO = analyzeStudentRisk(account, logs);
            detailVO.setRiskLevel(riskVO.getRiskLevel());
            detailVO.setRiskReasons(riskVO.getRiskReasons());
            detailVO.setTotalDuration(riskVO.getTotalDuration());
            detailVO.setQuestionCount(riskVO.getQuestionCount());
            detailVO.setAvgInterval(riskVO.getAvgInterval());
            detailVO.setMinInterval(riskVO.getMinInterval());
            detailVO.setRapidAnswerCount(riskVO.getRapidAnswerCount());
            detailVO.setPasteCount(riskVO.getPasteCount());
            
            return ResultUtils.success(detailVO);
        } catch (Exception e) {
            log.error("[ExperimentLog] 获取日志详情失败:", e);
            return ResultUtils.error(500, "获取日志详情失败");
        }
    }

    /**
     * 分析学生风险等级
     */
    private ExperimentLogRiskVO analyzeStudentRisk(String account, List<SysStudentLog> logs) {
        ExperimentLogRiskVO vo = new ExperimentLogRiskVO();
        vo.setAccount(account);
        vo.setLogCount(logs.size());
        
        List<String> riskReasons = new ArrayList<>();
        
        // 提取学生名称和班级
        for (SysStudentLog log : logs) {
            try {
                Map<String, Object> detail = objectMapper.readValue(log.getActionDetail(), Map.class);
                if (vo.getStudentName() == null && detail.get("studentName") != null) {
                    vo.setStudentName((String) detail.get("studentName"));
                }
                if (vo.getClazzNo() == null && detail.get("clazzNo") != null) {
                    vo.setClazzNo((String) detail.get("clazzNo"));
                }
            } catch (Exception ignored) {}
        }
        
        // 找到进入实验和提交实验的时间
        Date enterTime = null;
        Date submitTime = null;
        int questionCount = 0;
        int pasteCount = 0;
        List<Long> answerIntervals = new ArrayList<>();
        Date lastAnswerTime = null;
        
        for (SysStudentLog log : logs) {
            if (log.getActionType() == ACTION_ENTER_EXPERIMENT && enterTime == null) {
                enterTime = log.getOpTime();
            }
            if (log.getActionType() == ACTION_SUBMIT_EXPERIMENT) {
                submitTime = log.getOpTime();
            }
            if (log.getActionType() == ACTION_SAVE_ANSWER) {
                questionCount++;
                if (lastAnswerTime != null) {
                    long interval = (log.getOpTime().getTime() - lastAnswerTime.getTime()) / 1000;
                    answerIntervals.add(interval);
                }
                lastAnswerTime = log.getOpTime();
            }
            if (log.getActionType() == ACTION_PASTE) {
                pasteCount++;
            }
        }
        
        // 计算总时长
        long totalDuration = 0;
        if (enterTime != null && submitTime != null) {
            totalDuration = (submitTime.getTime() - enterTime.getTime()) / 1000;
        } else if (enterTime != null && !logs.isEmpty()) {
            totalDuration = (logs.get(logs.size() - 1).getOpTime().getTime() - enterTime.getTime()) / 1000;
        }
        
        // 计算平均间隔和最短间隔
        long avgInterval = 0;
        long minInterval = Long.MAX_VALUE;
        int rapidCount = 0;
        if (!answerIntervals.isEmpty()) {
            long sum = 0;
            for (Long interval : answerIntervals) {
                sum += interval;
                if (interval < minInterval) minInterval = interval;
                if (interval < 10) rapidCount++;
            }
            avgInterval = sum / answerIntervals.size();
        }
        if (minInterval == Long.MAX_VALUE) minInterval = 0;
        
        vo.setTotalDuration(totalDuration);
        vo.setQuestionCount(questionCount);
        vo.setAvgInterval(avgInterval);
        vo.setMinInterval(minInterval);
        vo.setRapidAnswerCount(rapidCount);
        vo.setPasteCount(pasteCount);
        
        // 风险判定
        String riskLevel = "low";
        
        // 高风险条件
        if (questionCount > 0 && totalDuration < questionCount * 30) {
            riskLevel = "high";
            riskReasons.add("总用时过短（平均每题少于30秒）");
        }
        if (rapidCount >= 3) {
            riskLevel = "high";
            riskReasons.add("连续" + rapidCount + "题答题间隔小于10秒");
        }
        if (minInterval > 0 && minInterval < 5 && questionCount > 1) {
            riskLevel = "high";
            riskReasons.add("最短答题间隔仅" + minInterval + "秒");
        }
        
        // 中风险条件（仅当不是高风险时）
        if (!"high".equals(riskLevel)) {
            if (questionCount > 0 && totalDuration < questionCount * 60) {
                riskLevel = "medium";
                riskReasons.add("总用时偏短（平均每题少于60秒）");
            }
            if (avgInterval > 0 && avgInterval < 30) {
                riskLevel = "medium";
                riskReasons.add("平均答题间隔偏短（" + avgInterval + "秒）");
            }
            if (pasteCount > 3) {
                riskLevel = "medium";
                riskReasons.add("粘贴操作较多（" + pasteCount + "次）");
            }
        }
        
        if (riskReasons.isEmpty()) {
            riskReasons.add("答题行为正常");
        }
        
        vo.setRiskLevel(riskLevel);
        vo.setRiskReasons(riskReasons);
        
        return vo;
    }

    /**
     * 获取操作类型名称
     */
    private String getActionName(int actionType) {
        return switch (actionType) {
            case ACTION_ENTER_EXPERIMENT -> "进入实验";
            case ACTION_SWITCH_QUESTION -> "切换题目";
            case ACTION_SAVE_ANSWER -> "保存答案";
            case ACTION_SUBMIT_EXPERIMENT -> "提交实验";
            case ACTION_LEAVE_PAGE -> "离开页面";
            case ACTION_RETURN_PAGE -> "返回页面";
            case ACTION_COPY -> "复制操作";
            case ACTION_PASTE -> "粘贴操作";
            case ACTION_FOCUS_LOST -> "失去焦点";
            default -> "未知操作";
        };
    }
}
