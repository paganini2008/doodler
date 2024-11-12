package com.github.doodler.common.swagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.github.doodler.common.swagger.SwaggerResourceProperties.Authorization;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Description: SwaggerResourceConfig
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Profile({"local", "dev", "test", "prod"})
@EnableKnife4j
@EnableSwagger2
@EnableConfigurationProperties({SwaggerResourceProperties.class})
@Configuration
public class SwaggerResourceConfig {

    private static final String ROOT_PATH = "/**";
    private static final String[] DEFAULT_EXCLUDED_PATH = new String[] {"/error", "/ping",
            "/favicon.ico", "/actuator/**", "/security/**", "/test/**", "/job/**"};

    @Value("${spring.mvc.servlet.path:}")
    private String servletContextPath;

    @Bean
    public Docket doc(SwaggerResourceProperties swaggerProperties) {
        Predicate<RequestHandler> predicate;
        if (CollectionUtils.isNotEmpty(swaggerProperties.getApi())) {
            List<Predicate<RequestHandler>> predicates = new ArrayList<Predicate<RequestHandler>>();
            for (String packageName : swaggerProperties.getApi()) {
                predicates.add(RequestHandlerSelectors.basePackage(packageName));
            }
            predicate = Predicates.or(predicates);
        } else {
            predicate = RequestHandlerSelectors.any();
        }
        List<Predicate<String>> basePath = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(swaggerProperties.getPath())) {
            basePath = swaggerProperties.getPath().stream().map(path -> PathSelectors.ant(path))
                    .collect(Collectors.toList());
        } else {
            basePath.add(PathSelectors.ant(ROOT_PATH));
        }

        List<Predicate<String>> excludedPath;
        if (CollectionUtils.isNotEmpty(swaggerProperties.getExcludedPath())) {
            excludedPath = swaggerProperties.getExcludedPath().stream()
                    .map(path -> PathSelectors.ant(path)).collect(Collectors.toList());
        } else {
            excludedPath = Arrays.stream(DEFAULT_EXCLUDED_PATH).map(path -> PathSelectors.ant(path))
                    .collect(Collectors.toList());
        }
        Docket doc = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo(swaggerProperties))
                .securitySchemes(Collections.singletonList(securityScheme(swaggerProperties)))
                .securityContexts(Collections.singletonList(securityContext(swaggerProperties)))
                .select().apis(predicate).paths(Predicates
                        .and(Predicates.not(Predicates.or(excludedPath)), Predicates.or(basePath)))
                .build();
        if (StringUtils.isNotBlank(servletContextPath)) {
            doc.pathMapping(servletContextPath);
        }
        return doc.globalOperationParameters(getHeaders());
    }

    @Bean
    public ApiInfo apiInfo(SwaggerResourceProperties swaggerProperties) {
        ApiInfoBuilder builder = new ApiInfoBuilder().title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription());
        if (StringUtils.isNotBlank(swaggerProperties.getVersion())) {
            builder.version(swaggerProperties.getVersion());
        }
        if (StringUtils.isNotBlank(swaggerProperties.getLicense())) {
            builder.license(swaggerProperties.getLicense());
        }
        if (StringUtils.isNotBlank(swaggerProperties.getLicenseUrl())) {
            builder.licenseUrl(swaggerProperties.getLicenseUrl());
        }
        if (StringUtils.isNotBlank(swaggerProperties.getTermsOfServiceUrl())) {
            builder.termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl());
        }
        if (swaggerProperties.getContact() != null) {
            com.github.doodler.common.swagger.SwaggerResourceProperties.Contact c =
                    swaggerProperties.getContact();
            builder.contact(new Contact(c.getName(), c.getUrl(), c.getEmail()));
        }
        return builder.build();
    }

    private List<Parameter> getHeaders() {
        List<Parameter> pairs = new ArrayList<Parameter>();
        ParameterBuilder tokenPair = new ParameterBuilder().name("Authorization").order(1)
                .description("Spring Security Authorization").modelRef(new ModelRef("string"))
                .parameterType("header").required(false);
        pairs.add(tokenPair.build());
        return pairs;
    }

    private SecurityScheme securityScheme(SwaggerResourceProperties swaggerProperties) {
        switch (swaggerProperties.getAuthorization().getMode()) {
            case "basic":
                return new BasicAuth("Authorization");
            case "oauth":
                return oauthSecuritySchema(swaggerProperties);
            default:
                return new ApiKey("Authorization", "Authorization", "header");
        }
    }

    private SecurityScheme oauthSecuritySchema(SwaggerResourceProperties swaggerProperties) {
        ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        swaggerProperties.getAuthorization().getAuthorizationScopeList()
                .forEach(authorizationScope -> authorizationScopeList.add(new AuthorizationScope(
                        authorizationScope.getScope(), authorizationScope.getDescription())));
        ArrayList<GrantType> grantTypes = new ArrayList<>();
        swaggerProperties.getAuthorization().getTokenUrl().forEach(
                tokenUrl -> grantTypes.add(new ResourceOwnerPasswordCredentialsGrant(tokenUrl)));
        return new OAuthBuilder().name(swaggerProperties.getAuthorization().getName())
                .grantTypes(grantTypes).scopes(authorizationScopeList).build();
    }

    @Bean
    SecurityContext securityContext(SwaggerResourceProperties swaggerProperties) {
        Authorization authorization = swaggerProperties.getAuthorization();
        List<Predicate<String>> basePath = new ArrayList<>();
        if (CollectionUtils.isEmpty(authorization.getPath())) {
            basePath.add(PathSelectors.ant(ROOT_PATH));
        }
        List<Predicate<String>> excludedPath = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(authorization.getExcludedPath())) {
            excludedPath = authorization.getExcludedPath().stream()
                    .map(path -> PathSelectors.ant(path)).collect(Collectors.toList());
        }
        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(defaultAuth(swaggerProperties)))
                .forPaths(Predicates.and(Predicates.not(Predicates.or(excludedPath)),
                        Predicates.or(basePath)))
                .build();
    }

    @Bean
    SecurityReference defaultAuth(SwaggerResourceProperties swaggerProperties) {
        AuthorizationScope[] authorizationScopeList;
        if (CollectionUtils
                .isNotEmpty(swaggerProperties.getAuthorization().getAuthorizationScopeList())) {
            authorizationScopeList = swaggerProperties.getAuthorization()
                    .getAuthorizationScopeList().stream()
                    .map(authorizationScope -> new AuthorizationScope(authorizationScope.getScope(),
                            authorizationScope.getDescription()))
                    .toArray(AuthorizationScope[]::new);
        } else {
            AuthorizationScope authorizationScope =
                    new AuthorizationScope("global", "Access Everything");
            authorizationScopeList = new AuthorizationScope[] {authorizationScope};
        }
        return SecurityReference.builder().reference(swaggerProperties.getAuthorization().getName())
                .scopes(authorizationScopeList).build();
    }

    @Configuration
    public static class StaticResourceConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/doc.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
    }
}
