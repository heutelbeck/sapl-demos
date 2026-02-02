package io.sapl.demo.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

import java.io.Serial;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    @Serial
    private static final long serialVersionUID = SaplVersion.VERSION_UID;

    public MainLayout() {
        H1 appName = new H1("SAPL Demo Web Editor");
        appName.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.LARGE);

        Tabs tabs = createTabs();

        HorizontalLayout header = new HorizontalLayout(appName, tabs);
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);

        addToNavbar(header);
    }

    private Tabs createTabs() {
        Tabs tabs = new Tabs();
        tabs.add(createTab("SAPL Editor", VaadinIcon.EDIT, SaplLspEditorView.class));
        tabs.add(createTab("SAPL Test Editor", VaadinIcon.FILE_CODE, SaplTestLspEditorView.class));
        tabs.add(createTab("JSON Editor", VaadinIcon.CURLY_BRACKETS, JsonEditorView.class));
        tabs.add(createTab("Graph Visualization", VaadinIcon.CLUSTER, GraphVisualizationView.class));
        return tabs;
    }

    private Tab createTab(String title, VaadinIcon icon, Class<? extends Component> viewClass) {
        RouterLink link = new RouterLink();
        link.add(icon.create());
        link.add(title);
        link.setRoute(viewClass);
        return new Tab(link);
    }

}
