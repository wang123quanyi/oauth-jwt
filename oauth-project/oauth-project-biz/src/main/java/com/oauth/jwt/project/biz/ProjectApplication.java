package com.oauth.jwt.project.biz;

import com.oauth.jwt.common.data.annotation.MyEnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MyEnableFeignClients
@SpringBootApplication
public class ProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

}
