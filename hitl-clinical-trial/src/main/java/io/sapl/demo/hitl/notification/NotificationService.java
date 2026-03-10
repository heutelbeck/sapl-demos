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
package io.sapl.demo.hitl.notification;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    private final List<Consumer<ActionEvent>> actionListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<String>> toolCallListeners = new CopyOnWriteArrayList<>();

    public void send(String summary, String detail) {
        log.info("ACTION: {} - {}", summary, detail);
        var event = new ActionEvent(summary, detail);
        for (var listener : actionListeners) {
            listener.accept(event);
        }
    }

    public void toolCalled(String toolName) {
        log.info("TOOL CALL: {}", toolName);
        for (var listener : toolCallListeners) {
            listener.accept(toolName);
        }
    }

    public void addActionListener(Consumer<ActionEvent> listener) {
        actionListeners.add(listener);
    }

    public void removeActionListener(Consumer<ActionEvent> listener) {
        actionListeners.remove(listener);
    }

    public void addToolCallListener(Consumer<String> listener) {
        toolCallListeners.add(listener);
    }

    public void removeToolCallListener(Consumer<String> listener) {
        toolCallListeners.remove(listener);
    }

}
