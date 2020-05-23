package org.jhapy.baseserver.config;

import java.util.Arrays;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@Configuration
//@EnableSwagger2
public class SwaggerConfig {
  private final Environment environment;

  public final String authServerAccessTokenUri;
  public final String authServerAuthorizeUri;
  public final String clientId;
  public final String clientSecret;

  public SwaggerConfig(Environment environment) {
    this.environment = environment;

    authServerAccessTokenUri = environment.getProperty("security.oauth2.client.accessTokenUri");
    authServerAuthorizeUri= environment.getProperty("security.oauth2.client.userAuthorizationUri");
    clientId = environment.getProperty("security.oauth2.client.clientId");
    clientSecret = environment.getProperty("security.oauth2.client.clientSecret");
  }

  @Bean
  public Docket api() {
    // @formatter:off
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("org.jhapy.baseserver.endpoint"))
        .paths(PathSelectors.any())
        .build()
        .securitySchemes( Arrays.asList( securityScheme() ) )
        .securityContexts( Arrays.asList( securityContext() ) );
    // @formatter:on
  }

  private ApiInfo apiInfo() {
    // @formatter:off
    return new ApiInfo(
        "jHapy REST API",
        "Rest API documentation for JHapy Framework.",
        "API v1.0",
        "Terms of service",
        new Contact("jHapy", "", "jhapy@jhapy.org"),
        "License of API",
        "API license URL",
        Collections.emptyList());
    // @formatter:on
  }

  @Bean
  public SecurityConfiguration security() {
    return SecurityConfigurationBuilder.builder()
        .clientId(clientId)
        .clientSecret(clientSecret)
        .useBasicAuthenticationWithAccessCodeGrant(true)
        .build();
  }

  private SecurityScheme securityScheme() {
    // @formatter:off
    GrantType grantType =
        new AuthorizationCodeGrantBuilder()
            .tokenEndpoint(new TokenEndpoint(authServerAccessTokenUri, "oauthtoken"))
            .tokenRequestEndpoint(
                new TokenRequestEndpoint(authServerAuthorizeUri, clientId, clientSecret))
            .build();
    // @formatter:on
    return new OAuthBuilder()
        .name("spring_oauth")
        .grantTypes(Collections.singletonList(grantType))
        .scopes(Arrays.asList(scopes()))
        .build();
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(
            Collections.singletonList(new SecurityReference("spring_oauth", scopes())))
        .forPaths(PathSelectors.any())
        .build();
  }

  private AuthorizationScope[] scopes() {
    return new AuthorizationScope[]{
        new AuthorizationScope("read", "for read operations"),
        new AuthorizationScope("write", "for write operations"),
        new AuthorizationScope("foo", "Access foo API")
    };
  }
}
