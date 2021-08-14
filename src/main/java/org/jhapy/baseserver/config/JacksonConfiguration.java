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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.jhapy.dto.utils.LatLng;
import org.jhapy.dto.utils.MapBounds;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import java.io.IOException;

@Configuration
public class JacksonConfiguration {

    /**
     * Support for Java date and time API.
     *
     * @return the corresponding Jackson module.
     */
    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean
    public Jdk8Module jdk8TimeModule() {
        return new Jdk8Module();
    }

    /**
     * Jackson Afterburner module to speed up serialization/deserialization.
     */
    @Bean
    public AfterburnerModule afterburnerModule() {
        return new AfterburnerModule();
    }

    /*
     * Support for Hibernate types in Jackson.
     */
    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }

    /*
     * Module for serialization/deserialization of RFC7807 Problem.
     */
    @Bean
    public ProblemModule problemModule() {
        return new ProblemModule().withStackTraces();
    }

    /*
     * Module for serialization/deserialization of ConstraintViolationProblem.
     */
    @Bean
    public ConstraintViolationProblemModule constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }

    @Bean
    public JHapyModule jHapyModule() {
        return new JHapyModule();
    }

    class JHapyModule extends Module {
        @Override
        public String getModuleName() {
            return JHapyModule.class.getSimpleName();
        }

        @Override
        public Version version() {
            return VersionUtil.mavenVersionFor(JHapyModule.class.getClassLoader(),
                    "org.jhapy", "app-base-server");
        }

        @Override
        public void setupModule(SetupContext context) {
            final SimpleModule module = new SimpleModule();

            module.addDeserializer(MapBounds.class, new MapBoundsDeserializer());

            module.setupModule(context);
        }

        class MapBoundsDeserializer extends JsonDeserializer<MapBounds> {
            @Override
            public MapBounds deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                ObjectCodec oc = jp.getCodec();
                JsonNode node = oc.readTree(jp);

                final String northEast = node.get("northEast").asText();
                final String southWest = node.get("southWest").asText();

                ObjectMapper objectMapper = new ObjectMapper();
                LatLng northEastLatLng = objectMapper.readValue(northEast, LatLng.class);
                LatLng southWestLatLng = objectMapper.readValue(southWest, LatLng.class);

                return new MapBounds(northEastLatLng, southWestLatLng);
            }
        }
    }
}