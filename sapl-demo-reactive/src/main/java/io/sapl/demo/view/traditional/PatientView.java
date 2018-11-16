package io.sapl.demo.view.traditional;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import io.sapl.demo.domain.Patient;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.demo.security.SecurityUtils;
import io.sapl.spring.SAPLAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringComponent("traditionalPatientView")
@SpringView(name = "traditional")
public class PatientView extends VerticalLayout implements View {

    @Autowired
    private SAPLAuthorizer authorizer;

    @Autowired
    private PatientRepo patientRepo;

    private Grid<Patient> grid;

    public PatientView() {
        setMargin(true);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final PatientForm form = new PatientForm(this::refresh, patientRepo, authorizer);
        form.setSizeFull();
        form.setVisible(false);

        grid = new Grid<>(Patient.class);
        grid.setColumns("id", "name");
        grid.getColumn("id").setWidth(75);
        grid.setSizeFull();
        grid.setItems(loadAllPatients());
        grid.asSingleSelect().addValueChangeListener(selection -> {
            if (selection.getValue() == null) {
                form.setVisible(false);
            } else {
                form.setPatient(selection.getValue(), false);
            }
        });

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Button addPatientBtn = new Button("Add new patient");
        addPatientBtn.addClickListener(e -> {
            if (authorizer.authorize(authentication, "create", "profile")) {
                grid.asSingleSelect().clear();
                form.setPatient(new Patient(), true);
            } else {
                SecurityUtils.notifyNotAuthorized();
            }
        });
        addPatientBtn.setVisible(authorizer.wouldAuthorize(authentication, "create", "profile"));

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
}
