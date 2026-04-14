package com.springboot.controller.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.experiment.DocxImportRequest;
import com.springboot.model.dto.experiment.EduExperimentAddRequest;
import com.springboot.model.dto.experiment.EduExperimentQueryRequest;
import com.springboot.model.dto.experiment.EduExperimentUpdateRequest;
import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.vo.experiment.DocxImportResult;
import com.springboot.model.vo.experiment.EduExperimentVO;
import com.springboot.service.experiment.EduExperimentQuestionService;
import com.springboot.service.experiment.EduExperimentService;
import com.springboot.utils.DocxTemplateGenerator;
import jakarta.annotation.Resource;
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

@RestController
@RequestMapping("/experiment/eduExperiment")
public class EduExperimentController {

    @Resource
    private EduExperimentService eduExperimentService;

    @Resource
    private EduExperimentQuestionService eduExperimentQuestionService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addEduExperiment(@RequestBody EduExperimentAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperiment entity = new EduExperiment();
        BeanUtils.copyProperties(addRequest, entity);
        eduExperimentService.validEduExperiment(entity, true);
        boolean result = eduExperimentService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEduExperiment(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduExperimentService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateEduExperiment(@RequestBody EduExperimentUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperiment entity = new EduExperiment();
        BeanUtils.copyProperties(updateRequest, entity);
        eduExperimentService.validEduExperiment(entity, false);
        boolean result = eduExperimentService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<EduExperimentVO> getEduExperimentVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperiment entity = eduExperimentService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduExperimentService.getEduExperimentVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<EduExperiment>> listEduExperimentByPage(@RequestBody EduExperimentQueryRequest queryRequest) {
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
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<EduExperiment> page = eduExperimentService.page(new Page<>(current, size), eduExperimentService.getQueryWrapper(queryRequest));
        return ResultUtils.success(eduExperimentService.getEduExperimentVOPage(page, null));
    }

    /**
     * 从docx文档导入实验和题目
     * <p>
     * 支持两种方式：
     * 1. 直接上传docx文件（file参数）
     * 2. 提供已上传的docx文件URL（importRequest.fileUrl）
     *
     * @param file 上传的docx文件（可选，与fileUrl二选一）
     * @param importRequest 导入请求参数
     * @return 导入结果
     */
    @PostMapping("/import/docx")
    public BaseResponse<DocxImportResult> importFromDocx(
            @RequestParam(required = false) MultipartFile file,
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

        // 创建实验记录
        EduExperiment experiment = new EduExperiment();
        experiment.setName(experimentName != null ? experimentName : "待定");
        experiment.setPublishStatus(autoPublish != null && autoPublish ? 1 : 0);

        // 调用导入服务
        DocxImportResult result = eduExperimentQuestionService.importExperimentFromDocx(importRequest, experiment);

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
        try {
            byte[] templateBytes = DocxTemplateGenerator.generateTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDispositionFormData("attachment", "实验文档导入模板.docx");
            headers.setContentLength(templateBytes.length);

            return new ResponseEntity<>(templateBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成模板文件失败: " + e.getMessage());
        }
    }
}
