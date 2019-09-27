package org.demo.view.traditional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.demo.domain.Patient;
import org.demo.model.PatientListItem;
import org.demo.security.SecurityUtils;
import org.demo.service.UIController;
import org.demo.view.traditional.AbstractPatientForm.RefreshCallback;
import org.demo.view.traditional.singlerequest.SingleRequestStreamManager;
import org.springframework.security.access.AccessDeniedException;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * Abstract base class for patient views.
 */
public abstract class AbstractPatientView extends VerticalLayout implements View {

	private UIController controller;

	protected AbstractPatientView(UIController controller) {
		this.controller = controller;
		setMargin(true);
	}

	protected abstract AbstractPatientForm createForm(UIController uiController, RefreshCallback refreshCallback);

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		final Grid<PatientListItem> grid = new Grid<>(PatientListItem.class);
		grid.setColumns("id", "name");
		grid.getColumn("id").setWidth(75);
		grid.setSizeFull();
		grid.setItems(loadAllPatients());

		final AbstractPatientForm form = createForm(controller, () -> grid.setItems(loadAllPatients()));
		form.setSizeFull();
		form.setVisible(false);

		grid.asSingleSelect().addValueChangeListener(selection -> {
			if (selection.getValue() == null) {
				form.hide();
			}
			else {
				final Optional<Patient> patient = controller.getPatient(selection.getValue().getId());
				patient.ifPresent(form::show);
			}
		});

		final Button addPatientBtn = new Button("Add new patient");
		addPatientBtn.setData("ui:view:patients:addPatientButton");
		addPatientBtn.addClickListener(e -> {
			grid.asSingleSelect().clear();
			form.show(new Patient());
		});
		addPatientBtn.setVisible(isPermitted("use", addPatientBtn));

		final HorizontalLayout main = new HorizontalLayout(grid, form);
		main.setSizeFull();
		main.setExpandRatio(grid, 0.4f);
		main.setExpandRatio(form, 0.6f);

		addComponents(addPatientBtn, main);
	}

	private List<PatientListItem> loadAllPatients() {
		try {
			return controller.getPatients();
		}
		catch (AccessDeniedException e) {
			SecurityUtils.notifyNotAuthorized();
			return Collections.emptyList();
		}
	}

	private boolean isPermitted(String action, AbstractComponent resource) {
		final SingleRequestStreamManager streamManager = getSession().getAttribute(SingleRequestStreamManager.class);
		return streamManager.isAccessPermitted(action, resource.getData());
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		event.navigate();
	}

}
