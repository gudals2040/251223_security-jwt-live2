package kr.java.jwt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 5-4
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() { // default가 아니라 우리가 bean을 통해서 주입한 swagger
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
        return new OpenAPI()
                .info(new Info()
                        .title("JWT Security API")
                        .description("Spring Security 6 + JWT + Redis")
                        .version("1.0.0")
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
