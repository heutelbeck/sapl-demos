package org.demo;

import org.demo.domain.PrinterUser;
import org.demo.domain.PrinterUserService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class PrinterUserForm extends FormLayout {

	private static final long serialVersionUID = -7109927158545674141L;

	private Binder<PrinterUser> binder = new Binder<>(PrinterUser.class);

	private TextField firstName = new TextField("First name");

	private TextField lastName = new TextField("Last name");

	private TextField ethereumAddress = new TextField("Ethereum address");

	private DatePicker birthDate = new DatePicker("Birthdate");

	private Button save = new Button("Save");

	private Button delete = new Button("Delete");

	private MainView mainView;

	private PrinterUserService service = PrinterUserService.getInstance();

	public PrinterUserForm(MainView mainView) {
		this.mainView = mainView;

		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickListener(event -> save());

		delete.addClickListener(event -> delete());

		binder.bindInstanceFields(this);

		HorizontalLayout buttons = new HorizontalLayout(save, delete);

		add(firstName, lastName, ethereumAddress, birthDate, buttons);

	}

	public void setPrinterUser(PrinterUser printerUser) {
		binder.setBean(printerUser);

		if (printerUser == null) {
			setVisible(false);
		}
		else {
			setVisible(true);
			firstName.focus();
		}
	}

	private void save() {
		PrinterUser printerUser = binder.getBean();
		service.save(printerUser);
		mainView.updateList();
		setPrinterUser(null);
	}

	private void delete() {
		PrinterUser printerUser = binder.getBean();
		service.delete(printerUser);
		mainView.updateList();
		setPrinterUser(null);
	}

}
