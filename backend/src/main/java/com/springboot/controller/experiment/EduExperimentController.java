package com.springboot.controller.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.mapper.experiment.EduExperimentItemMapper;
import com.springboot.mapper.experiment.ResExperimentResultMapper;
import com.springboot.mapper.user.AuthClassMapper;
import com.springboot.mapper.user.AuthStudentMapper;
import com.springboot.model.dto.experiment.DocxImportRequest;
import com.springboot.model.dto.experiment.EduExperimentAddRequest;
import com.springboot.model.dto.experiment.EduExperimentQueryRequest;
import com.springboot.model.dto.experiment.EduExperimentUpdateRequest;
import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.entity.experiment.EduExperimentItem;
import com.springboot.model.entity.experiment.ResExperimentResult;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.experiment.DocxImportResult;
import com.springboot.model.vo.experiment.EduExperimentVO;
import com.springboot.service.experiment.EduExperimentQuestionService;
import com.springboot.service.experiment.EduExperimentService;
import com.springboot.service.experiment.ExperimentClassService;
import com.springboot.utils.DocxTemplateGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/experiment/eduExperiment")
public class EduExperimentController {

    @Resource
    private EduExperimentService eduExperimentService;

    @Resource
    private EduExperimentQuestionService eduExperimentQuestionService;

    @Resource
    private EduExperimentItemMapper eduExperimentItemMapper;

    @Resource
    private ResExperimentResultMapper resExperimentResultMapper;

    @Resource
    private AuthStudentMapper authStudentMapper;

    @Resource
    private AuthClassMapper authClassMapper;

