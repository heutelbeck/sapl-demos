package org.demo;

import org.demo.domain.PrinterUser;
import org.demo.domain.PrinterUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and use @Route annotation to announce it in a URL as a
 * Spring managed bean. Use the @PWA annotation make the application installable on phones, tablets and some desktop
 * browsers.
 * <p>
 * A new instance of this class is created for every new user and every browser tab/window.
 */
@Route
@PWA(name = "Sapl Ethereum Printer Application", shortName = "Sapl Ethereum App", description = "This is an application showing the use of Sapl with Ethereum.", enableInstallPrompt = true)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

	private static final long serialVersionUID = -2277375623427884753L;

	private PrinterUserForm form = new PrinterUserForm(this);

	private PrinterUserService printerUserService = PrinterUserService.getInstance();

	private Grid<PrinterUser> grid = new Grid<>(PrinterUser.class);

	private TextField gridFilterText = new TextField();

	private Button addUserButton = new Button("Add new user");

	public MainView(@Autowired PrintService service) {

		TextField textField = new TextField("Issue certificate");
		textField.setPlaceholder("Enter Ethereum address...");
		textField.setClearButtonVisible(true);

		Button button = new Button("Start printer", e -> Notification.show(service.print(textField.getValue())));
		button.setEnabled(false);
		button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		grid.setColumns("firstName", "lastName", "ethereumAddress", "birthDate");
		grid.asSingleSelect().addValueChangeListener(event -> form.setPrinterUser(grid.asSingleSelect().getValue()));
		updateList();

		form.setPrinterUser(null);

		gridFilterText.setPlaceholder("Search...");
		gridFilterText.setClearButtonVisible(true);
		gridFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		gridFilterText.addValueChangeListener(e -> updateList());

		addUserButton.addClickListener(e -> {
			grid.asSingleSelect().clear();
			form.setPrinterUser(new PrinterUser());
		});

		HorizontalLayout searchAndAdd = new HorizontalLayout(gridFilterText, addUserButton);

		VerticalLayout buttonField = new VerticalLayout(textField, button);

		HorizontalLayout userManagement = new HorizontalLayout(grid, form);
		grid.setSizeFull();
		userManagement.setSizeFull();

		add(searchAndAdd);
		add(userManagement);
		add(buttonField);
		setSizeFull();

	}

	public void updateList() {
		grid.setItems(printerUserService.findAll());
	}

}
