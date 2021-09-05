package org.jhapy.baseserver.config;

import org.jhapy.commons.config.AppProperties;
import org.jhapy.mailcow.client.invoker.ApiClient;
import org.jhapy.mailcow.client.invoker.auth.ApiKeyAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaldavConfig {
  @Bean
  public ApiClient apiClient(AppProperties appProperties) {
    ApiClient apiClient = new ApiClient();
    ApiKeyAuth apiKeyAuth = (ApiKeyAuth) apiClient.getAuthentication("ApiKeyAuth");
    apiKeyAuth.setApiKey(appProperties.getMailcow().getApiKey());

    apiClient.setBasePath(appProperties.getMailcow().getServerUrl());

    return apiClient;
  }
}