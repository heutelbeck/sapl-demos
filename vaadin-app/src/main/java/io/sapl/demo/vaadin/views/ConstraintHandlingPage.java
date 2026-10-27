/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.demo.vaadin.views;

import java.io.Serial;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.api.model.NumberValue;
import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.TextValue;
import io.sapl.api.model.Value;
import io.sapl.api.pdp.Decision;
import io.sapl.demo.vaadin.pizzaform.PizzaOrder;
import io.sapl.demo.vaadin.shared.Utilities;
import io.sapl.vaadin.PepBuilderService;
import io.sapl.vaadin.constraint.providers.FieldValidationConstraintHandlerProvider;
import jakarta.annotation.security.PermitAll;

/**
 * Demonstrates constraint handling on a form: field validation driven by JSON
 * schemas in obligations, a local constraint handler for the minimum order and
 * a confirmation dialog outside the opening hours.
 */
@PermitAll
@PageTitle("Constraint Handling Page")
@Route(value = "constraint-handling-page", layout = MainLayout.class)
public final class ConstraintHandlingPage extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String CONSTRAINT_TYPE_VAADIN = "saplVaadin";
    private static final String CONSTRAINT_ID_MIN_PIZZA = "minPizza";
    private static final String PIZZA_IMAGE_HEIGHT      = "140px";

    private final IntegerField cheese    = new IntegerField("Cheese Pizza");
    private final IntegerField veggie    = new IntegerField("Veggie Pizza");
    private final IntegerField pepperoni = new IntegerField("Pepperoni Pizza");
    private final IntegerField beer      = new IntegerField("+Beer");
    private final EmailField   email     = new EmailField("E-Mail");
    private final TimePicker   time      = new TimePicker("Delivery time");
    private final Button       submit    = new Button("Submit order");
    private final NativeLabel  minPizzaLabel = new NativeLabel();

    private final BeanValidationBinder<PizzaOrder> binder = new BeanValidationBinder<>(PizzaOrder.class);

    private final transient PepBuilderService pepBuilderService;

    private boolean submitAllowed = false;
    private int     minPizza      = 0;

    public ConstraintHandlingPage(PepBuilderService pepBuilderService) {
        this.pepBuilderService = pepBuilderService;
        setWidth(null);
        setHeightFull();

        add(Utilities.getInfoText(
                "This page demonstrates JSON-Schema-based constraint handling functionality while using a form."));
        add(Utilities.getDefaultHeader("Constraint Handling Page"));
        add(Utilities.getInfoText(
                "The number of allowed pizza(s) is controlled by a time-based sapl policy and a local constraint handler provider."));
        add(new HorizontalLayout(pizzaColumn("images/cheese.jpg", cheese), pizzaColumn("images/veggie.jpg", veggie),
                pizzaColumn("images/salami.jpg", pepperoni)));
        add(minPizzaLabel);
        add(Utilities.getInfoText(
                "Beer is only allowed between 0 seconds and 30 seconds. The beer-field use JSON-schema and SAPL policies for validation."));
        beer.setValueChangeMode(ValueChangeMode.EAGER);
        email.setValueChangeMode(ValueChangeMode.EAGER);
        add(beer, clock(), email, time, submit);

        submit.setEnabled(false);
        binder.addStatusChangeListener(event -> {
            var ordered          = zeroIfNull(cheese.getValue()) + zeroIfNull(pepperoni.getValue())
                    + zeroIfNull(veggie.getValue());
            var minPizzaSelected = ordered >= minPizza;
            minPizzaLabel.setText("You need to order at least " + minPizza + " pizza.");
            minPizzaLabel.getStyle().set("color", minPizzaSelected ? "green" : "red");
            submit.setEnabled(submitAllowed && minPizzaSelected && binder.isValid());
        });

        enforceFormPolicies();
        binder.bindInstanceFields(this);
        binder.setBean(new PizzaOrder());
        enforceOpeningHours();
    }

    private static VerticalLayout pizzaColumn(String image, IntegerField field) {
        field.setValueChangeMode(ValueChangeMode.EAGER);
        var picture = new Image(image, field.getLabel());
        picture.setHeight(PIZZA_IMAGE_HEIGHT);
        var column = new VerticalLayout(picture, field);
        column.setAlignItems(Alignment.CENTER);
        return column;
    }

    private NativeLabel clock() {
        var label     = new NativeLabel();
        var formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        var timer     = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                label.getUI().ifPresent(ui -> ui.access(
                        () -> label.setText("time: " + LocalDateTime.now(ZoneId.systemDefault()).format(formatter))));
            }
        }, 0, 1000);
        addDetachListener(event -> timer.cancel());
        return label;
    }

    /**
     * Subscribes the form to the policies for action "display" on resource
     * "pizzaForm". Field validation obligations are handled by the
     * {@link FieldValidationConstraintHandlerProvider}, the minimum order by a
     * local constraint handler and the decision enables the submit button.
     */
    private void enforceFormPolicies() {
        var fieldValidation = new FieldValidationConstraintHandlerProvider(binder, this).bindField(cheese)
                .bindField(veggie).bindField(pepperoni).bindField(email).bindField(time).bindField(beer);
        pepBuilderService.with(this).action("display").resource("pizzaForm")
                .addConstraintHandlerProvider(fieldValidation)
                .addConstraintHandler(ConstraintHandlingPage::isMinPizzaConstraint,
                        constraint -> ui -> minPizza = minimumOf(constraint))
                .onDecisionDo(decision -> submitAllowed = decision.decision() == Decision.PERMIT).build();
    }

    private static boolean isMinPizzaConstraint(Value constraint) {
        return constraint instanceof ObjectValue obj && obj.get("type") instanceof TextValue(String type)
                && CONSTRAINT_TYPE_VAADIN.equals(type) && obj.get("id") instanceof TextValue(String id)
                && CONSTRAINT_ID_MIN_PIZZA.equals(id);
    }

    private static int minimumOf(Value constraint) {
        return constraint instanceof ObjectValue obj && obj.get("min") instanceof NumberValue(var min)
                ? min.intValue()
                : 0;
    }

    /**
     * Shows a confirmation dialog when the page is visited outside the opening
     * hours, as requested by the policy "opening_hours_dialog".
     */
    private void enforceOpeningHours() {
        var openingHours = ObjectValue.builder().put("startTime", Value.of("10:00")).put("endTime", Value.of("16:00"))
                .build();
        pepBuilderService.with(this).resource(openingHours).action("opening_hours_dialog")
                .onDecisionDo((decision, component) -> {
                }).build();
    }

    private static int zeroIfNull(Integer value) {
        return value != null ? value : 0;
    }

}
