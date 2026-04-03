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

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import io.sapl.api.SaplVersion;
import io.sapl.demo.hitl.approval.ApprovalRequest;
import io.sapl.demo.hitl.approval.ApprovalService;
import io.sapl.demo.hitl.approval.SessionIdHolder;
import io.sapl.demo.hitl.chat.ChatService;
import io.sapl.demo.hitl.domain.DemoPrincipal;
import io.sapl.demo.hitl.domain.DemoUser;
import io.sapl.demo.hitl.notification.ActionEvent;
import io.sapl.demo.hitl.notification.NotificationService;
import io.sapl.demo.hitl.tools.AdverseEventData;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.Disposable;

import java.io.Serial;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Route("")
@PageTitle("Clinical Trial AI Assistant (HITL)")
public class ChatView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = SaplVersion.VERSION_UID;

    private static final String[] DOT_FRAMES = { "", ".", "..", "...", "..", "." };
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final transient ChatService chatService;
    private final transient ApprovalService approvalService;
    private final transient String sessionId;
    private final transient Consumer<ActionEvent> actionListener;
    private final transient Consumer<String> toolCallListener;
    private final transient Consumer<ApprovalRequest> approvalListener;
    private final MessageList messageList;
    private final TextField inputField;
    private final Button actionButton;
    private final Div promptButtons;
    private final Select<DemoUser> userSelector;
    private final Checkbox autoApproveToggle;
    private final VerticalLayout actionLog;

    private final List<MessageListItem> messages = new ArrayList<>();
    private transient Disposable currentSubscription;
    private transient ScheduledExecutorService animator;
    private volatile boolean autoApprove;
    private volatile String currentStatusText = "";
    private volatile boolean animating;
    private final AtomicReference<StringBuffer> activeContent = new AtomicReference<>();
    private final AtomicReference<MessageListItem> activeAssistantMessage = new AtomicReference<>();
    private boolean generating;

    public ChatView(ChatService chatService, AdverseEventData adverseEventData,
                    NotificationService notificationService, ApprovalService approvalService) {
        this.chatService = chatService;
        this.approvalService = approvalService;
        this.sessionId = UUID.randomUUID().toString();
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        val ui = UI.getCurrent();
        actionListener = event -> ui.access(() -> addActionLogEntry(event));
        toolCallListener = toolName -> ui.access(() -> onToolCall(toolName));
        approvalListener = request -> {
            if (autoApprove && !request.forceHumanInteraction()) {
                approvalService.resolve(request.requestId(), true);
            } else {
                ui.access(() -> new ApprovalDialog(request, approvalService).open());
            }
        };
        notificationService.addActionListener(actionListener);
        notificationService.addToolCallListener(toolCallListener);
        approvalService.addListener(sessionId, approvalListener);
        addDetachListener(e -> {
            notificationService.removeActionListener(actionListener);
            notificationService.removeToolCallListener(toolCallListener);
            approvalService.removeListener(sessionId, approvalListener);
        });

        val infoPanel = new InfoPanel(adverseEventData);

        userSelector = new Select<>();
        userSelector.setLabel("Active User");
        userSelector.setItems(DemoUser.values());
        userSelector.setValue(DemoUser.DR_FISCHER);
        userSelector.setItemLabelGenerator(user -> user.getDisplayName() + " (" + user.getRole() + ")");
        userSelector.setWidthFull();
        userSelector.addValueChangeListener(e -> clearChat());

        autoApproveToggle = new Checkbox("Auto-Approve Actions");
        autoApproveToggle.setValue(false);
        autoApproveToggle.addValueChangeListener(e -> autoApprove = e.getValue());

        val selectorLayout = new HorizontalLayout(userSelector, autoApproveToggle);
        selectorLayout.setWidthFull();
        selectorLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        messageList = new MessageList();
        messageList.setMarkdown(true);
        messageList.setSizeFull();

        inputField = new TextField();
        inputField.setPlaceholder("Ask a question...");
        inputField.addKeyPressListener(Key.ENTER, e -> submitCurrentMessage());

        actionButton = new Button(VaadinIcon.PLAY.create(), e -> onActionButtonClick());

        val inputLayout = new HorizontalLayout(inputField, actionButton);
        inputLayout.setWidthFull();
        inputLayout.setFlexGrow(1, inputField);
        inputLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        promptButtons = createSuggestedPrompts();

        val chatPanel = new VerticalLayout(selectorLayout, messageList, inputLayout, promptButtons);
        chatPanel.setSizeFull();
        chatPanel.setPadding(false);
        chatPanel.setSpacing(true);
        chatPanel.setFlexGrow(1, messageList);

        actionLog = new VerticalLayout();
        actionLog.setPadding(false);
        actionLog.setSpacing(false);
        actionLog.getStyle().set("overflow-y", "auto");

        val actionLogHeader = new H4("Action Log");
        actionLogHeader.getStyle().set("margin", "0 0 8px 0");

        val actionLogPanel = new VerticalLayout(actionLogHeader, actionLog);
        actionLogPanel.setWidth("350px");
        actionLogPanel.setPadding(true);
        actionLogPanel.setSpacing(false);
        actionLogPanel.getStyle().set("border-left", "1px solid var(--lumo-contrast-10pct)");

        val mainLayout = new HorizontalLayout(chatPanel, actionLogPanel);
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(1, chatPanel);

        add(infoPanel, mainLayout);
        setFlexGrow(1, mainLayout);
    }

    private void onToolCall(String toolName) {
        val toolMessage = new MessageListItem("Calling " + toolName + "...", Instant.now(), "Tool");
        toolMessage.setUserColorIndex(3);
        messages.add(toolMessage);

        val message = new MessageListItem("", Instant.now(), "AI Assistant");
        message.setUserColorIndex(2);
        activeAssistantMessage.set(message);
        messages.add(message);
        activeContent.set(new StringBuffer());
        messageList.setItems(messages);
    }

    private void addActionLogEntry(ActionEvent event) {
        val timestamp = Instant.now().atZone(ZoneId.systemDefault()).format(TIME_FORMAT);
        val summary = new Span(timestamp + " - " + event.summary());
        summary.getStyle().set("font-size", "var(--lumo-font-size-s)");
        summary.getStyle().set("font-weight", "500");

        val detail = new Span(event.detail());
        detail.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        detail.getStyle().set("white-space", "pre-wrap");

        val entry = new Details(summary, detail);
        entry.setWidthFull();
        entry.getStyle().set("border-bottom", "1px solid var(--lumo-contrast-10pct)");
        actionLog.addComponentAsFirst(entry);
    }

    private void onActionButtonClick() {
        if (generating) {
            stopGeneration();
        } else {
            submitCurrentMessage();
        }
    }

    private void submitCurrentMessage() {
        val text = inputField.getValue();
        if (text == null || text.isBlank()) {
            return;
        }
        inputField.clear();
        inputField.setEnabled(false);
        promptButtons.setEnabled(false);
        actionButton.setIcon(VaadinIcon.STOP.create());
        generating = true;

        val user = userSelector.getValue();
        val userName = user != null ? user.getDisplayName() : "User";

        val userMessage = new MessageListItem(text.strip(), Instant.now(), userName);
        userMessage.setUserColorIndex(1);
        messages.add(userMessage);

        val assistantMessage = new MessageListItem("", Instant.now(), "AI Assistant");
        assistantMessage.setUserColorIndex(2);
        activeAssistantMessage.set(assistantMessage);
        messages.add(assistantMessage);
        activeContent.set(new StringBuffer());
        messageList.setItems(messages);

        val ui = UI.getCurrent();
        val history = buildConversationHistory();

        val principal = new DemoPrincipal(user.getDisplayName(), user.getRole());
        val authentication = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        startDotAnimation(assistantMessage, ui);

        currentSubscription = chatService.askStreaming(text.strip(), history, status -> {
                    currentStatusText = status;
                    ui.access(() -> {
                        activeAssistantMessage.get().setText(status);
                        messageList.setItems(messages);
                    });
                })
                .contextWrite(ctx -> ctx.put(SessionIdHolder.CONTEXT_KEY, sessionId))
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                .subscribe(
                        token -> {
                            stopDotAnimation();
                            activeContent.get().append(token);
                            ui.access(() -> {
                                activeAssistantMessage.get().setText(activeContent.get().toString());
                                messageList.setItems(messages);
                            });
                        },
                        error -> {
                            stopDotAnimation();
                            ui.access(() -> {
                                activeAssistantMessage.get().setText("An error occurred while generating the response. Please try again.");
                                messageList.setItems(messages);
                                onGenerationFinished();
                            });
                        },
                        () -> ui.access(this::onGenerationFinished)
                );
    }

    private void startDotAnimation(MessageListItem message, UI ui) {
        val dotFrame = new AtomicInteger(0);
        animating = true;
        animator = Executors.newSingleThreadScheduledExecutor();
        animator.scheduleAtFixedRate(() -> {
            if (!animating) {
                return;
            }
            val status = currentStatusText;
            if (status == null || status.isBlank()) {
                return;
            }
            val dots = DOT_FRAMES[dotFrame.getAndIncrement() % DOT_FRAMES.length];
            ui.access(() -> {
                if (!animating) {
                    return;
                }
                message.setText(status + dots);
                messageList.setItems(messages);
            });
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    private void stopDotAnimation() {
        animating = false;
        if (animator != null) {
            animator.shutdownNow();
            animator = null;
        }
    }

    private void stopGeneration() {
        stopDotAnimation();
        if (currentSubscription != null && !currentSubscription.isDisposed()) {
            currentSubscription.dispose();
        }
        onGenerationFinished();
    }

    private void onGenerationFinished() {
        inputField.setEnabled(true);
        promptButtons.setEnabled(true);
        actionButton.setIcon(VaadinIcon.PLAY.create());
        generating = false;
        currentSubscription = null;
        currentStatusText = "";
        inputField.focus();
    }

    private Div createSuggestedPrompts() {
        val layout = new Div();
        layout.getStyle().set("display", "flex");
        layout.getStyle().set("flex-wrap", "wrap");
        layout.getStyle().set("gap", "8px");

        addPromptButton(layout, "Handle all adverse events according to safety guidelines");
        addPromptButton(layout, "Handle all severe adverse events according to safety guidelines");
        addPromptButton(layout, "Handle AE-001 according to safety guidelines");
        addPromptButton(layout, "List all active adverse events");
        addPromptButton(layout, "What are the safety guidelines?");
        addPromptButton(layout, "Which adverse events require immediate action?");
        return layout;
    }

    private void addPromptButton(Div layout, String promptText) {
        val button = new Button(promptText, e -> {
            inputField.setValue(promptText);
            submitCurrentMessage();
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL);
        layout.add(button);
    }

    private void clearChat() {
        messages.clear();
        messageList.setItems(messages);
        actionLog.removeAll();
    }

    private String buildConversationHistory() {
        val history = new StringBuilder();
        for (val message : messages) {
            val role = message.getUserColorIndex() == 1 ? "User" : "Assistant";
            history.append(role).append(": ").append(message.getText()).append('\n');
        }
        return history.toString();
    }

}
