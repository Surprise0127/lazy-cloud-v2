package com.lazy.c.doc.config;

import com.lazy.c.core.factory.YamlPropertySourceFactory;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * springdoc自动配置类
 *
 * @author Surprise0127
 * @since 1.0.0
 */
@AutoConfiguration
@PropertySource(value = "classpath:application-c-springdoc.yaml", factory = YamlPropertySourceFactory.class)
public class DocAutoConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Token", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .scheme("Bearer")))
                .addSecurityItem(new SecurityRequirement().addList("Token")) // todo 后续 接口文档 可以根据 spring security 的白名单决定是否显示锁
                .info(new Info()
                        .title(applicationName + " API")
                        .version("v1.0.0")
                        .description(applicationName + " 接口文档")
                        .contact(new Contact().name("lazy"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}