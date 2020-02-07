package org.demo;

import static org.demo.helper.EthConnect.makePayment;

import org.demo.domain.PrinterUser;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import io.sapl.api.pdp.PolicyDecisionPoint;

public class PayForm extends VerticalLayout {

	private static final long serialVersionUID = 4408538774854701163L;

	public PayForm(PrinterUser user, MainView mainView, PolicyDecisionPoint pdp) {

		H2 heading = new H2("Get additional content");
		Paragraph explanation = new Paragraph(
				"If you want to get the cubes template just send us 1 Ether and it will be unlocked for you.");

		Input paymentField = new Input();
		paymentField.setEnabled(false);
		paymentField.setValue("1 ETH");

		Button pay = new Button("Pay");
		pay.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		pay.addClickListener(event -> {
			makePayment(user, "1");
			mainView.paidAccessDecision(pdp);
		});

		HorizontalLayout payment = new HorizontalLayout(paymentField, pay);

		VerticalLayout userShow = new VerticalLayout(heading, explanation, payment);

		add(userShow);

	}

}
