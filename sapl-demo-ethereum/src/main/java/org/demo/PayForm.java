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
package org.demo;

import org.demo.domain.PrinterUser;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.Getter;

@Getter
public class PayForm extends VerticalLayout {

	private static final long serialVersionUID = 4408538774854701163L;

	private final Button pay;

	public PayForm(PrinterUser user) {

		H2 heading = new H2("Get additional content");
		Paragraph explanation = new Paragraph(
				"If you want to get the cubes template just send us 1 Ether and it will be unlocked for you.");

		Input paymentField = new Input();
		paymentField.setEnabled(false);
		paymentField.setValue("1 ETH");

		pay = new Button("Pay");
		pay.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		HorizontalLayout payment = new HorizontalLayout(paymentField, pay);

		VerticalLayout userShow = new VerticalLayout(heading, explanation, payment);

		add(userShow);

	}

}
