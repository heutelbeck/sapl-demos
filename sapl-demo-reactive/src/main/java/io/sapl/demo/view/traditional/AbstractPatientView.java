package io.sapl.demo.view.traditional;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import io.sapl.demo.domain.Patient;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.demo.security.SecurityUtils;
import io.sapl.pep.BlockingSAPLAuthorizer;
import io.sapl.pep.SAPLAuthorizer;

public abstract class AbstractPatientView extends VerticalLayout implements View {

    private BlockingSAPLAuthorizer authorizer;
    private PatientRepo patientRepo;

    private Grid<Patient> grid;

    protected AbstractPatientView(SAPLAuthorizer authorizer, PatientRepo patientRepo) {
        this.authorizer = new BlockingSAPLAuthorizer(authorizer);
        this.patientRepo = patientRepo;

        setMargin(true);
    }

    protected abstract AbstractPatientForm createForm(AbstractPatientForm.RefreshCallback refreshCallback, PatientRepo patientRepo, BlockingSAPLAuthorizer authorizer);

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        final AbstractPatientForm form = createForm(this::refresh, patientRepo, authorizer);
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
            if (authorizer.authorize(authentication, "create", "profile")) {
                grid.asSingleSelect().clear();
                form.show(new Patient(), true);
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

    @Override
    public void beforeLeave(ViewBeforeLeaveEvent event) {
        authorizer.dispose();
        event.navigate();
    }
}
