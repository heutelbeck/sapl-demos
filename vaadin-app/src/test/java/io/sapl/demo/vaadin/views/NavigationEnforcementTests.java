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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.browserless.SpringBrowserlessTest;

@SpringBootTest
@DisplayName("Navigation and component enforcement")
class NavigationEnforcementTests extends SpringBrowserlessTest {

    private static final String ADMIN_PAGE      = "admin-page";
    private static final String ANNOTATION_PAGE = "annotation-page";

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void whenUserOpensAdminPageThenBuilderReroutesToHomePage() {
        navigate(ADMIN_PAGE, HomePage.class);

        assertThat(getCurrentView()).isInstanceOf(HomePage.class);
        assertThat(find(Notification.class).all()).isNotEmpty();
    }

    @Test
    @WithMockUser(username = "admin", roles = "Admin")
    void whenAdminOpensAdminPageThenPageIsShownWithNotification() {
        navigate(AdminPage.class);

        assertThat(getCurrentView()).isInstanceOf(AdminPage.class);
        assertThat(find(Notification.class).all()).isNotEmpty();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void whenUserOpensAnnotationPageThenAnnotationReroutesToHomePage() {
        navigate(ANNOTATION_PAGE, HomePage.class);

        assertThat(getCurrentView()).isInstanceOf(HomePage.class);
    }

    @Test
    @WithMockUser(username = "admin", roles = "Admin")
    void whenAdminOpensAnnotationPageThenPageIsShown() {
        navigate(AnnotationPage.class);

        assertThat(getCurrentView()).isInstanceOf(AnnotationPage.class);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void whenFormIsOpenedThenMinimumOrderObligationIsAppliedAndSubmitStaysDisabled() {
        navigate(ConstraintHandlingPage.class);

        var submit   = find(Button.class).withText("Submit order").single();
        var minPizza = find(NativeLabel.class).withTextContaining("You need to order at least").single();
        assertThat(submit.isEnabled()).isFalse();
        assertThat(minPizza.getText()).contains("pizza");
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void whenSubscriptionPageIsOpenedThenComponentsAreUnderPolicyControl() {
        navigate(SingleAndMultisubscriptionPage.class);

        assertThat(find(Button.class).withText("enforced by singlesubscription").all()).hasSize(1);
        assertThat(find(Button.class).withText("access enforced by multisubscription").all()).hasSize(1);
    }

}
