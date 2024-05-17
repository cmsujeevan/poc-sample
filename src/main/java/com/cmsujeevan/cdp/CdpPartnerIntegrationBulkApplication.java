package com.cmsujeevan.cdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CdpPartnerIntegrationBulkApplication {

    public static void main(String[] args) {
        SpringApplication.run(CdpPartnerIntegrationBulkApplication.class, args);
    }

}
