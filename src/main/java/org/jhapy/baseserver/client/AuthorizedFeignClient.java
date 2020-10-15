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

package org.jhapy.baseserver.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@FeignClient
public @interface AuthorizedFeignClient {

  @AliasFor(annotation = FeignClient.class, attribute = "name")
  String name() default "";

  /**
   * A custom {@code @Configuration} for the feign client.
   *
   * Can contain override {@code @Bean} definition for the pieces that make up the client, for
   * instance {@link feign.codec.Decoder}, {@link feign.codec.Encoder}, {@link feign.Contract}.
   *
   * @return the custom {@code @Configuration} for the feign client.
   * @see FeignClientsConfiguration for the defaults.
   */
  @AliasFor(annotation = FeignClient.class, attribute = "configuration")
  Class<?>[] configuration() default OAuth2InterceptedFeignConfiguration.class;

  /**
   * An absolute URL or resolvable hostname (the protocol is optional).
   *
   * @return the URL.
   */
  String url() default "";

  /**
   * Whether 404s should be decoded instead of throwing FeignExceptions.
   *
   * @return true if 404s will be decoded; false otherwise.
   */
  boolean decode404() default false;

  /**
   * Fallback class for the specified Feign client interface. The fallback class must implement the
   * interface annotated by this annotation and be a valid Spring bean.
   *
   * @return the fallback class for the specified Feign client interface.
   */
  Class<?> fallback() default void.class;

  /**
   * Define a fallback factory for the specified Feign client interface. The fallback factory must
   * produce instances of fallback classes that implement the interface annotated by {@link
   * FeignClient}. The fallback factory must be a valid spring bean.
   *
   * @return fallback factory for the specified Feign client interface
   * @see feign.hystrix.FallbackFactory for details.
   */
  Class<?> fallbackFactory() default void.class;


  /**
   * Path prefix to be used by all method-level mappings. Can be used with or without {@code
   *
   * @return the path prefix to be used by all method-level mappings.
   * @RibbonClient}.
   */
  String path() default "";
}
