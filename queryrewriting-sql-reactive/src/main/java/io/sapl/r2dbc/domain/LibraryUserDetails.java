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
package io.sapl.r2dbc.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Authentication wrapper containing credentials for Spring Security.
 * <p>
 * This record wraps a {@link LibraryUser} with the password hash required for
 * credential validation. It implements {@link UserDetails} to integrate with
 * Spring Security's authentication infrastructure.
 * <p>
 * <b>Important:</b> This class is used ONLY during authentication. After successful
 * credential validation, the wrapped {@link LibraryUser} is extracted and set as
 * the Authentication principal. This wrapper (with its password) is discarded.
 * <p>
 * This pattern ensures:
 * <ul>
 *   <li>Passwords never appear in the security context after authentication</li>
 *   <li>Authorization logic works with a clean identity object</li>
 *   <li>SAPL subscriptions contain only authorization-relevant attributes</li>
 * </ul>
 *
 * @see LibraryUser the clean principal used after authentication
 * @see io.sapl.r2dbc.security.SecurityConfig#authenticationManager for the extraction logic
 */
public record LibraryUserDetails(
        LibraryUser libraryUser,
        String password
) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return libraryUser.authorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return libraryUser.username();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
