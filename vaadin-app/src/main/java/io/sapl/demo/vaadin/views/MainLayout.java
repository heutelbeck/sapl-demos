package io.sapl.demo.vaadin.views;

import java.io.Serial;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;

import io.sapl.demo.vaadin.security.SecurityUtils;
import jakarta.annotation.security.PermitAll;

/**
 * The main view is a top-level placeholder for other views.
 */
@PermitAll
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    @Serial
    private static final long serialVersionUID = -5398634391624843809L;

    private static final String ITEMS_CENTER = "items-center";
    private H1 viewTitle;

    /**
     * Create main Layout
     */
    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("m-0", "text-l", "margin-top");

        Button logoutBtn = new Button();
        logoutBtn.addClassName("nav-logout-btn");
        logoutBtn.setText("Logout");
        logoutBtn.addClickListener(x -> SecurityUtils.logout());

        H4 userRole = new H4(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(authority -> authority.startsWith("ROLE_"))
                .collect(Collectors.joining(", ")));
        userRole.addClassName("nav-logout-btn");
        userRole.getStyle().set("margin-bottom", "18px");

        Header header = new Header(toggle, viewTitle, userRole, logoutBtn);
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", ITEMS_CENTER,
                "w-full");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("SAPL Vaadin Demo");
        appName.addClassNames("flex", ITEMS_CENTER, "h-xl", "m-0", "px-m", "text-m");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
        nav.getElement().setAttribute("aria-labelledby", "views");

        H3 views = new H3("Views");
        views.addClassNames("flex", "h-m", ITEMS_CENTER, "mx-m", "my-0", "text-s", "text-tertiary");
        views.setId("views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("list-none", "m-0", "p-0");
        nav.add(list);

        for (RouterLink link : createLinks()) {
            ListItem item = new ListItem(link);
            list.add(item);
        }
        return nav;
    }

    private List<RouterLink> createLinks() {
        MainLayout.MenuItemInfo[] menuItems = {                                       //
                new MainLayout.MenuItemInfo("Home Page", HomePage.class),                                //
                new MainLayout.MenuItemInfo("Single- and Multisubscription Page",
                        SingleAndMultisubscriptionPage.class),                                                           //
                new MainLayout.MenuItemInfo("Constraint Handling Page", ConstraintHandlingPage.class),   //
                new MainLayout.MenuItemInfo("Annotation Page", AnnotationPage.class),                    //
                new MainLayout.MenuItemInfo("Admin Page", AdminPage.class)                               //
        };
        List<RouterLink>          links          = new ArrayList<>(7);
        for (MainLayout.MenuItemInfo menuItemInfo : menuItems) {
            links.add(createLink(menuItemInfo));

        }
        return links;
    }

    private static RouterLink createLink(MainLayout.MenuItemInfo menuItemInfo) {
        RouterLink link = new RouterLink();
        link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
        link.setRoute(menuItemInfo.getView());

        var icon = VaadinIcon.GLOBE.create();
        icon.addClassNames("me-s", "text-l");

        Span text = new Span(menuItemInfo.getText());
        text.addClassNames("font-medium", "text-s");

        link.add(icon, text);
        return link;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("flex", ITEMS_CENTER, "my-s", "px-m", "py-xs");

        return layout;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    static class MenuItemInfo {

        private final String                     text;
        private final Class<? extends Component> view;

        public MenuItemInfo(String text, Class<? extends Component> view) {
            this.text = text;
            this.view = view;
        }

        public String getText() {
            return text;
        }

        public Class<? extends Component> getView() {
            return view;
        }

    }

}
