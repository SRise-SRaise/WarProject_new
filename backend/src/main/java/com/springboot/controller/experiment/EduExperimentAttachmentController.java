package com.springboot.controller.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.experiment.EduExperimentAttachmentQueryRequest;
import com.springboot.model.entity.experiment.EduExperimentAttachment;
import com.springboot.model.vo.experiment.EduExperimentAttachmentVO;
import com.springboot.service.cloud.ObsUploadService;
import com.springboot.service.experiment.EduExperimentAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/experiment/attachment")
@Slf4j
@Tag(name = "实验附件管理")
public class EduExperimentAttachmentController {

    @Resource
    private EduExperimentAttachmentService attachmentService;

    @Resource
    private ObsUploadService obsUploadService;

    @PostMapping("/upload")
    @Operation(summary = "上传附件", description = "上传实验报告附件到OBS")
    public BaseResponse<EduExperimentAttachmentVO> uploadAttachment(
            @Parameter(description = "上传的文件", required = true) @RequestPart("file") MultipartFile file,
            @Parameter(description = "实验提交记录ID", required = true) @RequestParam Long resultId,
            @Parameter(description = "学生ID", required = true) @RequestParam Long studentId) {

        if (resultId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "实验提交记录ID不能为空");
        }
        if (studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生ID不能为空");
        }

        EduExperimentAttachmentVO attachmentVO = attachmentService.uploadAttachment(file, resultId, studentId);
        return ResultUtils.success(attachmentVO);
    }

    @PostMapping("/upload/batch")
    @Operation(summary = "批量上传附件", description = "批量上传实验报告附件到OBS")
    public BaseResponse<List<EduExperimentAttachmentVO>> batchUploadAttachment(
            @Parameter(description = "上传的文件数组", required = true) @RequestPart("files") MultipartFile[] files,
            @Parameter(description = "实验提交记录ID", required = true) @RequestParam Long resultId,
            @Parameter(description = "学生ID", required = true) @RequestParam Long studentId) {

        if (resultId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "实验提交记录ID不能为空");
        }
        if (studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生ID不能为空");
        }
        if (files == null || files.length == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件不能为空");
        }

        List<EduExperimentAttachmentVO> attachments = attachmentService.batchUploadAttachment(files, resultId, studentId);
        return ResultUtils.success(attachments);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除附件", description = "删除指定的附件（逻辑删除）")
    public BaseResponse<Boolean> deleteAttachment(
            @Parameter(description = "附件ID", required = true) @RequestParam Long id,
            @Parameter(description = "学生ID", required = true) @RequestParam Long studentId) {

        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "附件ID不能为空");
        }
        if (studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生ID不能为空");
        }

        boolean result = attachmentService.deleteAttachment(id, studentId);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete/admin")
    @Operation(summary = "管理员删除附件", description = "管理员删除指定的附件")
    public BaseResponse<Boolean> adminDeleteAttachment(
            @Parameter(description = "附件ID", required = true) @RequestParam Long id) {

        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "附件ID不能为空");
        }

        boolean result = attachmentService.deleteAttachment(id, null);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    @Operation(summary = "获取附件详情", description = "根据ID获取附件详细信息")
    public BaseResponse<EduExperimentAttachmentVO> getAttachmentVO(
            @Parameter(description = "附件ID", required = true) @RequestParam Long id) {

        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "附件ID不能为空");
        }

        EduExperimentAttachment attachment = attachmentService.getById(id);
        ThrowUtils.throwIf(attachment == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(attachment.getIsDeleted() != null && attachment.getIsDeleted() == 1,
                ErrorCode.NOT_FOUND_ERROR, "附件已被删除");

        return ResultUtils.success(attachmentService.getAttachmentVO(attachment, null));
    }

    @PostMapping("/list/page")
    @Operation(summary = "分页查询附件", description = "分页查询附件列表")
    public BaseResponse<Page<EduExperimentAttachmentVO>> listAttachmentByPage(
            @RequestBody EduExperimentAttachmentQueryRequest queryRequest) {

        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR, "每页最多50条数据");

        Page<EduExperimentAttachment> page = attachmentService.page(
                new Page<>(current, size),
                attachmentService.getQueryWrapper(queryRequest));

        return ResultUtils.success(attachmentService.getAttachmentVOPage(page, null));
    }

    @GetMapping("/list/by-result/{resultId}")
    @Operation(summary = "获取实验提交的所有附件", description = "根据实验提交记录ID获取所有附件")
    public BaseResponse<List<EduExperimentAttachmentVO>> getAttachmentsByResultId(
            @Parameter(description = "实验提交记录ID", required = true) @PathVariable Long resultId) {

        if (resultId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "实验提交记录ID不能为空");
        }

        List<EduExperimentAttachmentVO> attachments = attachmentService.getAttachmentsByResultId(resultId);
        return ResultUtils.success(attachments);
    }

    @GetMapping("/list/by-student/{studentId}")
    @Operation(summary = "获取学生的所有附件", description = "根据学生ID获取所有附件")
    public BaseResponse<List<EduExperimentAttachmentVO>> getAttachmentsByStudentId(
            @Parameter(description = "学生ID", required = true) @PathVariable Long studentId) {

        if (studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生ID不能为空");
        }

        List<EduExperimentAttachmentVO> attachments = attachmentService.getAttachmentsByStudentId(studentId);
        return ResultUtils.success(attachments);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载附件", description = "下载指定的附件")
    public void downloadAttachment(
            @Parameter(description = "附件ID", required = true) @PathVariable Long id,
            HttpServletResponse response) {

        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "附件ID不能为空");
        }

        EduExperimentAttachment attachment = attachmentService.getById(id);
        if (attachment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "附件不存在");
        }

        try {
            // 从OBS URL中提取objectName
            String obsUrl = attachment.getObsUrl();
            String objectName = extractObjectName(obsUrl);

            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8) + "\"");

            if (attachment.getFileSize() != null) {
                response.setHeader("Content-Length", String.valueOf(attachment.getFileSize()));
            }

            // 获取文件内容并写入响应
            byte[] fileContent = obsUploadService.downloadFile(objectName);
            try (OutputStream os = response.getOutputStream()) {
                os.write(fileContent);
                os.flush();
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载附件失败: id={}", id, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "下载失败");
        }
    }

    @GetMapping("/download/url/{id}")
    @Operation(summary = "获取附件下载链接", description = "获取附件的下载/访问链接（带签名）")
    public BaseResponse<String> getDownloadUrl(
            @Parameter(description = "附件ID", required = true) @PathVariable Long id,
            @Parameter(description = "链接有效期（秒），默认3600秒") @RequestParam(defaultValue = "3600") int expireSeconds) {

        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "附件ID不能为空");
        }

        String downloadUrl = attachmentService.getDownloadUrl(id, expireSeconds);
        return ResultUtils.success(downloadUrl);
    }

    /**
     * 从OBS URL中提取objectName
     */
    private String extractObjectName(String obsUrl) {
        if (obsUrl == null || obsUrl.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件路径无效");
        }
        if (obsUrl.contains("/experiment_attachment/")) {
            return obsUrl.substring(obsUrl.indexOf("/experiment_attachment/") + 1);
        } else if (obsUrl.startsWith("/api/files/")) {
            return obsUrl.substring("/api/files/".length());
        }
        return obsUrl;
    }
}
