/*
 * Copyright 2020-2020 the original author or authors from the JHapy project.
 *
 * This file is part of the JHapy project, see https://www.jhapy.org/ for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jhapy.baseserver.config;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jhapy.commons.utils.HasLogger;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;


/**
 * Ping implementation if you want to do a "health check" kind of Ping. This will be a "real" ping.
 * As in a real http/s call is made to this url e.g. http://ec2-75-101-231-85.compute-1.amazonaws.com:7101/cs/hostRunning
 *
 * Some services/clients choose PingDiscovery - which is quick but is not a real ping. i.e It just
 * asks discovery (eureka) in-memory cache if the server is present in its Roster PingUrl on the
 * other hand, makes an actual call. This is more expensive - but its the "standard" way most VIPs
 * and other services perform HealthChecks.
 *
 * Choose your Ping based on your needs.
 *
 * @author stonse
 */
public class MyPingUrl implements IPing, HasLogger {

  public MyPingUrl() {
  }

  public boolean isAlive(Server server) {
    String loggerPrefix = getLoggerPrefix("isAlive", server.getHost());
    boolean isAlive = false;
    if (server != null && server instanceof DiscoveryEnabledServer) {
      DiscoveryEnabledServer dServer = (DiscoveryEnabledServer) server;
      InstanceInfo instanceInfo = dServer.getInstanceInfo();
      String appName = instanceInfo.getAppName();
      String instanceStatus = instanceInfo.getStatus().toString();
      String urlStr = instanceInfo.getHealthCheckUrl();

      HttpClient httpClient = new DefaultHttpClient();
      HttpUriRequest getRequest = new HttpGet(urlStr);
      String content = null;
      try {
        HttpResponse response = httpClient.execute(getRequest);
        content = EntityUtils.toString(response.getEntity());
        logger().trace(loggerPrefix + "content:" + content);

        if (response.getStatusLine().getStatusCode() == 200) {
          JsonParser springParser = JsonParserFactory.getJsonParser();
          Map<String, Object> map = springParser.parseMap(content);
          isAlive = map.get("status").equals("UP");
          logger().trace(loggerPrefix + appName + " server OK");
        } else {
          logger().trace(loggerPrefix + appName + " server KO");
        }
      } catch (IOException e) {
        logger()
            .error(loggerPrefix + "IO Exception with server '" + appName + "', instance status is '"
                + instanceStatus + "', url = '" + urlStr + "' : " + e.getLocalizedMessage());

      } catch (Exception e) {
        logger().error(loggerPrefix +
            "Unknown Exception with server url " + urlStr + " : " + e.getLocalizedMessage(), e);
      } finally {
        // Release the connection.
        getRequest.abort();
      }
    }
    return isAlive;
  }
}
