package org.demo.view.blocking;

import static io.sapl.api.pdp.Decision.PERMIT;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.demo.domain.Patient;
import org.demo.model.PatientListItem;
import org.demo.security.SecurityUtils;
import org.demo.service.UIController;
import org.springframework.security.access.AccessDeniedException;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import io.sapl.spring.PolicyEnforcementPoint;

public abstract class AbstractPatientView extends VerticalLayout implements View {

	private PolicyEnforcementPoint pep;
	private UIController controller;

	private Grid<PatientListItem> grid;

	protected AbstractPatientView(PolicyEnforcementPoint pep, UIController controller) {
		this.pep = pep;
		this.controller = controller;

		setMargin(true);
	}

	protected abstract AbstractPatientForm createForm(PolicyEnforcementPoint pep, UIController uiController,
			AbstractPatientForm.RefreshCallback refreshCallback);

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		final AbstractPatientForm form = createForm(pep, controller, this::refresh);
		form.setSizeFull();
		form.setVisible(false);

		grid = new Grid<>(PatientListItem.class);
		grid.setColumns("id", "name");
		grid.getColumn("id").setWidth(75);
		grid.setSizeFull();
		grid.setItems(loadAllPatients());
		grid.asSingleSelect().addValueChangeListener(selection -> {
			if (selection.getValue() == null) {
				form.hide();
			} else {
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
		addPatientBtn.setVisible(
			pep.enforce(SecurityUtils.getAuthentication(), "use", addPatientBtn.getData()).blockFirst() == PERMIT
		);

		final HorizontalLayout main = new HorizontalLayout(grid, form);
		main.setSizeFull();
		main.setExpandRatio(grid, 0.4f);
		main.setExpandRatio(form, 0.6f);

		addComponents(addPatientBtn, main);
	}

	private List<PatientListItem> loadAllPatients() {
		try {
			return controller.getPatients();
		} catch (AccessDeniedException e) {
			SecurityUtils.notifyNotAuthorized();
			return Collections.emptyList();
		}
	}

	private void refresh() {
		grid.setItems(loadAllPatients());
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		event.navigate();
	}
}
