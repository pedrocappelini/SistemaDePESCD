package br.dsw.pescd.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BASIC_AUTH = "basicAuth";

    @Bean
    public OpenAPI pescdOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("sistema pescd api")
                        .version("1.0.0"))
                .schemaRequirement(BASIC_AUTH, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic"))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH));
    }
}
