package com.springboot.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.constant.FileConstant;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.common.EduLectureMapper;
import com.springboot.model.dto.common.EduLectureQueryRequest;
import com.springboot.model.entity.common.EduLecture;
import com.springboot.model.vo.common.EduLectureVO;
import com.springboot.service.common.EduLectureService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class EduLectureServiceImpl extends ServiceImpl<EduLectureMapper, EduLecture> implements EduLectureService {

    @Value("${spring.file.upload.path}")
    private String uploadPath;

    @Override
    public void validEduLecture(EduLecture eduLecture, boolean add) {
        ServiceMethodSupport.validEntity(eduLecture);
    }

    @Override
    public QueryWrapper<EduLecture> getQueryWrapper(EduLectureQueryRequest queryRequest) {
        QueryWrapper<EduLecture> queryWrapper = ServiceMethodSupport.buildQueryWrapper(queryRequest);
        if (queryRequest == null) {
            return queryWrapper;
        }
        queryWrapper.like(StringUtils.isNotBlank(queryRequest.getLectureName()), "lecture_name",
                queryRequest.getLectureName());
        queryWrapper.eq(queryRequest.getCategoryId() != null, "category_id", queryRequest.getCategoryId());
        queryWrapper.eq(StringUtils.isNotBlank(queryRequest.getFileExtension()), "file_extension",
                queryRequest.getFileExtension());
        queryWrapper.like(StringUtils.isNotBlank(queryRequest.getFilePath()), "file_path", queryRequest.getFilePath());
        return queryWrapper;
    }

    @Override
    public EduLectureVO getEduLectureVO(EduLecture eduLecture, HttpServletRequest request) {
        return EduLectureVO.objToVo(eduLecture);
    }

    @Override
    public Page<EduLectureVO> getEduLectureVOPage(Page<EduLecture> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduLectureVO::objToVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIdWithFile(String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduLecture entity = this.getById(id);
        if (entity == null) {
            return false;
        }

        boolean removed = this.removeById(id);
        if (!removed) {
            return false;
        }

        String filePath = entity.getFilePath();
        if (StringUtils.isBlank(filePath)) {
            return true;
        }

        Path localFile = resolveLocalFilePath(filePath);
        if (localFile == null) {
            log.warn("Skip deleting lecture file due to unknown filePath: {}", filePath);
            return true;
        }

        try {
            Files.deleteIfExists(localFile);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete lecture file: {}", localFile, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除资料文件失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByIdWithFileCleanup(EduLecture eduLecture) {
        if (eduLecture == null || eduLecture.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduLecture oldEntity = this.getById(eduLecture.getId());
        if (oldEntity == null) {
            return false;
        }

        boolean updated = this.updateById(eduLecture);
        if (!updated) {
            return false;
        }

        String oldPath = oldEntity.getFilePath();
        String newPath = eduLecture.getFilePath();
        if (StringUtils.isBlank(oldPath) || StringUtils.equals(oldPath, newPath)) {
            return true;
        }

        Path localFile = resolveLocalFilePath(oldPath);
        if (localFile == null) {
            log.warn("Skip deleting old lecture file due to unknown filePath: {}", oldPath);
            return true;
        }
        try {
            Files.deleteIfExists(localFile);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete old lecture file after update: {}", localFile, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新资料后删除旧文件失败");
        }
    }

    private Path resolveLocalFilePath(String filePath) {
        String normalized = filePath.trim();
        try {
            if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
                normalized = URI.create(normalized).getPath();
            }
        } catch (Exception ignore) {
            // ignore parse errors, fallback to raw string
        }

        String prefix = FileConstant.LOCAL_HOST;
        if (normalized.startsWith(prefix)) {
            normalized = normalized.substring(prefix.length());
        }
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        if (StringUtils.isBlank(normalized)) {
            return null;
        }

        Path base = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path resolved = base.resolve(normalized).normalize();
        if (!resolved.startsWith(base)) {
            // protect against path traversal
            return null;
        }
        return resolved;
    }
}
