package io.sapl.demo.view.reactivemultirequest;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@SpringComponent("reactiveMultiRequestView")
@SpringView(name = "reactiveMultiRequest")
public class ReactiveMultiRequestView extends VerticalLayout implements View {

    public ReactiveMultiRequestView() {
        Notification.show("Not yet implemented", Notification.Type.HUMANIZED_MESSAGE);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
