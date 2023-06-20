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
package io.sapl.ethereum.demo.views.mainview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import io.sapl.ethereum.demo.helper.EthConnect;
import io.sapl.ethereum.demo.security.PrinterUser;

public class CrowdfundingForm extends VerticalLayout {

	private static final long serialVersionUID = 8878184008835129794L;

	public CrowdfundingForm(PrinterUser user, EthConnect ethConnect) {

		H2        crowdfunding = new H2("Crowdfunding");
		Paragraph explanation  = new Paragraph(
				"We also need your help to maintain our printer service. Once our donation goal has been reached, we will unlock a "
						+ "new template for the community.");

		Input donationField = new Input();
		NativeLabel unit          = new NativeLabel("ETH");
		unit.getStyle().set("margin-top", "10px");

		Button donate = new Button("Donate");
		donate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		donate.addClickListener(event -> ethConnect.makeDonation(user, donationField.getValue()));

		HorizontalLayout donation = new HorizontalLayout(donationField, unit, donate);

		VerticalLayout userShow = new VerticalLayout(crowdfunding, explanation, donation);

		add(userShow);

	}

}
