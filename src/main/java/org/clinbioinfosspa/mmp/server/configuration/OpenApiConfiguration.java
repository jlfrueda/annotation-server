package org.clinbioinfosspa.mmp.server.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI api() {
        var components = new Components();
        // components.addSecuritySchemes("bearer-key", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"));
        // var license = new License().name(licenseName).url(licenseUrl);
        // Contact contact = new Contact().name(contactName).email(contactEmail).url(contactUrl);
        // var info = new Info().title(name).description(description).license(license).contact(contact).version(version);
        var info = new Info();
        return new OpenAPI().components(components).info(info);
    }
}
