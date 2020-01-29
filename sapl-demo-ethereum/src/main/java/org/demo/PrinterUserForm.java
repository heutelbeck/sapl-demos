package org.demo;

import static org.demo.helper.AccessCertificate.issueCertificate;
import static org.demo.helper.AccessCertificate.revokeCertificate;

import org.demo.domain.PrinterUser;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class PrinterUserForm extends VerticalLayout {

	private static final long serialVersionUID = 1949849828168713357L;

	private Button issue = new Button("Issue Certificate");

	private Button revoke = new Button("Revoke Certificate");

	public PrinterUserForm(PrinterUser user) {
		String username = user.getUsername();
		String address = user.getEthereumAddress();

		H2 userWelcome = new H2("Welcome " + username);
		Paragraph showAddress = new Paragraph("Your registered Ethereum address: " + address);

		VerticalLayout userShow = new VerticalLayout(userWelcome, showAddress);

		issue.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		issue.addClickListener(event -> issueCertificate(address));

		revoke.addClickListener(event -> revokeCertificate(address));

		HorizontalLayout buttons = new HorizontalLayout(issue, revoke);

		add(userShow, buttons);

	}

}
