package org.demo;

import static org.demo.helper.EthConnect.makeDonation;

import org.demo.domain.PrinterUser;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CrowdfundingForm extends VerticalLayout {

	private static final long serialVersionUID = 8878184008835129794L;

	public CrowdfundingForm(PrinterUser user) {

		H2 crowdfunding = new H2("Crowdfunding");
		Paragraph explanation = new Paragraph(
				"We also need your help to maintain our printer service. Once our donation goal is reached, we will unlock a new template for the community.");

		Input donationField = new Input();
		Label unit = new Label("ETH");

		Button donate = new Button("Donate");
		donate.addClickListener(event -> makeDonation(user.getEthereumAddress(), donationField.getValue()));

		HorizontalLayout donation = new HorizontalLayout(donationField, unit, donate);

		VerticalLayout userShow = new VerticalLayout(crowdfunding, explanation, donation);

		add(userShow);

	}

}
