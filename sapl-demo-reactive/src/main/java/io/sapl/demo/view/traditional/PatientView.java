package io.sapl.demo.view.traditional;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import io.sapl.api.SAPLAuthorizer;
import io.sapl.demo.domain.PatientRepo;
import io.sapl.demo.view.AbstractPatientForm;
import io.sapl.demo.view.AbstractPatientView;


@SpringComponent("traditionalPatientView")
@SpringView(name = "traditional")
public class PatientView extends AbstractPatientView {

    @Override
    protected AbstractPatientForm createForm(AbstractPatientForm.RefreshCallback refreshCallback, PatientRepo patientRepo, SAPLAuthorizer authorizer) {
        return new PatientForm(refreshCallback, patientRepo, authorizer);
    }
}
