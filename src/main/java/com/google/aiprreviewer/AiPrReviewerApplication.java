package com.google.aiprreviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AiPrReviewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPrReviewerApplication.class, args);
    }

}
