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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApprovalService {

    static final int DEFAULT_TIMEOUT_SECONDS = 60;

    private final ConcurrentHashMap<String, CompletableFuture<Boolean>> pendingApprovals = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Consumer<ApprovalRequest>>> listeners = new ConcurrentHashMap<>();

    public boolean requestApproval(String sessionId, String toolName, String summary, String detail,
                                   boolean forceHumanInteraction) {
        val requestId = UUID.randomUUID().toString();
        val deadlineEpochMillis = System.currentTimeMillis() + DEFAULT_TIMEOUT_SECONDS * 1000L;
        val request = new ApprovalRequest(requestId, sessionId, toolName, summary, detail, forceHumanInteraction,
                deadlineEpochMillis);
        val future = new CompletableFuture<Boolean>();
        pendingApprovals.put(requestId, future);

        try {
            val sessionListeners = listeners.get(sessionId);
            if (sessionListeners == null || sessionListeners.isEmpty()) {
                log.warn("No listener registered for session {}. Denying approval for tool '{}'.", sessionId, toolName);
                return false;
            }
            for (val listener : sessionListeners) {
                listener.accept(request);
            }
            return future.get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.warn("Approval timed out for tool '{}' in session {}. Denying.", toolName, sessionId);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Approval interrupted for tool '{}' in session {}. Denying.", toolName, sessionId);
            return false;
        } catch (ExecutionException e) {
            log.error("Approval failed for tool '{}' in session {}. Denying.", toolName, sessionId, e);
            return false;
        } finally {
            pendingApprovals.remove(requestId);
        }
    }

    public void resolve(String requestId, boolean approved) {
        val future = pendingApprovals.get(requestId);
        if (future != null) {
            future.complete(approved);
        }
    }

    public void addListener(String sessionId, Consumer<ApprovalRequest> listener) {
        listeners.computeIfAbsent(sessionId, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

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
