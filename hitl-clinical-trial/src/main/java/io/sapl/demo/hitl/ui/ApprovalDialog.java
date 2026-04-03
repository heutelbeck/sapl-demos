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
package io.sapl.demo.hitl.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.sapl.api.SaplVersion;
import io.sapl.demo.hitl.approval.ApprovalRequest;
import io.sapl.demo.hitl.approval.ApprovalService;
import lombok.val;

import java.io.Serial;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;

class ApprovalDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = SaplVersion.VERSION_UID;

    private final transient ScheduledExecutorService countdown;

    ApprovalDialog(ApprovalRequest request, ApprovalService approvalService) {
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setHeaderTitle("Action Approval Required");

        val summarySpan = new Span(request.summary());
        summarySpan.getStyle().set("font-weight", "bold");

        val detailSpan = new Span(request.detail());
        detailSpan.getStyle().set("white-space", "pre-wrap");
        detailSpan.getStyle().set("font-size", "var(--lumo-font-size-s)");

        val details = new Details("Details", detailSpan);
        details.setWidthFull();

        val remainingSeconds = max(0, (request.deadlineEpochMillis() - currentTimeMillis()) / 1000);
        val countdownSpan = new Span("Auto-deny in " + remainingSeconds + "s");
        countdownSpan.getStyle().set("font-size", "var(--lumo-font-size-s)");
        countdownSpan.getStyle().set("color", "var(--lumo-secondary-text-color)");

        val body = new VerticalLayout(summarySpan, details, countdownSpan);
        body.setPadding(false);
        body.setSpacing(true);
        add(body);

        val approveButton = new Button("Approve", e -> {
            approvalService.resolve(request.requestId(), true);
            close();
        });
        approveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        val denyButton = new Button("Deny", e -> {
            approvalService.resolve(request.requestId(), false);
            close();
        });
        denyButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        val footer = new HorizontalLayout(approveButton, denyButton);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setWidthFull();
        getFooter().add(footer);

        val ui = UI.getCurrent();
        countdown = Executors.newSingleThreadScheduledExecutor();
        countdown.scheduleAtFixedRate(() -> {
            val remaining = max(0, (request.deadlineEpochMillis() - currentTimeMillis()) / 1000);
            ui.access(() -> {
                countdownSpan.setText("Auto-deny in " + remaining + "s");
                if (remaining <= 0) {
                    approvalService.resolve(request.requestId(), false);
                    close();
                }
            });
        }, 1, 1, TimeUnit.SECONDS);

        addDetachListener(e -> countdown.shutdownNow());
    }

}
