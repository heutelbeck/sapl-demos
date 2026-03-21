/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
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
package io.sapl.demo.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import io.sapl.api.SaplVersion;
import io.sapl.demo.views.graph.GraphVisualizationView;
import io.sapl.demo.views.jsoneditor.JsonEditorView;
import io.sapl.demo.views.lsp.SaplLspEditorView;
import io.sapl.demo.views.sapltesteditor.SaplTestLspEditorView;
import io.sapl.vaadin.theme.ThemeToggleButton;

import java.io.Serial;

/**
 * Main application layout with navigation tabs and a sun/moon theme toggle.
 */
public class MainLayout extends AppLayout {

    @Serial
    private static final long serialVersionUID = SaplVersion.VERSION_UID;

    private final ThemeToggleButton themeToggle = new ThemeToggleButton();

    public MainLayout() {
        var appName = new H1("SAPL Demo Web Editor");
        appName.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.LARGE);

        themeToggle.addThemeToggleListener(event -> applyTheme(event.isDarkMode()));

        var header = new HorizontalLayout(appName, createTabs(), themeToggle);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);

        addToNavbar(header);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        attachEvent.getUI().getPage()
                .executeJs("return window.matchMedia('(prefers-color-scheme: dark)').matches")
                .then(Boolean.class, this::applyTheme);
    }

    private void applyTheme(boolean darkMode) {
        themeToggle.setDarkMode(darkMode);
        UI.getCurrent().getPage().setColorScheme(
                darkMode ? ColorScheme.Value.DARK : ColorScheme.Value.LIGHT);
        ComponentUtil.fireEvent(UI.getCurrent(), new ThemeChangedEvent(UI.getCurrent(), darkMode));
    }

    private Tabs createTabs() {
        var tabs = new Tabs();
        tabs.add(createTab("SAPL Editor", VaadinIcon.EDIT, SaplLspEditorView.class));
        tabs.add(createTab("SAPL Test Editor", VaadinIcon.FILE_CODE, SaplTestLspEditorView.class));
        tabs.add(createTab("JSON Editor", VaadinIcon.CURLY_BRACKETS, JsonEditorView.class));
        tabs.add(createTab("Graph Visualization", VaadinIcon.CLUSTER, GraphVisualizationView.class));
        return tabs;
    }

    private Tab createTab(String title, VaadinIcon icon, Class<? extends Component> viewClass) {
        var link = new RouterLink();
        link.add(icon.create());
        link.add(title);
        link.setRoute(viewClass);
        return new Tab(link);
    }
}
