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
package io.sapl.demo.tools.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
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
import io.sapl.demo.tools.chat.ChatService;
import io.sapl.demo.tools.domain.DemoPrincipal;
import io.sapl.demo.tools.domain.DemoUser;
import io.sapl.demo.tools.domain.Purpose;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.Disposable;

import java.io.Serial;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Route("")
@PageTitle("Clinical Trial AI Assistant")
public class ChatView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = SaplVersion.VERSION_UID;

    private static final String   ACTIVE     = "Active";
    private static final String[] DOT_FRAMES = { "", ".", "..", "...", "..", "." };

    private final transient ChatService chatService;
    private final MessageList messageList;
    private final TextField inputField;
    private final Button actionButton;
    private final Div promptButtons;
    private final Select<DemoUser> userSelector;
    private final Select<Purpose> purposeSelector;
    private final Select<String> securitySelector;

    private final List<MessageListItem> messages = new ArrayList<>();
    private transient Disposable currentSubscription;
    private transient ScheduledExecutorService animator;
    private volatile String currentStatusText = "";
    private volatile boolean animating;
    private boolean generating;

    public ChatView(ChatService chatService) {
        this.chatService = chatService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        val infoPanel = new InfoPanel();

        userSelector = new Select<>();
        userSelector.setLabel("Active User");
        userSelector.setItems(DemoUser.values());
        userSelector.setValue(DemoUser.DR_FISCHER);
        userSelector.setItemLabelGenerator(user -> {
            val site = "all".equals(user.getSite()) ? "All Sites" : capitalize(user.getSite());
            return user.getDisplayName() + " (" + user.getRole() + ", " + site + ")";
        });
        userSelector.setWidthFull();
        userSelector.addValueChangeListener(e -> clearChat());

        purposeSelector = new Select<>();
        purposeSelector.setLabel("Purpose");
        purposeSelector.setItems(Purpose.values());
        purposeSelector.setValue(Purpose.STATISTICAL_ANALYSIS);
        purposeSelector.setItemLabelGenerator(Purpose::getDisplayName);
        purposeSelector.setWidthFull();
        purposeSelector.addValueChangeListener(e -> clearChat());

        securitySelector = new Select<>();
        securitySelector.setLabel("SAPL Enforcement");
        securitySelector.setItems(ACTIVE, "Deactivated");
        securitySelector.setValue(ACTIVE);
        securitySelector.setWidthFull();
        securitySelector.addValueChangeListener(e -> clearChat());

        val selectorLayout = new HorizontalLayout(userSelector, purposeSelector, securitySelector);
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

        add(infoPanel, selectorLayout, messageList, inputLayout, promptButtons);
        setFlexGrow(1, messageList);
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
        messages.add(assistantMessage);
        messageList.setItems(messages);

        val ui = UI.getCurrent();
        val content = new StringBuilder();
        val history = buildConversationHistory();

        val purpose = purposeSelector.getValue();
        val securityActive = ACTIVE.equals(securitySelector.getValue());
        val principal = new DemoPrincipal(user.getDisplayName(), user.getRole(), user.getSite(),
                purpose != null ? purpose.name() : Purpose.STATISTICAL_ANALYSIS.name(), securityActive);
        val authentication = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        startDotAnimation(assistantMessage, ui);

        currentSubscription = chatService.askStreaming(text.strip(), history, status -> {
                    currentStatusText = status;
                    ui.access(() -> {
                        assistantMessage.setText(status);
                        messageList.setItems(messages);
                    });
                })
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                .subscribe(
                        token -> {
                            stopDotAnimation();
                            content.append(token);
                            ui.access(() -> {
                                assistantMessage.setText(content.toString());
                                messageList.setItems(messages);
                            });
                        },
                        error -> {
                            stopDotAnimation();
                            ui.access(() -> {
                                assistantMessage.setText("An error occurred while generating the response. Please try again.");
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

        addPromptButton(layout, "What are the PHQ-9 scores for P-003?");
        addPromptButton(layout, "Show all PHQ-9 data from the Edinburgh site");
        addPromptButton(layout, "Which participants showed improvement by week 8?");
        addPromptButton(layout, "Summarize all reported adverse events");
        addPromptButton(layout, "What do you know about P-003?");
        addPromptButton(layout, "List all participants with their real names and email addresses");
        addPromptButton(layout, "Who is Emily Campbell in the study?");
        addPromptButton(layout, "Which participants need to be contacted due to adverse events, include email addresses?");
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

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    private void clearChat() {
        messages.clear();
        messageList.setItems(messages);
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
