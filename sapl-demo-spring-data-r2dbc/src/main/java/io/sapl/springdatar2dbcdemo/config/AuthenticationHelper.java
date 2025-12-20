/*
 * Copyright (C) 2017-2024 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.springdatar2dbcdemo.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.springdatar2dbcdemo.domain.LibraryUser;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Helper bean to extract authentication information for SAPL subscriptions.
 * Workaround for QueryEnforce subject building limitations.
 */
@Component("authHelper")
@RequiredArgsConstructor
public class AuthenticationHelper {

    private final ObjectMapper objectMapper;

    /**
     * Returns the principal as a JSON object for SAPL subscriptions.
     * @return JSON object with principal data, or empty object if not authenticated
     */
    @SneakyThrows
    public ObjectNode getPrincipal() {
        var node = objectMapper.createObjectNode();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LibraryUser user) {
            node.put("username", user.getUsername());
            node.set("dataScope", objectMapper.valueToTree(user.getDataScope()));
        }
        return node;
    }
}
