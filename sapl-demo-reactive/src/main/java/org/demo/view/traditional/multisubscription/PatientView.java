package org.demo.view.traditional.multisubscription;

import org.demo.service.UIController;
import org.demo.view.traditional.AbstractPatientForm;
import org.demo.view.traditional.AbstractPatientView;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;

@SpringView(name = "multiSubscription")
@SpringComponent("multiSubscriptionPatientView")
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
