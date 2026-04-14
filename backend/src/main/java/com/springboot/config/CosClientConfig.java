package com.springboot.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.AnonymousCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云对象存储客户端
 */
@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosClientConfig {

    /**
     * accessKey
     */
    private String accessKey;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 区域
     */
    private String region;

    /**
     * 桶名
     */
    private String bucket;

    /**
     * 本地开发占位用 COSClient：
     * - 使用匿名凭证，避免 accessKey/secretKey 为空导致启动失败
     * - 真正需要上传到 COS 时，再改回 BasicCOSCredentials + 正确配置
     */
    @Bean
    public COSClient cosClient() {
        COSCredentials cred = new AnonymousCOSCredentials();
        String effectiveRegion = (region != null && !region.isEmpty()) ? region : "ap-guangzhou";
        ClientConfig clientConfig = new ClientConfig(new Region(effectiveRegion));
        return new COSClient(cred, clientConfig);
    }
}
