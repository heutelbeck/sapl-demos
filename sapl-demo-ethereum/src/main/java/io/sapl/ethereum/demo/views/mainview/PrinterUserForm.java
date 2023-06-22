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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

import io.sapl.ethereum.demo.helper.AccessCertificate;
import io.sapl.ethereum.demo.security.PrinterUser;

public class PrinterUserForm extends VerticalLayout {

	public PrinterUserForm(PrinterUser user, Select<String> printerSelect, AccessCertificate accessCertificate) {
		String username = user.getUsername();
		String address  = user.getEthereumAddress();

		H2   userWelcome = new H2("Welcome " + username);
		Span showAddress = new Span("Your registered Ethereum address: " + address);
		Span certExplain = new Span(
				"With the following buttons you can issue or revoke a certificate on the blockchain "
						+ "for the selected printer.");

		VerticalLayout userShow = new VerticalLayout(userWelcome, showAddress, certExplain);

		Button issue = new Button("Issue Certificate");
		issue.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		issue.addClickListener(event -> accessCertificate.issueCertificate(address, printerSelect.getValue()));

		Button revoke = new Button("Revoke Certificate");
		revoke.addClickListener(event -> accessCertificate.revokeCertificate(address, printerSelect.getValue()));

		HorizontalLayout buttons = new HorizontalLayout(issue, revoke);
		buttons.getStyle().set("margin-left", "20px");

		add(userShow, buttons);

	}

}
