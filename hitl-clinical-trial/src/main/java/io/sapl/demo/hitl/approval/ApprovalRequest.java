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

/**
 * Immutable request for human approval of a tool invocation.
 *
 * @param requestId unique identifier for this approval request
 * @param sessionId browser session to route the approval dialog to
 * @param toolName name of the tool requiring approval
 * @param summary short description shown in the approval dialog
 * @param detail expanded description shown in the approval dialog
 * @param forceHumanInteraction if true, auto-approve cannot bypass the dialog
 * @param deadlineEpochMillis epoch millis after which the request auto-denies
 */
public record ApprovalRequest(String requestId, String sessionId, String toolName,
                       String summary, String detail,
                       boolean forceHumanInteraction, long deadlineEpochMillis) {
}
