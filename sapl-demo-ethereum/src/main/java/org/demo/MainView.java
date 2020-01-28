package org.demo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.demo.domain.PrinterUser;
import org.demo.domain.PrinterUserService;
import org.demo.pip.EthereumPrinterPip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import io.sapl.api.functions.FunctionException;
import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PDPConfigurationException;
import io.sapl.api.pip.AttributeException;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint;
import io.sapl.pdp.embedded.EmbeddedPolicyDecisionPoint.Builder.IndexType;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and use @Route annotation to announce it in a URL as a
 * Spring managed bean. Use the @PWA annotation make the application installable on phones, tablets and some desktop
 * browsers.
 * <p>
 * A new instance of this class is created for every new user and every browser tab/window.
 */
@Slf4j
@Route
@PWA(name = "Sapl Ethereum Printer Application", shortName = "Sapl Ethereum App", description = "This is an application showing the use of Sapl with Ethereum.", enableInstallPrompt = true)
@CssImport("./styles/shared-styles.css")
@StyleSheet("frontend://styles/styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

	private static final long serialVersionUID = -5506530757803376574L;

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private PrinterUserForm form = new PrinterUserForm(this);

	private PrinterUserService printerUserService;

	private Grid<PrinterUser> grid = new Grid<>(PrinterUser.class);

	private TextField gridFilterText = new TextField();

	private Button addUserButton = new Button("Add new user");

	public MainView(@Autowired PrintService service, @Autowired PrinterUserService printerUserService) {
		this.printerUserService = printerUserService;
		addClassName("main-view");

		H1 header = new H1("3D Printer Control Panel");
		header.getElement().getThemeList().add("dark");
		add(header);

		TextField textField = new TextField("Issue certificate");
		textField.setPlaceholder("Enter Ethereum address...");
		textField.setClearButtonVisible(true);

		Button printerButton = new Button("Start printer", e -> Notification.show(service.print(textField.getValue())));
		printerButton.setEnabled(false);
		printerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		grid.setColumns("username", "ethereumAddress");
		grid.asSingleSelect().addValueChangeListener(event -> form.setPrinterUser(grid.asSingleSelect().getValue()));
		updateList();

		form.setPrinterUser(null);

		gridFilterText.setPlaceholder("Search...");
		gridFilterText.setClearButtonVisible(true);
		gridFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		gridFilterText.addValueChangeListener(e -> updateList());

		addUserButton.addClickListener(e -> {
			grid.asSingleSelect().clear();
			form.setPrinterUser(new PrinterUser("", "", "", Collections.emptyList()));
		});

		HorizontalLayout searchAndAdd = new HorizontalLayout(gridFilterText, addUserButton);

		VerticalLayout buttonField = new VerticalLayout(textField, printerButton);

		HorizontalLayout userManagement = new HorizontalLayout(grid, form);
		grid.setSizeFull();
		userManagement.setSizeFull();

		add(searchAndAdd);
		add(userManagement);
		add(buttonField);

		setSizeFull();

		String path = "src/main/resources";
		File file = new File(path);
		String absolutePath = file.getAbsolutePath();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUserName = authentication.getName();
		PrinterUser user = printerUserService.loadUser(currentUserName);

		EmbeddedPolicyDecisionPoint pdp;
		try {
			pdp = EmbeddedPolicyDecisionPoint.builder()
					.withFilesystemPDPConfigurationProvider(absolutePath + "/policies")
					.withFilesystemPolicyRetrievalPoint(absolutePath + "/policies", IndexType.SIMPLE)
					.withPolicyInformationPoint(new EthereumPrinterPip()).build();

			AuthorizationSubscription authzSubscription = new AuthorizationSubscription(
					JSON.textNode(user.getEthereumAddress()), JSON.textNode("print"), JSON.textNode("printer3D"), null);
			LOGGER.info("{}", authzSubscription);

			final Flux<AuthorizationDecision> printerAccessDecision = pdp.decide(authzSubscription);

			printerAccessDecision.subscribe(decision -> {
				getUI().ifPresent(ui -> ui.access(() -> {
					if (Decision.PERMIT.equals(decision.getDecision()))
						printerButton.setEnabled(true);
					else
						printerButton.setEnabled(false);
				}));
			});
		}
		catch (IOException | URISyntaxException | PolicyEvaluationException | PDPConfigurationException
				| AttributeException | FunctionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void updateList() {
		grid.setItems(printerUserService.findAll());
	}

}
