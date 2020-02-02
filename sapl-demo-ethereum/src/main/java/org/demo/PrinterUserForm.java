package org.demo;

import static org.demo.helper.AccessCertificate.issueCertificate;
import static org.demo.helper.AccessCertificate.revokeCertificate;

import org.demo.domain.PrinterUser;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

public class PrinterUserForm extends VerticalLayout {

	private static final long serialVersionUID = 1949849828168713357L;

	public PrinterUserForm(PrinterUser user, Select<String> printerSelect) {
		String username = user.getUsername();
		String address = user.getEthereumAddress();

		H2 userWelcome = new H2("Welcome " + username);
		Span showAddress = new Span("Your registered Ethereum address: " + address);
		Span certExplain = new Span(
				"With the following buttons you can issue or revoke a certificate on the blockchain "
						+ "for the selected printer.");

		VerticalLayout userShow = new VerticalLayout(userWelcome, showAddress, certExplain);

		Button issue = new Button("Issue Certificate");
		issue.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		issue.addClickListener(event -> issueCertificate(address, printerSelect.getValue()));

		Button revoke = new Button("Revoke Certificate");
		revoke.addClickListener(event -> revokeCertificate(address, printerSelect.getValue()));

		HorizontalLayout buttons = new HorizontalLayout(issue, revoke);
		buttons.getStyle().set("margin-left", "20px");

		add(userShow, buttons);

	}

}
