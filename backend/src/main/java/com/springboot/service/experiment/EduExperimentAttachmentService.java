package com.springboot.service.experiment;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.experiment.EduExperimentAttachmentAddRequest;
import com.springboot.model.dto.experiment.EduExperimentAttachmentQueryRequest;
import com.springboot.model.dto.experiment.EduExperimentAttachmentUpdateRequest;
import com.springboot.model.entity.experiment.EduExperimentAttachment;
import com.springboot.model.vo.experiment.EduExperimentAttachmentVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EduExperimentAttachmentService extends IService<EduExperimentAttachment> {

    /**
     * 上传附件
     *
     * @param file         上传的文件
     * @param resultId     关联的实验提交记录ID
     * @param studentId    学生ID
     * @return 附件VO
     */
    EduExperimentAttachmentVO uploadAttachment(MultipartFile file, Long resultId, Long studentId);

    /**
     * 批量上传附件
     *
     * @param files       上传的文件数组
     * @param resultId    关联的实验提交记录ID
     * @param studentId   学生ID
     * @return 附件VO列表
     */
    List<EduExperimentAttachmentVO> batchUploadAttachment(MultipartFile[] files, Long resultId, Long studentId);

    /**
     * 校验附件
     *
     * @param attachment 附件实体
     * @param add        是否为新增
     */
    void validAttachment(EduExperimentAttachment attachment, boolean add);

    /**
     * 获取查询Wrapper
     *
     * @param queryRequest 查询请求
     * @return QueryWrapper
     */
    QueryWrapper<EduExperimentAttachment> getQueryWrapper(EduExperimentAttachmentQueryRequest queryRequest);

    /**
     * 获取附件VO
     *
     * @param attachment 附件实体
     * @param request    请求
     * @return 附件VO
     */
    EduExperimentAttachmentVO getAttachmentVO(EduExperimentAttachment attachment, HttpServletRequest request);

    /**
     * 获取附件VO分页
     *
     * @param entityPage 分页实体
     * @param request    请求
     * @return 附件VO分页
     */
    Page<EduExperimentAttachmentVO> getAttachmentVOPage(Page<EduExperimentAttachment> entityPage, HttpServletRequest request);

    /**
     * 根据实验提交记录ID获取附件列表
     *
     * @param resultId 实验提交记录ID
     * @return 附件列表
     */
    List<EduExperimentAttachmentVO> getAttachmentsByResultId(Long resultId);

    /**
     * 根据学生ID获取附件列表
     *
     * @param studentId 学生ID
     * @return 附件列表
     */
    List<EduExperimentAttachmentVO> getAttachmentsByStudentId(Long studentId);

    /**
     * 删除附件（逻辑删除）
     *
     * @param attachmentId 附件ID
     * @param studentId    学生ID（用于权限校验）
     * @return 是否删除成功
     */
    boolean deleteAttachment(Long attachmentId, Long studentId);

    /**
     * 获取附件下载URL
     *
     * @param attachmentId 附件ID
     * @param expireSeconds 过期时间（秒）
     * @return 下载URL
     */
    String getDownloadUrl(Long attachmentId, int expireSeconds);
}
