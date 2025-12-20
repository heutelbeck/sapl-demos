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
package io.sapl.springdatar2dbcdemo;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import io.sapl.springdatar2dbcdemo.domain.LibraryUser;

/**
 * Factory for creating SecurityContext with a LibraryUser principal.
 */
public class WithMockLibraryUserSecurityContextFactory implements WithSecurityContextFactory<WithMockLibraryUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockLibraryUser annotation) {
        var dataScope = Arrays.stream(annotation.dataScope()).boxed().collect(Collectors.toList());
        var user = new LibraryUser(
            annotation.username(),
            annotation.department(),
            dataScope,
            "password"
        );
        var auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
