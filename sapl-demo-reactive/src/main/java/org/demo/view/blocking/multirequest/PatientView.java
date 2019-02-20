package org.demo.view.blocking.multirequest;

import org.demo.domain.PatientRepository;
import org.demo.view.blocking.AbstractPatientForm;
import org.demo.view.blocking.AbstractPatientView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

import io.sapl.api.pdp.PolicyDecisionPoint;
import io.sapl.spring.PolicyEnforcementPoint;

@SpringView(name = "multiRequest")
@SpringComponent("multiRequestPatientView")
public class PatientView extends AbstractPatientView {

    private PolicyDecisionPoint pdp;

	@Autowired
    public PatientView(PolicyEnforcementPoint pep, PatientRepository patientRepo, PolicyDecisionPoint pdp) {
        super(pep, patientRepo);
        this.pdp = pdp;
    }

    @Override
    protected AbstractPatientForm createForm(AbstractPatientForm.RefreshCallback refreshCallback, PatientRepository patientRepo, PolicyEnforcementPoint pep) {
        return new PatientForm(refreshCallback, patientRepo, pep, pdp);
    }
}
