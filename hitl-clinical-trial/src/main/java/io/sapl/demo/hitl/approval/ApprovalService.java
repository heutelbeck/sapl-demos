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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;

/**
 * Manages blocking human-in-the-loop approval for tool invocations. Listeners
 * are registered per browser session so that approval dialogs route to the
 * originating tab.
 */
@Slf4j
@Service
public class ApprovalService {

    static final int DEFAULT_TIMEOUT_SECONDS = 60;
    static final String ERROR_APPROVAL_FAILED = "Approval failed for tool '{}' in session {}. Denying.";
    static final String WARN_APPROVAL_INTERRUPTED = "Approval interrupted for tool '{}' in session {}. Denying.";
    static final String WARN_APPROVAL_TIMED_OUT = "Approval timed out for tool '{}' in session {}. Denying.";
    static final String WARN_NO_LISTENER = "No listener registered for session {}. Denying approval for tool '{}'.";

    private final ConcurrentMap<String, CompletableFuture<Boolean>> pendingApprovals = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CopyOnWriteArrayList<Consumer<ApprovalRequest>>> listeners = new ConcurrentHashMap<>();

    /**
     * Blocks the calling thread until the human approves, denies, or the request
     * times out.
     *
     * @param sessionId browser session ID for routing the dialog
     * @param toolName tool name shown in the dialog
     * @param summary short summary shown in the dialog
     * @param detail expanded detail shown in the dialog
     * @param forceHumanInteraction if true, auto-approve cannot bypass the dialog
     * @param timeoutSeconds seconds before auto-deny; values below 1 use the default
     * @return the outcome of the approval request
     */
    ApprovalResult requestApproval(String sessionId, String toolName, String summary, String detail,
                                   boolean forceHumanInteraction, int timeoutSeconds) {
        val effectiveTimeout = timeoutSeconds > 0 ? timeoutSeconds : DEFAULT_TIMEOUT_SECONDS;
        val requestId = randomUUID().toString();
        val deadlineEpochMillis = currentTimeMillis() + effectiveTimeout * 1000L;
        val request = new ApprovalRequest(requestId, sessionId, toolName, summary, detail, forceHumanInteraction,
                deadlineEpochMillis);
        val future = new CompletableFuture<Boolean>();
        pendingApprovals.put(requestId, future);

        try {
            val sessionListeners = listeners.get(sessionId);
            if (sessionListeners == null || sessionListeners.isEmpty()) {
                log.warn(WARN_NO_LISTENER, sessionId, toolName);
                return ApprovalResult.DENIED;
            }
            for (val listener : sessionListeners) {
                listener.accept(request);
            }
            return Boolean.TRUE.equals(future.get(effectiveTimeout, TimeUnit.SECONDS))
                    ? ApprovalResult.APPROVED
                    : ApprovalResult.DENIED;
        } catch (TimeoutException e) {
            log.warn(WARN_APPROVAL_TIMED_OUT, toolName, sessionId);
            return ApprovalResult.TIMED_OUT;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn(WARN_APPROVAL_INTERRUPTED, toolName, sessionId);
            return ApprovalResult.DENIED;
        } catch (ExecutionException e) {
            log.error(ERROR_APPROVAL_FAILED, toolName, sessionId, e);
            return ApprovalResult.DENIED;
        } finally {
            pendingApprovals.remove(requestId);
        }
    }

    /**
     * Completes a pending approval request. Idempotent if already resolved.
     *
     * @param requestId the approval request to resolve
     * @param approved true to approve, false to deny
     */
    public void resolve(String requestId, boolean approved) {
        val future = pendingApprovals.get(requestId);
        if (future != null) {
            future.complete(approved);
        }
    }

    /**
     * Registers a listener that receives approval requests for the given session.
     *
     * @param sessionId the browser session ID
     * @param listener callback invoked when an approval is requested
     */
    public void addListener(String sessionId, Consumer<ApprovalRequest> listener) {
        listeners.computeIfAbsent(sessionId, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * Removes a previously registered listener for the given session.
     *
     * @param sessionId the browser session ID
     * @param listener the listener to remove
     */
    public void removeListener(String sessionId, Consumer<ApprovalRequest> listener) {
        val sessionListeners = listeners.get(sessionId);
        if (sessionListeners != null) {
            sessionListeners.remove(listener);
            if (sessionListeners.isEmpty()) {
                listeners.remove(sessionId);
            }
        }
    }

}
