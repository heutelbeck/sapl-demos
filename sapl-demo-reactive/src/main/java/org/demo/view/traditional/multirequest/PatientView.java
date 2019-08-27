package org.demo.view.traditional.multirequest;

import org.demo.service.UIController;
import org.demo.view.traditional.AbstractPatientForm;
import org.demo.view.traditional.AbstractPatientView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

@SpringView(name = "multiRequest")
@SpringComponent("multiRequestPatientView")
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
