package org.demo;

import org.demo.domain.PrinterUser;
import org.demo.domain.PrinterUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class PrinterUserForm extends FormLayout {

	private static final long serialVersionUID = 1949849828168713357L;

	private Binder<PrinterUser> binder = new Binder<>(PrinterUser.class);

	private TextField ethereumAddress = new TextField("Ethereum address");

	private Button save = new Button("Save");

	private Button delete = new Button("Delete");

	private MainView mainView;

	@Autowired
	private PrinterUserService service;

	public PrinterUserForm(MainView mainView) {
		this.mainView = mainView;

		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickListener(event -> save());

		delete.addClickListener(event -> delete());

		binder.bindInstanceFields(this);

		HorizontalLayout buttons = new HorizontalLayout(save, delete);

		add(ethereumAddress, buttons);

	}

	public void setPrinterUser(PrinterUser printerUser) {
		binder.setBean(printerUser);

		if (printerUser == null) {
			setVisible(false);
		}
		else {
			setVisible(true);
			ethereumAddress.focus();
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
