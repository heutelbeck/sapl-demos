package org.demo;

import java.util.LinkedHashSet;
import java.util.Set;

import org.demo.domain.PrinterUser;
import org.demo.domain.PrinterUserService;
import org.demo.helper.AccessCertificate;
import org.demo.helper.EthConnect;
import org.demo.pep.VaadinPEP;
import org.demo.security.SecurityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.PolicyDecisionPoint;
import lombok.extern.slf4j.Slf4j;

/**
 * In the main view one can see all features of the demo application.
 */
@Push
@Slf4j
@Route
@PWA(name = "Sapl Ethereum Printer Application", shortName = "Sapl Ethereum App", description = "This is an application showing the use of Sapl with Ethereum.", enableInstallPrompt = true)
@CssImport("./styles/shared-styles.css")
@StyleSheet("frontend://styles/styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

	public static final String GRAFTEN = "Graften One";

	public static final String ULTIMAKER = "Ultimaker 2 Extended+";

	public static final String ZMORPH = "Zmorph VX";

	private static final long serialVersionUID = -5506530757803376574L;

	private static final String BALL = "Ball";

	private static final String ROCKET = "Rocket";

	private static final String BOAT = "Boat";

	private static final String ROBOT = "Robot";

	private static final String CUBES = "Cubes";

	private static final String GRAFTEN_IMAGE = "https://cdn.pixabay.com/photo/2017/10/13/15/39/printer-2847967_960_720.jpg";

	private static final String ULTIMAKER_IMAGE = "https://cdn.pixabay.com/photo/2016/06/13/21/33/printer-1455169_960_720.jpg";

	private static final String ZMORPH_IMAGE = "https://cdn.pixabay.com/photo/2019/07/19/07/18/printer-4348151_960_720.jpg";

	private static final String ROBOT_IMAGE = "https://cdn.pixabay.com/photo/2019/02/10/06/10/robot-3986545_960_720.jpg";

	private static final String BOAT_IMAGE = "https://cdn.pixabay.com/photo/2017/08/25/19/45/korablik-2681190_960_720.jpg";

	private static final String ROCKET_IMAGE = "https://cdn.pixabay.com/photo/2017/06/02/10/36/rocket-2365907_960_720.jpg";

	private static final String BALL_IMAGE = "https://cdn.pixabay.com/photo/2015/01/12/18/15/ball-597523_960_720.jpg";

	private static final String CUBES_IMAGE = "https://cdn.pixabay.com/photo/2019/04/17/17/55/calibration-cube-4134916_960_720.jpg";

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private final ObjectMapper mapper;

	private Set<String> disabledItems = new LinkedHashSet<>();

	private PrinterUser user;

	private String currentPrinterImage;

	private Button printerButton;

	private Select<String> printerSelect;

	private Select<String> templateSelect;

	private Span printerStatus;

	public MainView(PrintService service, PrinterUserService printerUserService, AccessCertificate accessCertificate,
			PolicyDecisionPoint pdp, ObjectMapper mapper, EthConnect ethConnect) {
		this.mapper = mapper;
		addClassName("main-view");

		user = SecurityUtils.getUser();

		H1 title = new H1("3D Printer Control Panel");
		Button logout = createLogoutButton();
		HorizontalLayout header = new HorizontalLayout(title, logout);
		header.getThemeList().add("dark");
		header.addClassName("main-header");
		add(header);

		currentPrinterImage = ULTIMAKER_IMAGE;
		Image printerImage = new Image();
		printerImage.setSrc(currentPrinterImage);
		printerImage.setClassName("image-size");

		templateSelect = new Select<>();
		templateSelect.setPlaceholder("Select template...");
		templateSelect.getStyle().set("margin-top", "33px");
		templateSelect.setItems(ROBOT, BOAT, ROCKET, CUBES, BALL);
		disabledItems.add(CUBES);
		disabledItems.add(BALL);
		templateSelect.setItemEnabledProvider(this::itemEnabledCheck);
		templateSelect.addValueChangeListener(event -> {
			String template = event.getValue();
			switch (template) {
			case ROBOT:
				printerImage.setSrc(ROBOT_IMAGE);
				break;
			case BOAT:
				printerImage.setSrc(BOAT_IMAGE);
				break;
			case ROCKET:
				printerImage.setSrc(ROCKET_IMAGE);
				break;
			case CUBES:
				printerImage.setSrc(CUBES_IMAGE);
				break;
			case BALL:
				printerImage.setSrc(BALL_IMAGE);
				break;
			default:
				printerImage.setSrc(currentPrinterImage);
			}

		});

		printerSelect = new Select<>();
		printerSelect.setLabel("Printer");
		printerSelect.setItems(ULTIMAKER, GRAFTEN, ZMORPH);
		printerSelect.setValue(ULTIMAKER);
		printerSelect.setWidth("210px");

		printerStatus = new Span();
		printerStatus.getStyle().set("margin-left", "33px");

		printerButton = new Button("Start printer", e -> {
			service.print(templateSelect.getValue());
			printerImage.setSrc(currentPrinterImage);
			templateSelect.setValue("");
		});
		printerButton.setEnabled(false);
		printerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		PrinterUserForm puForm = new PrinterUserForm(user, printerSelect, accessCertificate);
		CrowdfundingForm cfForm = new CrowdfundingForm(user, ethConnect);
		PayForm payForm = new PayForm(user);
		Button pay = payForm.getPay();

		printerButton.getStyle().set("margin-top", "37px");
		VerticalLayout userAndCrowd = new VerticalLayout(puForm, printerStatus, payForm, cfForm);
		HorizontalLayout buttonField = new HorizontalLayout(printerSelect, templateSelect, printerButton);
		VerticalLayout imageAndSelect = new VerticalLayout(printerImage, buttonField);
		HorizontalLayout imageAndUser = new HorizontalLayout(imageAndSelect, userAndCrowd);

		add(imageAndUser);
		setSizeFull();

		VaadinPEP<Select<String>> paymentPep = new VaadinPEP<Select<String>>(templateSelect, paidSub(), pdp,
				UI.getCurrent());
		paymentPep.onPermit((component, decision) -> {
			LOGGER.info("New paid access decision: {}", decision.getDecision());
			disabledItems.remove(CUBES);
			component.setItemEnabledProvider(this::itemEnabledCheck);
		});
		paymentPep.onDeny((component, decision) -> {
			LOGGER.info("New paid access decision: {}", decision.getDecision());
		});
		paymentPep.enforce();

		pay.addClickListener(event -> {
			ethConnect.makePayment(user, "1");
			paymentPep.newSub(paidSub());
			paymentPep.enforce();
		});

		VaadinPEP<Select<String>> crowdPep = new VaadinPEP<Select<String>>(templateSelect, crowdSub(), pdp,
				UI.getCurrent());
		crowdPep.onPermit((component, decision) -> {
			LOGGER.info("New crowd access decision: {}", decision.getDecision());
			disabledItems.remove(BALL);
			component.setItemEnabledProvider(this::itemEnabledCheck);
		});
		crowdPep.onDeny((component, decision) -> {
			LOGGER.info("New crowd access decision: {}", decision.getDecision());
		});
		crowdPep.enforce();

		VaadinPEP<Button> printerPep = new VaadinPEP<Button>(printerButton, printerSub(printerSelect.getValue()), pdp,
				UI.getCurrent());
		printerPep.onPermit((component, decision) -> {
			LOGGER.info("New printer access decision: {}", decision.getDecision());
			component.setEnabled(true);
			printerStatus.setText("You are certified for the current printer.");
			printerStatus.getStyle().set("color", "green");

		});
		printerPep.onDeny((component, decision) -> {
			LOGGER.info("New printer access decision: {}", decision.getDecision());
			component.setEnabled(false);
			printerStatus.setText("You are not certified for the current printer.");
			printerStatus.getStyle().set("color", "red");
		});
		printerPep.enforce();

		printerSelect.addValueChangeListener(event -> {
			String printer = event.getValue();
			switch (printer) {
			case ULTIMAKER:
				printerPep.newSub(printerSub(printer));
				printerPep.enforce();
				currentPrinterImage = ULTIMAKER_IMAGE;
				printerImage.setSrc(currentPrinterImage);
				templateSelect.setValue("");
				break;
			case GRAFTEN:
				printerPep.newSub(printerSub(printer));
				printerPep.enforce();
				currentPrinterImage = GRAFTEN_IMAGE;
				printerImage.setSrc(currentPrinterImage);
				templateSelect.setValue("");
				break;
			case ZMORPH:
				printerPep.newSub(printerSub(printer));
				printerPep.enforce();
				currentPrinterImage = ZMORPH_IMAGE;
				printerImage.setSrc(currentPrinterImage);
				templateSelect.setValue("");
				break;
			default:
				printerImage.setSrc(currentPrinterImage);
				templateSelect.setValue("");
				break;

			}
		});

		logout.addClickListener(e -> {
			getUI().ifPresent(ui -> {
				printerPep.dispose();
				paymentPep.dispose();
				crowdPep.dispose();
				SecurityContextHolder.clearContext();
				ui.getPage().setLocation("/logout");
				ui.getSession().close();
			});

		});

	}

	private AuthorizationSubscription buildSubscription(String action, String resource) {
		return new AuthorizationSubscription(mapper.convertValue(user, JsonNode.class), JSON.textNode(action),
				JSON.textNode(resource), null);
	}

	private boolean itemEnabledCheck(String item) {
		if (disabledItems.contains(item))
			return false;
		return true;
	}

	private Button createLogoutButton() {
		Button logout = new Button("Logout");
		logout.setWidth("10%");
		return logout;
	}

	private AuthorizationSubscription paidSub() {
		return buildSubscription("access", "paidTemplate");
	}

	private AuthorizationSubscription crowdSub() {
		return buildSubscription("access", "crowdTemplate");
	}

	private AuthorizationSubscription printerSub(String printer) {
		return buildSubscription("start", printer);
	}

}
