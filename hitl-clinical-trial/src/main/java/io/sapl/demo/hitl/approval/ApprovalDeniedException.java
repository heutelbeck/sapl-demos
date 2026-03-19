/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.demo.hitl.approval;

import java.io.Serial;

import org.springframework.security.access.AccessDeniedException;

/**
 * Thrown when an operator explicitly denies a tool invocation in the
 * human-in-the-loop approval dialog.
 */
public class ApprovalDeniedException extends AccessDeniedException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ApprovalDeniedException(String toolName, String summary) {
        super("Operator denied '" + toolName + "': " + summary);
    }

}
