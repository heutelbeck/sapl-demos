package io.sapl.vaadindemo.views;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.vaadin.PepBuilderService;
import io.sapl.vaadindemo.shared.Utilities;
import jakarta.annotation.security.PermitAll;

/**
 * The SingleAndMultisubscriptionPage got four buttons. The left one is for
 * singlesubscription using {@link VaadinSingleButtonPepBuilder} and the right
 * three are for multisubscriptions with {@link MultiBuilder}
 *
 */
@PermitAll
@PageTitle("Single- and Multisubscription Page")
@Route(value = "single-and-multisubscription-page", layout = MainLayout.class)
public class SingleAndMultisubscriptionPage extends VerticalLayout {
	private static final String INFOTEXT = "This site demonstrates the usage of Single- and "
			+ "MultiSubscription component based builders.\n"
			+ "The related policies are defined in the file: singleAndMultisubscriptionPage.sapl";

	public SingleAndMultisubscriptionPage(PepBuilderService pepBuilderService) {

		setWidth(null);
		setHeightFull();
		add(Utilities.getInfoText(INFOTEXT));
		add(Utilities.getDefaultHeader("Single- and Multisubscription Page"));

		HorizontalLayout horizontalLayout = new HorizontalLayout(createSingleSubscriptionLayout(pepBuilderService),
				crateMultisubscriptionLayout(pepBuilderService));
		horizontalLayout.setSizeFull();
		add(horizontalLayout);
	}

	private VerticalLayout createSingleSubscriptionLayout(PepBuilderService pepBuilderService) {
		H3 singleSubscriptionH3 = new H3("Single Subscription");

		Button singleSubscriptionButton = createButtonSingleSubscriptionEnforce(pepBuilderService);
		singleSubscriptionButton.setMinWidth("86px");

		VerticalLayout singleSubscriptionLayout = new VerticalLayout(singleSubscriptionH3);
		singleSubscriptionLayout.setMaxWidth("600px");

		// add info
		singleSubscriptionLayout.add(
				Utilities.getInfoText(
						"access to buttons is granted/revoked in a 5 seconds interval " +
								"by the policy \"grant_access_to_buttons_on_singleAndMultisubscription_page\"\n" +
								"- decision is enforced on the Button by disabling/enabling the button"));

		// add and format component
		singleSubscriptionLayout.add(singleSubscriptionButton);
		singleSubscriptionButton.setWidth("100%");

		return singleSubscriptionLayout;
	}

	private Button createButtonSingleSubscriptionEnforce(PepBuilderService pepBuilderService) {
		var resource = JsonNodeFactory.instance.objectNode()
				.put("object", "button")
				.put("page", "SingleAndMultisubscription");

		return pepBuilderService.with(new Button("enforced by singlesubscription"))
				.action("view_component")
				.resource(resource)
				.onDecisionEnableOrDisable()
				.build();
	}

	private VerticalLayout crateMultisubscriptionLayout(PepBuilderService pepBuilderService) {
		H3  multiSubscriptionH3 = new H3("Multisubscription");
		var buttonResource      = JsonNodeFactory.instance.objectNode()
				.put("object", "button")
				.put("page", "SingleAndMultisubscription");

		var textfieldResource = JsonNodeFactory.instance.objectNode()
				.put("object", "textfield")
				.put("page", "SingleAndMultisubscription");

		Button    button1    = new Button("access enforced by multisubscription");
		Button    button2    = new Button("visibility enforced by multisubscription");
		TextField textField1 = new TextField("access enforced by multisubscription");

		// @formatter:off
		pepBuilderService.getMultiBuilder().subject("dummy")
			.with(button1)
				.action("view_component").resource(buttonResource)
				.onDecisionEnableOrDisable()
			.and(button2)
				.action("view_component").resource(buttonResource)
				.onDecisionVisibleOrHidden()
			.and().with(textField1)
				.action("view_component").resource(textfieldResource)
				.onDecisionEnableOrDisable()
			.build();
		// @formatter:on

		VerticalLayout multisubscriptionLayout = new VerticalLayout();
		multisubscriptionLayout.setMaxWidth("600px");
		multisubscriptionLayout.add(multiSubscriptionH3);

		// add info
		multisubscriptionLayout.add(
				Utilities.getInfoText("""
						access to buttons is granted/revoked in a 5 seconds interval by the policy "grant_access_to_buttons_on_singleAndMultisubscription_page".
						  - decision is enforced on the 1. Button by disabling/enabling the button
						  - decision is enforced on the 2. Button by hiding/showing the button"""),
				Utilities.getInfoText("""
						access to TextBox is granted/revoked in a 6 seconds interval by the policy "grant_access_to_textfields_on_singleAndMultisubscription_page".
						  - decision is enforced on the TextField by disabling/enabling the TextField"""));

		// add and format components
		multisubscriptionLayout.add(button1);
		button1.setWidth("100%");
		multisubscriptionLayout.add(button2);
		button2.setWidth("100%");
		multisubscriptionLayout.add(textField1);
		textField1.setWidth("100%");

		return multisubscriptionLayout;
	}
}