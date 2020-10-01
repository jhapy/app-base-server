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

import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.DeleteByStrIdQuery;
import org.jhapy.dto.serviceQuery.generic.GetByStrIdQuery;
import org.jhapy.dto.serviceQuery.generic.SaveQuery;
import org.jhapy.dto.utils.StoredFile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-05-15
 */
@FeignClient(name = "${jhapy.remote-services.resource-server.name:null}", url = "${jhapy.remote-services.resource-server.url:}", path = "/api/resourceService", fallbackFactory = ResourceServiceFallback.class)
@Primary
public interface ResourceService {

  @PostMapping(value = "/save")
  ServiceResult<StoredFile> save(@RequestBody SaveQuery<StoredFile> query);

  @PostMapping(value = "/getById")
  ServiceResult<StoredFile> getById(@RequestBody GetByStrIdQuery query);

  @PostMapping(value = "/delete")
  ServiceResult<Void> delete(@RequestBody DeleteByStrIdQuery query);
}
