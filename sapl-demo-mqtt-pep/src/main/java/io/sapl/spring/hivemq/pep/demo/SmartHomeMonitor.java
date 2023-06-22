/*
 * Copyright © 2019-2022 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sapl.spring.hivemq.pep.demo;

import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.communication.PushMode;
import reactor.core.Disposable;
import reactor.core.Disposables;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Optional;

@Route(value = "")
@PageTitle("SmartHomeMonitor")
public class SmartHomeMonitor extends VerticalLayout {
	
	static final String BADGE_DESIGN_BLUE       = "badge";
    static final String BADGE_DESIGN_ERROR      = "badge error";
    static final String BADGE_DESIGN_SUCCESS    = "badge success";
    static final String BADGE_DESIGN_UNKNOWN    = "badge contrast";

    public SmartHomeMonitor(MqttClientService mqttSettingsClient, MqttClientService mqttHeatingStatusClient,
                            MqttClientService mqttSecurityCamStatusClient, MqttClientService mqttDoorLockStatusClient) {

        // heating status
        HorizontalLayout heatingStatusLayout        = buildStatusMonitor("Heating: ",
                mqttHeatingStatusClient, "heating_status", "on", "off");
        // security cam status
        HorizontalLayout securityCamStatusLayout    = buildStatusMonitor("Security cam: ",
                mqttSecurityCamStatusClient, "security_cam_status", "on", "off");
        // door lock status
        HorizontalLayout doorLockStatusLayout       = buildStatusMonitor("Door lock: ",
                mqttDoorLockStatusClient, "door_lock_status/#", "open", "closed");
        // heating setting
        HorizontalLayout heatingSettingLayout       = buildHeatingSettingMonitor(mqttSettingsClient);
        // security cam setting
        HorizontalLayout securityCamSettingLayout   = buildSecurityCamSettingMonitor(mqttSettingsClient);

        var header          = new H1("Your smart home environment");
        var statusHeader    = new H2("Status:");
        var settingsHeader  = new H2("Settings:");
        var statusLayout    = new HorizontalLayout(statusHeader);
        var settingsLayout  = new HorizontalLayout(settingsHeader);

        setMargin(true);
        add(header, statusLayout, heatingStatusLayout, securityCamStatusLayout, doorLockStatusLayout,
                settingsLayout, heatingSettingLayout, securityCamSettingLayout);
    }

    private HorizontalLayout buildSecurityCamSettingMonitor(MqttClientService mqttClientService) {
        Span securityCamSettingHeader                           = new Span("Security cam: ");
        RadioButtonGroup<String> securityCamSettingButtonGroup  =
                buildSettingButtonGroupMonitor(mqttClientService, "security_cam_status");
        return new HorizontalLayout(securityCamSettingHeader, securityCamSettingButtonGroup);
    }

    private HorizontalLayout buildHeatingSettingMonitor(MqttClientService mqttClientService) {
        Span heatingSettingHeader                           = new Span("Heating: ");
        RadioButtonGroup<String> heatingSettingButtonGroup  =
                buildSettingButtonGroupMonitor(mqttClientService, "heating_status");
        heatingSettingButtonGroup.setValue("on");
        return new HorizontalLayout(heatingSettingHeader, heatingSettingButtonGroup);
    }

    private RadioButtonGroup<String> buildSettingButtonGroupMonitor(MqttClientService mqttClientService,
                                                                    String statusTopic) {
        RadioButtonGroup<String> heatingSettingButtonGroup = new RadioButtonGroup<>();
        heatingSettingButtonGroup.setItems("on", "off");
        var publishers = Disposables.composite();
        heatingSettingButtonGroup.addValueChangeListener(statusEvent -> publishers.add(
                subscribeSettingPublisher(statusEvent, statusTopic, mqttClientService)));
        heatingSettingButtonGroup.addDetachListener(detach -> publishers.dispose());
        return heatingSettingButtonGroup;
    }

    private Disposable subscribeSettingPublisher(AbstractField.ComponentValueChangeEvent<RadioButtonGroup<String>,
            String> statusEvent, String statusTopic, MqttClientService mqttClientService) {
        return mqttClientService
                .publish(statusTopic, statusEvent.getSource().getValue(), true)
                .subscribe();
    }

    private HorizontalLayout buildStatusMonitor(String statusMonitorName , MqttClientService mqttClientService,
                                                String statusTopic, String on, String off) {
        Span statusHeader   = new Span(statusMonitorName);
        Span statusBadge    = new Span("unknown");
        statusBadge.getElement().getThemeList().add(BADGE_DESIGN_UNKNOWN);
        statusBadge.addAttachListener(attach ->
                subscribeStatusAndChangeReactive(statusBadge, mqttClientService, statusTopic, on, off));
        return new HorizontalLayout(statusHeader, statusBadge);
    }

    private void subscribeStatusAndChangeReactive(Span span, MqttClientService mqttClientService,
                                                  String topic, String positiveState, String negativeState) {
        Optional<UI> ui = getUI();
        if (ui.isEmpty()) {
            throw new IllegalStateException("Access to the UI is not possible.");
        }
        ui.get().getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
        mqttClientService.getMqttClient()
                .subscribe(MqttClientService.buildMqttSubscribeMessage(topic, 0),
                        message -> ui.get().access(() ->
                                changeBadgeAppearance(span, positiveState, negativeState, message)));
    }

    private void changeBadgeAppearance(Span span, String on, String off, Mqtt5Publish message) {
        Optional<ByteBuffer> payloadBuffer = message.getPayload();
        if (payloadBuffer.isEmpty()) {
            return;
        }
        String statusMessage = Charset.defaultCharset().decode(payloadBuffer.get()).toString();
        span.setText(statusMessage);
        if (statusMessage.equals(on)) {
            span.getElement().getThemeList().clear();
            span.getElement().getThemeList().add(BADGE_DESIGN_SUCCESS);
        } else if (statusMessage.equals(off)) {
            span.getElement().getThemeList().clear();
            span.getElement().getThemeList().add(BADGE_DESIGN_ERROR);
        } else {
            span.getElement().getThemeList().clear();
            span.getElement().getThemeList().add(BADGE_DESIGN_BLUE);
        }
    }
}