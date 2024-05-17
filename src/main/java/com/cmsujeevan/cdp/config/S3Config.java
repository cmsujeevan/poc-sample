package com.cmsujeevan.cdp.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class S3Config {

    @Value("${s3.source.bucket}")
    private String sourceBucket;

    @Value("${s3.source.folder}")
    private String sourceFolder;
    @Value("${s3.destination.bucket}")
    private String destinationBucket;

    @Value("${s3.url.expire.duration}")
    private int urlExpireDuration;

    @Value("${s3.url.expire.timeUnit}")
    private String urlExpireTimeUnit;

    @Value("${s3.activate.folder}")
    private String activateFolder;

    @Bean
    public AmazonS3 getAmazonS3() {
        return AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    }

    @Bean(name = "sourceBucket")
    public String getSourceBucket(){
        return sourceBucket;
    }

    @Bean(name = "destinationBucket")
    public String getDestinationBucket() {
        return destinationBucket;
    }

    @Bean(name = "preSignedUrlExpiration")
    public int getUrlExpireDuration() {
        return urlExpireDuration;
    }

    @Bean(name = "preSignedUrlExpirationTimeUnit")
    public String getUrlExpireTimeUnit() {
        return urlExpireTimeUnit;
    }

    @Bean(name = "sourceFolder")
    public String getSourceFolder() {
        return sourceFolder;
    }

    @Bean(name = "activateFolder")
    public String getActivateFolder() {
        return activateFolder;
    }

}
