package org.demo.view.blocking.multirequest;

import org.demo.domain.PatientRepository;
import org.demo.view.blocking.AbstractPatientForm;
import org.demo.view.blocking.AbstractPatientView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.pep.BlockingSAPLAuthorizer;
import io.sapl.pep.SAPLAuthorizer;

@SpringComponent("multiRequestPatientView")
@SpringView(name = "multiRequest")
public class PatientView extends AbstractPatientView {

    @Autowired
    public PatientView(SAPLAuthorizer authorizer, PatientRepository patientRepo) {
        super(authorizer, patientRepo);
    }

    @Override
    protected AbstractPatientForm createForm(AbstractPatientForm.RefreshCallback refreshCallback, PatientRepository patientRepo, BlockingSAPLAuthorizer authorizer) {
        return new PatientForm(refreshCallback, patientRepo, authorizer);
    }
}
