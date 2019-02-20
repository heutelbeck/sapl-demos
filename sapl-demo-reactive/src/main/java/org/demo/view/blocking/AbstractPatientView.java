package org.demo.view.blocking;

import java.util.ArrayList;
import java.util.List;

import org.demo.domain.Patient;
import org.demo.domain.PatientRepository;
import org.demo.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
	private PatientRepository patientRepo;

	private Grid<Patient> grid;

	protected AbstractPatientView(PolicyEnforcementPoint pep, PatientRepository patientRepo) {
		this.pep = pep;
		this.patientRepo = patientRepo;

		setMargin(true);
	}

	protected abstract AbstractPatientForm createForm(AbstractPatientForm.RefreshCallback refreshCallback,
			PatientRepository patientRepo, PolicyEnforcementPoint authorizer);

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		final AbstractPatientForm form = createForm(this::refresh, patientRepo, pep);
		form.setSizeFull();
		form.setVisible(false);

		grid = new Grid<>(Patient.class);
		grid.setColumns("id", "name");
		grid.getColumn("id").setWidth(75);
		grid.setSizeFull();
		grid.setItems(loadAllPatients());
		grid.asSingleSelect().addValueChangeListener(selection -> {
			if (selection.getValue() == null) {
				form.hide();
			} else {
				form.show(selection.getValue(), false);
			}
		});

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final Button addPatientBtn = new Button("Add new patient");
		addPatientBtn.addClickListener(e -> {
			if (pep.enforce(authentication, "create", "profile")) {
				grid.asSingleSelect().clear();
				form.show(new Patient(), true);
			} else {
				SecurityUtils.notifyNotAuthorized();
			}
		});
		addPatientBtn.setVisible(pep.enforce(authentication, "create", "profile"));

		final HorizontalLayout main = new HorizontalLayout(grid, form);
		main.setSizeFull();
		main.setExpandRatio(grid, 0.4f);
		main.setExpandRatio(form, 0.6f);

		addComponents(addPatientBtn, main);
	}

	private List<Patient> loadAllPatients() {
		final List<Patient> result = new ArrayList<>();
		final Iterable<Patient> allPatients = patientRepo.findAll();
		allPatients.forEach(result::add);
		return result;

	}

	private void refresh() {
		final List<Patient> patients = loadAllPatients();
		grid.setItems(patients);
	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		event.navigate();
	}
}
