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
package io.sapl.springdatar2dbcdemo.domain;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LibraryUser extends org.springframework.security.core.userdetails.User implements UserDetails {

    private static final long serialVersionUID = -7244331453519181420L;

    @Getter
    private int           department;
    @Getter
    private List<Integer> dataScope = List.of();

    public LibraryUser(String username, int department, List<Integer> dataScope, String password) {
        super(username, password, true, true, true, true, List.of());
        this.department = department;
        this.dataScope  = dataScope;
    }

}
