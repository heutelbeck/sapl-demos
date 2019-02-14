package org.demo.view.blocking;

import org.demo.domain.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.pep.BlockingSAPLAuthorizer;
import io.sapl.pep.SAPLAuthorizer;


@SpringComponent("traditionalPatientView")
@SpringView(name = "traditional")
public class PatientView extends AbstractPatientView {

    @Autowired
    public PatientView(SAPLAuthorizer authorizer, PatientRepo patientRepo) {
        super(authorizer, patientRepo);
    }

    @Override
    protected AbstractPatientForm createForm(AbstractPatientForm.RefreshCallback refreshCallback, PatientRepo patientRepo, BlockingSAPLAuthorizer authorizer) {
        return new PatientForm(refreshCallback, patientRepo, authorizer);
    }
}
