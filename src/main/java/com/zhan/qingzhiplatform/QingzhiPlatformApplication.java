package com.zhan.qingzhiplatform;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;

// 启动类
@ServletComponentScan
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "青知平台接口文档", version = "1.0"))
public class QingzhiPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(QingzhiPlatformApplication.class, args);
    }

}
