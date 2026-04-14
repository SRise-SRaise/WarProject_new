package com.springboot.service.experiment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.experiment.EduExperimentAttachmentMapper;
import com.springboot.model.dto.experiment.EduExperimentAttachmentQueryRequest;
import com.springboot.model.entity.experiment.EduExperimentAttachment;
import com.springboot.model.vo.experiment.EduExperimentAttachmentVO;
import com.springboot.service.cloud.ObsUploadService;
import com.springboot.service.experiment.EduExperimentAttachmentService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class EduExperimentAttachmentServiceImpl
        extends ServiceImpl<EduExperimentAttachmentMapper, EduExperimentAttachment>
        implements EduExperimentAttachmentService {

    private static final String BIZ_TYPE = "experiment_attachment";

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "doc", "docx", "pdf", "txt", "xls", "xlsx", "ppt", "pptx",
            "zip", "rar", "7z",
            "png", "jpg", "jpeg", "gif", "bmp", "webp",
            "java", "c", "cpp", "py", "js", "html", "css", "sql"
    );

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    @Autowired
    private ObsUploadService obsUploadService;

    @Override
    public EduExperimentAttachmentVO uploadAttachment(MultipartFile file, Long resultId, Long studentId) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        if (resultId == null) {
            throw new RuntimeException("实验提交记录ID不能为空");
        }
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }

        // 上传文件到OBS或本地
        ObsUploadService.ObsUploadResult uploadResult = obsUploadService.uploadFile(file, BIZ_TYPE, studentId);

        // 创建附件记录
        EduExperimentAttachment attachment = new EduExperimentAttachment();
        attachment.setResultId(resultId);
        attachment.setStudentId(studentId);
        attachment.setFileName(uploadResult.getFileName());
        attachment.setFileSize(uploadResult.getFileSize());
        attachment.setFileType(uploadResult.getFileType());
        attachment.setFileSuffix(uploadResult.getFileSuffix());
        attachment.setObsUrl(uploadResult.getObsUrl());
        attachment.setObsBucket(uploadResult.getObsBucket());
        attachment.setUploadStatus(uploadResult.isSuccess() ? 1 : 0);
        attachment.setCreatedAt(new Date());

        // 保存到数据库
        this.save(attachment);

        log.info("附件上传成功: 文件名={}, 学生ID={}, 记录ID={}",
                uploadResult.getFileName(), studentId, resultId);

        return EduExperimentAttachmentVO.objToVo(attachment);
    }

    @Override
    public List<EduExperimentAttachmentVO> batchUploadAttachment(MultipartFile[] files, Long resultId, Long studentId) {
        if (files == null || files.length == 0) {
            throw new RuntimeException("上传文件不能为空");
        }
        return Arrays.stream(files)
                .filter(file -> !file.isEmpty())
                .map(file -> uploadAttachment(file, resultId, studentId))
                .toList();
    }

    @Override
    public void validAttachment(EduExperimentAttachment attachment, boolean add) {
        if (attachment == null) {
            throw new RuntimeException("附件信息不能为空");
        }

        if (add) {
            if (attachment.getResultId() == null) {
                throw new RuntimeException("实验提交记录ID不能为空");
            }
            if (attachment.getStudentId() == null) {
                throw new RuntimeException("学生ID不能为空");
            }
        }

        // 校验文件大小
        if (attachment.getFileSize() != null && attachment.getFileSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过50MB");
        }

        // 校验文件后缀
        if (attachment.getFileSuffix() != null) {
            String suffix = attachment.getFileSuffix().toLowerCase();
            if (!ALLOWED_FILE_TYPES.contains(suffix)) {
                throw new RuntimeException("不支持的文件类型: " + suffix);
            }
        }
    }

    @Override
    public QueryWrapper<EduExperimentAttachment> getQueryWrapper(EduExperimentAttachmentQueryRequest queryRequest) {
        QueryWrapper<EduExperimentAttachment> queryWrapper = new QueryWrapper<>();

        if (queryRequest == null) {
            return queryWrapper;
        }

        // ID
        if (queryRequest.getId() != null) {
            queryWrapper.eq("id", queryRequest.getId());
        }
        // 提交记录ID
        if (queryRequest.getResultId() != null) {
            queryWrapper.eq("result_id", queryRequest.getResultId());
        }
        // 学生ID
        if (queryRequest.getStudentId() != null) {
            queryWrapper.eq("student_id", queryRequest.getStudentId());
        }
        // 文件名模糊搜索
        if (queryRequest.getFileName() != null && !queryRequest.getFileName().isEmpty()) {
            queryWrapper.like("file_name", queryRequest.getFileName());
        }
        // 文件后缀
        if (queryRequest.getFileSuffix() != null) {
            queryWrapper.eq("file_suffix", queryRequest.getFileSuffix());
        }
        // 上传状态
        if (queryRequest.getUploadStatus() != null) {
            queryWrapper.eq("upload_status", queryRequest.getUploadStatus());
        }

        // 排序
        if (queryRequest.getSortField() != null && !queryRequest.getSortField().isEmpty()) {
            String sortField = toUnderlineCase(queryRequest.getSortField());
            queryWrapper.orderBy(true,
                    "asc".equalsIgnoreCase(queryRequest.getSortOrder()),
                    sortField);
        } else {
            queryWrapper.orderByDesc("created_at");
        }

        return queryWrapper;
    }

    @Override
    public EduExperimentAttachmentVO getAttachmentVO(EduExperimentAttachment attachment, HttpServletRequest request) {
        return EduExperimentAttachmentVO.objToVo(attachment);
    }

    @Override
    public Page<EduExperimentAttachmentVO> getAttachmentVOPage(Page<EduExperimentAttachment> entityPage,
                                                               HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduExperimentAttachmentVO::objToVo);
    }

    @Override
    public List<EduExperimentAttachmentVO> getAttachmentsByResultId(Long resultId) {
        QueryWrapper<EduExperimentAttachment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("result_id", resultId);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("created_at");
        List<EduExperimentAttachment> attachments = this.list(queryWrapper);
        return EduExperimentAttachmentVO.objToVo(attachments);
    }

    @Override
    public List<EduExperimentAttachmentVO> getAttachmentsByStudentId(Long studentId) {
        QueryWrapper<EduExperimentAttachment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("created_at");
        List<EduExperimentAttachment> attachments = this.list(queryWrapper);
        return EduExperimentAttachmentVO.objToVo(attachments);
    }

    @Override
    public boolean deleteAttachment(Long attachmentId, Long studentId) {
        EduExperimentAttachment attachment = this.getById(attachmentId);
        if (attachment == null) {
            throw new RuntimeException("附件不存在");
        }

        // 校验是否是本人操作
        if (studentId != null && !studentId.equals(attachment.getStudentId())) {
            throw new RuntimeException("无权删除他人的附件");
        }

        // 逻辑删除
        attachment.setIsDeleted(1);
        return this.updateById(attachment);
    }

    @Override
    public String getDownloadUrl(Long attachmentId, int expireSeconds) {
        EduExperimentAttachment attachment = this.getById(attachmentId);
        if (attachment == null) {
            throw new RuntimeException("附件不存在");
        }

        // 从OBS URL中提取objectName
        String obsUrl = attachment.getObsUrl();
        if (obsUrl == null || obsUrl.isEmpty()) {
            throw new RuntimeException("文件路径无效");
        }

        String objectName;
        if (obsUrl.contains("/experiment_attachment/")) {
            objectName = obsUrl.substring(obsUrl.indexOf("/experiment_attachment/") + 1);
        } else if (obsUrl.startsWith("/api/files/")) {
            objectName = obsUrl.substring("/api/files/".length());
        } else {
            objectName = obsUrl;
        }

        return obsUploadService.getAccessUrl(objectName, expireSeconds);
    }

    /**
     * 驼峰转下划线
     */
    private String toUnderlineCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
