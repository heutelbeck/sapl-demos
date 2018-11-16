package io.sapl.demo.service;

import java.io.Serializable;

import com.vaadin.ui.Notification;
import io.sapl.spring.annotation.PdpAuthorize;
import org.springframework.stereotype.Service;

@Service
public class BackendService implements Serializable {

    @PdpAuthorize(action = "get", resource = "profiles")
    public void demonstrateUsageOfPdpAuthorizeAnnotation() {
        Notification.show("You are authorized to load the list of patients.", Notification.Type.HUMANIZED_MESSAGE);
    }
}
