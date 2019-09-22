package hive.mugshot.swagger;

import com.google.common.collect.Lists;
import hive.pandora.constant.HiveInternalHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {

  @Bean
  public Docket apiDocumentation() {
    Set<String> responseContentTypes = new HashSet<>();
    responseContentTypes.add("*/*");
    responseContentTypes.add("application/json");
    responseContentTypes.add("image/jpeg");
    responseContentTypes.add("text/plain");
    return new
        Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("hive.mugshot"))
        .build()
        .produces(responseContentTypes)
        .consumes(responseContentTypes)
        .apiInfo(metaData())
        .securitySchemes(Arrays.asList(apiKey()))
        .securityContexts(Arrays.asList(securityContext()));
  }

  private ApiInfo metaData() {
    return new
        ApiInfoBuilder()
        .title("Mugshot endpoints")
        .description("\"Profile image management API\""
            + "\n Repository: https://github.com/hex-g/mugshot"
            + "\n Created by: https://github.com/hex-g/")
        .version("v1.0")
        .license("")
        .licenseUrl("")
        .build();
  }
  private ApiKey apiKey() {
    return new ApiKey(HiveInternalHeaders.AUTHENTICATED_USER_ID, "Authorization", "header");
  }
  @Bean
  public SecurityConfiguration security() {
    return SecurityConfigurationBuilder.builder().scopeSeparator(",")
        .additionalQueryStringParams(null)
        .useBasicAuthenticationWithAccessCodeGrant(false).build();
  }
  private SecurityContext securityContext() {
    return SecurityContext.builder().securityReferences(defaultAuth())
        .forPaths(PathSelectors.any()).build();
  }

  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope(
        "global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Arrays.asList(new SecurityReference(HiveInternalHeaders.AUTHENTICATED_USER_ID,
        authorizationScopes));
  }


  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}