    @Resource
    private ExperimentClassService experimentClassService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addEduExperiment(@RequestBody EduExperimentAddRequest addRequest) {
        log.info("[EduExperiment] 新增实验");
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperiment entity = new EduExperiment();
        BeanUtils.copyProperties(addRequest, entity);
        eduExperimentService.validEduExperiment(entity, true);
        boolean result = eduExperimentService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        log.info("[EduExperiment] 新增实验成功: id={}", entity.getId());

        // 绑定班级
        if (addRequest.getClassCodes() != null && !addRequest.getClassCodes().isEmpty()) {
            experimentClassService.bindClasses(entity.getId(), addRequest.getClassCodes());
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEduExperiment(@RequestParam String id) {
        log.info("[EduExperiment] 删除实验: id={}", id);
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduExperimentService.removeById(id);
        log.info("[EduExperiment] 删除实验完成: id={}, success={}", id, result);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateEduExperiment(@RequestBody EduExperimentUpdateRequest updateRequest) {
        log.info("[EduExperiment] 更新实验");
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperiment entity = new EduExperiment();
        BeanUtils.copyProperties(updateRequest, entity);
        eduExperimentService.validEduExperiment(entity, false);
        boolean result = eduExperimentService.updateById(entity);
        log.info("[EduExperiment] 更新实验完成: id={}, success={}", updateRequest.getId(), result);

        // 更新班级绑定
        if (updateRequest.getClassCodes() != null) {
            experimentClassService.bindClasses(updateRequest.getId(), updateRequest.getClassCodes());
        }
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<EduExperimentVO> getEduExperimentVOById(@RequestParam String id) {
        log.debug("[EduExperiment] 查询实验详情: id={}", id);
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperiment entity = eduExperimentService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduExperimentService.getEduExperimentVO(entity, null));
    }

    /**
     * 获取实验相关的班级列表
     */
    @GetMapping("/classes")
    public BaseResponse<List<String>> getExperimentClasses(@RequestParam Long experimentId) {
        log.debug("[EduExperiment] 获取实验班级列表: experimentId={}", experimentId);
        if (experimentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询实验下的所有题目
        QueryWrapper<EduExperimentItem> itemQuery = new QueryWrapper<>();
        itemQuery.eq("experiment_id", experimentId);
        List<EduExperimentItem> items = eduExperimentItemMapper.selectList(itemQuery);

        if (items == null || items.isEmpty()) {
            return ResultUtils.success(Collections.emptyList());
        }

        Set<Long> itemIds = items.stream().map(EduExperimentItem::getId).collect(Collectors.toSet());

        // 查询所有提交了答案的学生ID
        QueryWrapper<ResExperimentResult> resultQuery = new QueryWrapper<>();
        resultQuery.in("item_id", itemIds);
        resultQuery.select("student_id");
        List<ResExperimentResult> results = resExperimentResultMapper.selectList(resultQuery);

        if (results == null || results.isEmpty()) {
            return ResultUtils.success(Collections.emptyList());
        }

        Set<Long> studentIds = results.stream()
                .filter(Objects::nonNull)
                .map(ResExperimentResult::getStudentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (studentIds.isEmpty()) {
            return ResultUtils.success(Collections.emptyList());
        }

        // ���询学生及其班级信息
        List<AuthStudent> students = authStudentMapper.selectBatchIds(studentIds);
        Set<String> classCodes = students.stream()
                .map(AuthStudent::getClassCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (classCodes.isEmpty()) {
            return ResultUtils.success(Collections.emptyList());
        }

        // 查询班级信息
        List<AuthClass> classes = authClassMapper.selectBatchIds(classCodes);
        List<String> classList = classes.stream()
                .map(AuthClass::getClassCode)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        return ResultUtils.success(classList);
    }

    /**
     * 获取实验已绑定的班级编号列表（通过 rel_experiment_class 表）
     */
    @GetMapping("/bind/classes")
    public BaseResponse<List<String>> getBindClasses(@RequestParam Long experimentId) {
        log.debug("[EduExperiment] 获取实验绑定班级: experimentId={}", experimentId);
        if (experimentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<String> classCodes = experimentClassService.getClassCodesByExperiment(experimentId);
        return ResultUtils.success(classCodes);
    }

    /**
     * 获取全部可选班级列表（供前端下拉选择）
     */
    @GetMapping("/all/classes")
    public BaseResponse<List<AuthClass>> getAllClasses() {
        log.debug("[EduExperiment] 获取全部班级列表");
        List<AuthClass> classes = authClassMapper.selectList(null);
        return ResultUtils.success(classes);
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<EduExperiment>> listEduExperimentByPage(@RequestBody EduExperimentQueryRequest queryRequest) {
        log.debug("[EduExperiment] 分页查询实验: current={}, size={}",
                queryRequest != null ? queryRequest.getCurrent() : "null",
                queryRequest != null ? queryRequest.getPageSize() : "null");
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<EduExperiment> page = eduExperimentService.page(new Page<>(current, size), eduExperimentService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EduExperimentVO>> listEduExperimentVOByPage(@RequestBody EduExperimentQueryRequest queryRequest) {
        log.debug("[EduExperiment] 分页查询实验VO: current={}, size={}",
                queryRequest != null ? queryRequest.getCurrent() : "null",
                queryRequest != null ? queryRequest.getPageSize() : "null");
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR, "每页数量不能超过200");
        Page<EduExperiment> page = eduExperimentService.page(new Page<>(current, size), eduExperimentService.getQueryWrapper(queryRequest));
        return ResultUtils.success(eduExperimentService.getEduExperimentVOPage(page, null));
    }

    /**
     * 从docx文档导入实验和题目
     * <p>
     * 支持两种方式：
     * 1. 直接上传docx文件（file参数）
     * 2. 提供已上传的docx文件URL（fileUrl参数）
     * <p>
     * 如果提供了experimentId，则将题目导入到已有实验；否则创建新实验。
     *
     * @param file 上传的docx文件（可选，与fileUrl二选一）
     * @param experimentId 已有实验ID（可选，若提供则导入到该实验）
     * @param experimentName 实验名称（可选）
     * @param categoryId 分类ID（可选）
     * @param requirement 实验要求（可选）
     * @param autoPublish 是否自动发布（默认true）
     * @param defaultDifficulty 默认难度（默认2）
     * @param defaultScore 默认分值（默认10）
     * @param fileUrl 已上传的docx文件URL（可选，与file二选一）
     * @return 导入结果
     */
    @PostMapping("/import/docx")
    public BaseResponse<DocxImportResult> importFromDocx(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) Long experimentId,
            @RequestParam(required = false) String experimentName,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String requirement,
            @RequestParam(required = false, defaultValue = "true") Boolean autoPublish,
            @RequestParam(required = false, defaultValue = "2") Integer defaultDifficulty,
            @RequestParam(required = false, defaultValue = "10") Integer defaultScore,
            @RequestParam(required = false) String fileUrl) {

        // 校验参数
        if ((file == null || file.isEmpty()) && (fileUrl == null || fileUrl.trim().isEmpty())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请上传docx文件或提供文件URL");
        }

        // 构建请求对象
        DocxImportRequest importRequest = new DocxImportRequest();
        importRequest.setFileUrl(fileUrl);
        importRequest.setExperimentName(experimentName);
        importRequest.setCategoryId(categoryId);
        importRequest.setRequirement(requirement);
        importRequest.setAutoPublish(autoPublish);
        importRequest.setDefaultDifficulty(defaultDifficulty);
        importRequest.setDefaultScore(defaultScore);

        // 如果提供了 experimentId，则使用已有实验；否则创建新实验
        EduExperiment experiment;
        if (experimentId != null) {
            experiment = eduExperimentService.getById(experimentId);
            if (experiment == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "实验不存在: " + experimentId);
            }
            log.info("[EduExperiment] 导入到已有实验: experimentId={}, name={}", experimentId, experiment.getName());
        } else {
            experiment = new EduExperiment();
            experiment.setName(experimentName != null ? experimentName : "待定");
            experiment.setPublishStatus(autoPublish != null && autoPublish ? 1 : 0);
        }

        // 调用导入服务（传入 file 以支持直接上传的 docx 文件）
        DocxImportResult result = eduExperimentQuestionService.importExperimentFromDocx(importRequest, experiment, file);
        log.info("[EduExperiment] DOCX导入完成: experimentName={}, success={}", result.getExperimentName(), result.getSuccess());
        return ResultUtils.success(result);
    }

    /**
     * 从docx文档导入实验和题目（JSON请求体方式）
     * <p>
     * 适合通过API调用，支持更丰富的参数配置
     *
     * @param importRequest 导入请求参数（必须包含fileUrl）
     * @return 导入结果
     */
    @PostMapping("/import/docx/json")
    public BaseResponse<DocxImportResult> importFromDocxJson(@RequestBody DocxImportRequest importRequest) {
        if (importRequest == null || StringUtils.isBlank(importRequest.getFileUrl())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请提供文件URL（fileUrl字段）");
        }

        // 创建实验记录
        EduExperiment experiment = new EduExperiment();
        experiment.setName(importRequest.getExperimentName() != null ? importRequest.getExperimentName() : "待定");
        experiment.setPublishStatus(importRequest.getAutoPublish() != null && importRequest.getAutoPublish() ? 1 : 0);

        // 调用导入服务
        DocxImportResult result = eduExperimentQuestionService.importExperimentFromDocx(importRequest, experiment);
        log.info("[EduExperiment] DOCX导入(JSON)完成: experimentName={}, success={}", result.getExperimentName(), result.getSuccess());
        return ResultUtils.success(result);
    }

    /**
     * 下载实验文档导入模板
     * <p>
     * 生成一个包含示例题目的docx模板文件，用户可以基于此模板编写实验文档
     *
     * @return docx格式的模板文件
     */
    @GetMapping("/template/download")
    public ResponseEntity<byte[]> downloadTemplate() {
        log.info("[EduExperiment] 下载实验导入模���");
        try {
            byte[] templateBytes = DocxTemplateGenerator.generateTemplate();

            // 中文文件名需要 RFC 5987 编码，避免部分容器抛 IllegalArgumentException
            String encodedFilename = java.net.URLEncoder
                    .encode("实验文档导入模板.docx", java.nio.charset.StandardCharsets.UTF_8)
                    .replace("+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"template.docx\"; filename*=UTF-8''" + encodedFilename);
            headers.setContentLength(templateBytes.length);

            return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("[EduExperiment] 生成模板失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成模板文件失败: " + e.getMessage());
        }
    }
}
