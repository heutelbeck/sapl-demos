/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.theme.lumo.Lumo;

public class MainViewIT extends AbstractViewTest {

	@Test
	public void clickingButtonShowsNotification() {
		Assert.assertFalse($(NotificationElement.class).exists());

		$(ButtonElement.class).first().click();

		Assert.assertTrue($(NotificationElement.class).waitForFirst().isOpen());
	}

	@Test
	public void clickingButtonTwiceShowsTwoNotifications() {
		Assert.assertFalse($(NotificationElement.class).exists());

		ButtonElement button = $(ButtonElement.class).first();
		button.click();
		button.click();

		Assert.assertEquals(2, $(NotificationElement.class).all().size());
	}

	@Test
	public void buttonIsUsingLumoTheme() {
		WebElement element = $(ButtonElement.class).first();
		assertThemePresentOnElement(element, Lumo.class);
	}

}
