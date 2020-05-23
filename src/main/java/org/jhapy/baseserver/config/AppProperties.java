package org.jhapy.baseserver.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private final RemoteServices remoteServices = new RemoteServices();
  private final Bootstrap bootstrap = new Bootstrap();
  private final Hazelcast hazelcast = new Hazelcast();

  @Data
  public static class RemoteServices {

    private RemoteServer backendServer = new RemoteServer();
    private RemoteServer authorizationServer = new RemoteServer();
    private RemoteServer i18nServer = new RemoteServer();
    private RemoteServer resourceServer = new RemoteServer();
    private RemoteServer auditServer = new RemoteServer();

    @Data
    public static final class RemoteServer {

      private String url;
      private String name;
    }
  }

  @Data
  public static final class Bootstrap {

    private Iso3166 iso3166 = new Iso3166();
    private CategoryList categoryList = new CategoryList();

    @Data
    public static final class Iso3166 {

      private Boolean isEnabled;
      private String file;
    }

    @Data
    public static final class CategoryList {

      private Boolean isEnabled;
      private String file;
    }
  }

  @Data
  public static class Hazelcast {

    private final ManagementCenter managementCenter = new ManagementCenter();
    private String interfaces;
    private int timeToLiveSeconds = 3600;
    private int backupCount = 1;

    @Data
    public static class ManagementCenter {

      private boolean enabled = false;
      private int updateInterval = 3;
      private String url = "";
    }
  }
}
