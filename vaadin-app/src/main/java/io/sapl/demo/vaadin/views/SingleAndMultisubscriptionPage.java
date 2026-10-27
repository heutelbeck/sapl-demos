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

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.api.model.ObjectValue;
import io.sapl.api.model.Value;
import io.sapl.demo.vaadin.shared.Utilities;
import io.sapl.vaadin.PepBuilderService;
import jakarta.annotation.security.PermitAll;

/**
 * Four components under policy control. The left button has a subscription of
 * its own, the right three share one multi subscription.
 */
@PermitAll
@PageTitle("Single- and Multisubscription Page")
@Route(value = "single-and-multisubscription-page", layout = MainLayout.class)
public class SingleAndMultisubscriptionPage extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String VIEW_COMPONENT               = "view_component";
    private static final String SINGLE_AND_MULTISUBSCRIPTION = "SingleAndMultisubscription";
    private static final String INFOTEXT                     = "This site demonstrates the usage of Single- and "
            + "MultiSubscription component based builders.\n"
            + "The related policies are defined in the file: singleAndMultisubscriptionPage.sapl";

    public SingleAndMultisubscriptionPage(PepBuilderService pepBuilderService) {
        setWidth(null);
        setHeightFull();
        add(Utilities.getInfoText(INFOTEXT));
        add(Utilities.getDefaultHeader("Single- and Multisubscription Page"));
        var layout = new HorizontalLayout(createSingleSubscriptionLayout(pepBuilderService),
                createMultisubscriptionLayout(pepBuilderService));
        layout.setSizeFull();
        add(layout);
    }

    private static ObjectValue resourceOf(String object) {
        return ObjectValue.builder().put("object", Value.of(object)).put("page", Value.of(SINGLE_AND_MULTISUBSCRIPTION))
                .build();
    }

    private static VerticalLayout createSingleSubscriptionLayout(PepBuilderService pepBuilderService) {
        var button = pepBuilderService.with(new Button("enforced by singlesubscription")).action(VIEW_COMPONENT)
                .resource(resourceOf("button")).onDecisionEnableOrDisable().build();
        button.setMinWidth("86px");
        button.setWidth("100%");
        var layout = new VerticalLayout(new H3("Single Subscription"));
        layout.setMaxWidth("600px");
        layout.add(Utilities.getInfoText("access to buttons is granted/revoked in a 5 seconds interval "
                + "by the policy \"grant_access_to_buttons_on_singleAndMultisubscription_page\"\n"
                + "- decision is enforced on the Button by disabling/enabling the button"));
        layout.add(button);
        return layout;
    }

    private static VerticalLayout createMultisubscriptionLayout(PepBuilderService pepBuilderService) {
        var buttonResource    = resourceOf("button");
        var textfieldResource = resourceOf("textfield");
        var button1           = new Button("access enforced by multisubscription");
        var button2           = new Button("visibility enforced by multisubscription");
        var textField         = new TextField("access enforced by multisubscription");
        // @formatter:off
        pepBuilderService.getMultiBuilder().subject("dummy")
            .with(button1)
                .action(VIEW_COMPONENT).resource(buttonResource)
                .onDecisionEnableOrDisable()
            .and(button2)
                .action(VIEW_COMPONENT).resource(buttonResource)
                .onDecisionVisibleOrHidden()
            .and().with(textField)
                .action(VIEW_COMPONENT).resource(textfieldResource)
                .onDecisionEnableOrDisable()
            .build();
        // @formatter:on
        var layout = new VerticalLayout(new H3("Multisubscription"));
        layout.setMaxWidth("600px");
        layout.add(Utilities.getInfoText("""
                access to buttons is granted/revoked in a 5 seconds interval by the policy "grant_access_to_buttons_on_singleAndMultisubscription_page".
                  - decision is enforced on the 1. Button by disabling/enabling the button
                  - decision is enforced on the 2. Button by hiding/showing the button"""),
                Utilities.getInfoText("""
                        access to TextBox is granted/revoked in a 6 seconds interval by the policy "grant_access_to_textfields_on_singleAndMultisubscription_page".
                          - decision is enforced on the TextField by disabling/enabling the TextField"""));
        for (var component : new HasSize[] { button1, button2, textField }) {
            component.setWidth("100%");
        }
        layout.add(button1, button2, textField);
        return layout;
    }

}
