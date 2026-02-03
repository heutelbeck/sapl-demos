/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.mongo.domain;

import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

/**
 * Subject identity containing authorization-relevant user attributes.
 * <p>
 * This record holds the authenticated user's identity along with domain-specific
 * attributes needed for authorization decisions:
 * <ul>
 *   <li>{@code username} - unique identifier</li>
 *   <li>{@code department} - organizational unit for department-based access control</li>
 *   <li>{@code dataScope} - list of library sections this user may access</li>
 *   <li>{@code authorities} - Spring Security granted authorities (roles)</li>
 * </ul>
 * <p>
 * <b>Why separate this from UserDetails?</b>
 * <p>
 * Spring Security's {@code UserDetails} is designed for <em>authentication</em> (validating
 * credentials), not for representing the authenticated subject. Using UserDetails as the
 * principal causes several problems:
 * <ul>
 *   <li><b>Credential leakage:</b> UserDetails contains the password, which then appears
 *       in logs, serialized tokens, and authorization contexts where it doesn't belong</li>
 *   <li><b>Serialization issues:</b> Custom UserDetails subclasses often don't serialize
 *       correctly, losing domain-specific attributes or exposing implementation details</li>
 *   <li><b>Conceptual confusion:</b> The principal should represent "who the user is",
 *       not "how they authenticated"</li>
 * </ul>
 * <p>
 * By using a dedicated identity record as the principal, we ensure clean serialization
 * in SAPL authorization subscriptions and prevent credentials from leaking into
 * policy evaluation contexts.
 * <p>
 * Implements {@link Principal} so that {@code Authentication.getName()} returns
 * the username rather than the record's toString().
 *
 * @see LibraryUserDetails for the authentication-only wrapper
 */
public record LibraryUser(
        String username,
        int department,
        List<Integer> dataScope,
        Collection<? extends GrantedAuthority> authorities
) implements Principal {

    public LibraryUser {
        dataScope = dataScope != null ? List.copyOf(dataScope) : List.of();
        authorities = authorities != null ? List.copyOf(authorities) : List.of();
    }

    @Override
    public String getName() {
        return username;
    }
}
