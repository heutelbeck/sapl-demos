package org.demo.view.traditional;

import org.demo.service.UIController;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

@SpringView(name = "traditional")
@SpringComponent("traditionalPatientView")
public class PatientView extends AbstractPatientView {

	@Autowired
	public PatientView(UIController controller) {
		super(controller);
	}

	@Override
	protected AbstractPatientForm createForm(UIController uiController,
			AbstractPatientForm.RefreshCallback refreshCallback) {
		return new PatientForm(uiController, refreshCallback);
	}

}
