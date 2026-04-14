package com.springboot.service.cloud;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.HttpMethodEnum;
import com.obs.services.model.ObsObject;
import com.obs.services.model.TemporarySignatureRequest;
import com.obs.services.model.TemporarySignatureResponse;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ObsUploadService {

    @Value("${obs.access-key:}")
    private String accessKey;

    @Value("${obs.secret-key:}")
    private String secretKey;

    @Value("${obs.end-point:}")
    private String endPoint;

    @Value("${obs.bucket-name:}")
    private String bucketName;

    @Value("${obs.domain:}")
    private String domain;

    @Value("${obs.enable:true}")
    private boolean obsEnable;

    @Value("${obs.max-file-size:10485760}")
    private long maxFileSize;

    private ObsClient obsClient;

    @PostConstruct
    public void init() {
        if (!obsEnable) {
            log.warn("OBS服务未启用，将使用本地存储");
            return;
        }
        if (StringUtils.isAnyBlank(accessKey, secretKey, endPoint, bucketName)) {
            log.warn("OBS配置不完整，将使用本地存储");
            obsEnable = false;
            return;
        }
        try {
            obsClient = new ObsClient(accessKey, secretKey, endPoint);
            log.info("华为云OBS客户端初始化成功");
        } catch (Exception e) {
            log.error("华为云OBS客户端初始化失败", e);
            obsEnable = false;
        }
    }

    @PreDestroy
    public void destroy() {
        if (obsClient != null) {
            try {
                obsClient.close();
            } catch (IOException e) {
                log.error("关闭OBS客户端异常", e);
            }
        }
    }

    /**
     * 上传文件到OBS
     *
     * @param file      上传的文件
     * @param bizType   业务类型目录
     * @param studentId 学生ID
     * @return 上传结果
     */
    public ObsUploadResult uploadFile(MultipartFile file, String bizType, Long studentId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件不能为空");
        }

        if (file.getSize() > maxFileSize) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过" + (maxFileSize / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        String fileSuffix = FileUtil.getSuffix(originalFilename);
        String uuid = IdUtil.fastSimpleUUID();
        String objectName = String.format("%s/%d/%s.%s", bizType, studentId, uuid, fileSuffix);

        if (obsEnable && obsClient != null) {
            return uploadToObs(file, objectName, originalFilename);
        } else {
            return uploadToLocal(file, bizType, studentId, uuid, fileSuffix, originalFilename);
        }
    }

    /**
     * 上传到华为云OBS
     */
    private ObsUploadResult uploadToObs(MultipartFile file, String objectName, String originalFilename) {
        try {
            obsClient.putObject(bucketName, objectName, file.getInputStream());
            String obsUrl = domain + "/" + objectName;

            ObsUploadResult result = new ObsUploadResult();
            result.setSuccess(true);
            result.setFileName(originalFilename);
            result.setFileSize(file.getSize());
            result.setFileType(file.getContentType());
            result.setFileSuffix(FileUtil.getSuffix(originalFilename));
            result.setObsUrl(obsUrl);
            result.setObsBucket(bucketName);
            result.setObjectName(objectName);
            result.setObs(true);

            log.info("文件上传至OBS成功: {}", obsUrl);
            return result;
        } catch (IOException e) {
            log.error("文件上传至OBS失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传到本地存储（OBS未配置时的降级方案）
     */
    private ObsUploadResult uploadToLocal(MultipartFile file, String bizType, Long studentId,
                                           String uuid, String fileSuffix, String originalFilename) {
        String localPath = System.getProperty("user.dir") + "/uploads/" + bizType + "/" + studentId;
        Path dirPath = Paths.get(localPath);
        try {
            Files.createDirectories(dirPath);
            String localFileName = uuid + "." + fileSuffix;
            Path filePath = dirPath.resolve(localFileName);
            file.transferTo(filePath.toFile());

            String localUrl = "/api/files/" + bizType + "/" + studentId + "/" + localFileName;

            ObsUploadResult result = new ObsUploadResult();
            result.setSuccess(true);
            result.setFileName(originalFilename);
            result.setFileSize(file.getSize());
            result.setFileType(file.getContentType());
            result.setFileSuffix(fileSuffix);
            result.setObsUrl(localUrl);
            result.setObsBucket("local");
            result.setObjectName(bizType + "/" + studentId + "/" + localFileName);
            result.setObs(false);

            log.info("文件上传至本地成功: {}", localUrl);
            return result;
        } catch (IOException e) {
            log.error("文件上传至本地失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     *
     * @param objectName 对象名称
     * @return 文件字节数组
     */
    public byte[] downloadFile(String objectName) {
        if (obsEnable && obsClient != null) {
            return downloadFromObs(objectName);
        } else {
            return downloadFromLocal(objectName);
        }
    }

    private byte[] downloadFromObs(String objectName) {
        ObsObject obsObject = null;
        try {
            obsObject = obsClient.getObject(bucketName, objectName, null);
            if (obsObject.getObjectContent() == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文件不存在");
            }
            return obsObject.getObjectContent().readAllBytes();
        } catch (IOException e) {
            log.error("从OBS下载文件失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件下载失败");
        } finally {
            if (obsObject != null) {
                try {
                    obsObject.getObjectContent().close();
                } catch (IOException e) {
                    log.warn("关闭OBS对象流失败", e);
                }
            }
        }
    }

    private byte[] downloadFromLocal(String objectName) {
        String localPath = System.getProperty("user.dir") + "/uploads/" + objectName;
        Path path = Paths.get(localPath);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("从本地下载文件失败", e);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文件不存在");
        }
    }

    /**
     * 获取文件访问URL（生成带签名的临时访问URL）
     *
     * @param objectName 对象名称
     * @param expireSeconds 过期时间（秒）
     * @return 访问URL
     */
    public String getAccessUrl(String objectName, int expireSeconds) {
        if (obsEnable && obsClient != null) {
            try {
                TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
                request.setBucketName(bucketName);
                request.setObjectKey(objectName);
                TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
                return response.getSignedUrl();
            } catch (ObsException e) {
                log.error("获取OBS签名URL失败", e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取访问链接失败");
            }
        } else {
            return "/api/files/" + objectName;
        }
    }

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     * @return 是否删除成功
     */
    public boolean deleteFile(String objectName) {
        if (obsEnable && obsClient != null) {
            try {
                obsClient.deleteObject(bucketName, objectName);
                log.info("从OBS删除文件成功: {}", objectName);
                return true;
            } catch (ObsException e) {
                log.error("从OBS删除文件失败", e);
                return false;
            }
        } else {
            String localPath = System.getProperty("user.dir") + "/uploads/" + objectName;
            try {
                Files.deleteIfExists(Paths.get(localPath));
                log.info("从本地删除文件成功: {}", localPath);
                return true;
            } catch (IOException e) {
                log.error("从本地删除文件失败", e);
                return false;
            }
        }
    }

    /**
     * 判断是否为OBS存储
     */
    public boolean isObsEnabled() {
        return obsEnable;
    }

    /**
     * 获取桶名称
     */
    public String getBucketName() {
        return obsEnable ? bucketName : "local";
    }

    /**
     * 上传结果封装类
     */
    @Data
    public static class ObsUploadResult {
        private boolean success;
        private String fileName;
        private Long fileSize;
        private String fileType;
        private String fileSuffix;
        private String obsUrl;
        private String obsBucket;
        private String objectName;
        private boolean isObs;
    }
}
