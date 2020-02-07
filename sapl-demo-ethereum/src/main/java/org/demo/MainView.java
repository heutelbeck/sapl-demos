package org.demo;

import java.util.LinkedHashSet;
import java.util.Set;

import org.demo.domain.PrinterUser;
import org.demo.domain.PrinterUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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

import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.api.pdp.Decision;
import io.sapl.api.pdp.PolicyDecisionPoint;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

/**
 * The main view groups together all features of the demo application.
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

	private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private static final String GRAFTEN_IMAGE = "https://cdn.pixabay.com/photo/2017/10/13/15/39/printer-2847967_960_720.jpg";

	private static final String ULTIMAKER_IMAGE = "https://cdn.pixabay.com/photo/2016/06/13/21/33/printer-1455169_960_720.jpg";

	private static final String ZMORPH_IMAGE = "https://cdn.pixabay.com/photo/2019/07/19/07/18/printer-4348151_960_720.jpg";

	private static final String ROBOT_IMAGE = "https://cdn.pixabay.com/photo/2019/02/10/06/10/robot-3986545_960_720.jpg";

	private static final String BOAT_IMAGE = "https://cdn.pixabay.com/photo/2017/08/25/19/45/korablik-2681190_960_720.jpg";

	private static final String ROCKET_IMAGE = "https://cdn.pixabay.com/photo/2017/06/02/10/36/rocket-2365907_960_720.jpg";

	private static final String BALL_IMAGE = "https://cdn.pixabay.com/photo/2015/01/12/18/15/ball-597523_960_720.jpg";

	private static final String CUBES_IMAGE = "https://cdn.pixabay.com/photo/2019/04/17/17/55/calibration-cube-4134916_960_720.jpg";

	private static final ObjectMapper mapper = new ObjectMapper();

	private Set<String> disabledItems = new LinkedHashSet<>();

	private PrinterUser user;

	private String currentPrinterImage;

	private Button printerButton;

	private Select<String> printerSelect;

	private Select<String> templateSelect;

	private Span printerStatus;

	private Disposable printerDisposable;

	private PolicyDecisionPoint pdp;

	public MainView(PrintService service, PrinterUserService printerUserService, PolicyDecisionPoint pdp) {
		this.pdp = pdp;
		addClassName("main-view");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUserName = authentication.getName();
		user = printerUserService.loadUser(currentUserName);

		currentPrinterImage = ULTIMAKER_IMAGE;

		H1 title = new H1("3D Printer Control Panel");
		Button logout = new Button("Logout");
		logout.setWidth("10%");
		logout.addClickListener(e -> {
			getUI().ifPresent(ui -> {
				ui.getPage().setLocation("/logout");
				ui.getSession().close();
			});

		});
		HorizontalLayout header = new HorizontalLayout(title, logout);
		header.getThemeList().add("dark");
		header.addClassName("main-header");
		add(header);

		Image printerImage = new Image();
		printerImage.setSrc(currentPrinterImage);
		printerImage.setSizeFull();

		templateSelect = new Select<>();
		templateSelect.setPlaceholder("Select template...");
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
		printerSelect.addValueChangeListener(event -> {
			String printer = event.getValue();
			switch (printer) {
			case ULTIMAKER:
				printerDisposable = printerAccessDecision();
				currentPrinterImage = ULTIMAKER_IMAGE;
				printerImage.setSrc(currentPrinterImage);
				templateSelect.setValue("");
				break;
			case GRAFTEN:
				printerDisposable = printerAccessDecision();
				currentPrinterImage = GRAFTEN_IMAGE;
				printerImage.setSrc(currentPrinterImage);
				templateSelect.setValue("");
				break;
			case ZMORPH:
				printerDisposable = printerAccessDecision();
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

		printerStatus = new Span();
		printerStatus.getStyle().set("margin-left", "33px");

		printerButton = new Button("Start printer", e -> {
			service.print(templateSelect.getValue());
			printerImage.setSrc(currentPrinterImage);
			templateSelect.setValue("");
		});
		printerButton.setEnabled(false);
		printerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		PrinterUserForm puForm = new PrinterUserForm(user, printerSelect);
		CrowdfundingForm cfForm = new CrowdfundingForm(user);
		PayForm payForm = new PayForm(user, this);

		VerticalLayout userAndCrowd = new VerticalLayout(puForm, printerStatus, payForm, cfForm);
		HorizontalLayout buttonField = new HorizontalLayout(templateSelect, printerButton);
		VerticalLayout printerForm = new VerticalLayout(printerSelect, buttonField);
		HorizontalLayout imageAndUser = new HorizontalLayout(printerImage, userAndCrowd);

		add(imageAndUser);
		add(printerForm);
		printerImage.setSizeFull();
		setSizeFull();

		printerDisposable = printerAccessDecision();
		crowdAccessDecision();
		paidAccessDecision();

	}

	public final Disposable paidAccessDecision() {
		AuthorizationSubscription sub = new AuthorizationSubscription(mapper.convertValue(user, JsonNode.class),
				JSON.textNode("access"), JSON.textNode("paidTemplate"), null);
		final Flux<AuthorizationDecision> paidAccess = pdp.decide(sub);
		Disposable dis = paidAccess.subscribe(decision -> {
			LOGGER.info("New paid access decision: {}", decision.getDecision());
			getUI().ifPresent(ui -> ui.access(() -> {
				if (Decision.PERMIT == decision.getDecision()) {
					disabledItems.remove(CUBES);
					System.out.println(disabledItems);
					templateSelect.setItemEnabledProvider(this::itemEnabledCheck);
					ui.push();
				}
			}));
		});
		return dis;
	}

	private Disposable crowdAccessDecision() {
		AuthorizationSubscription sub = new AuthorizationSubscription(JSON.textNode(user.getEthereumAddress()),
				JSON.textNode("access"), JSON.textNode("crowdTemplate"), null);
		final Flux<AuthorizationDecision> crowdAccess = pdp.decide(sub);
		Disposable dis = crowdAccess.subscribe(decision -> {
			LOGGER.info("New crowdfunding access decision: {}", decision.getDecision());
			getUI().ifPresent(ui -> ui.access(() -> {
				if (Decision.PERMIT == decision.getDecision()) {
					disabledItems.remove(BALL);
					templateSelect.setItemEnabledProvider(this::itemEnabledCheck);
					ui.push();
				}
				else {
					disabledItems.add(BALL);
					templateSelect.setItemEnabledProvider(this::itemEnabledCheck);
					ui.push();
				}
			}));
		});
		return dis;
	}

//	private EmbeddedPolicyDecisionPoint getPdp() throws IOException, URISyntaxException, PolicyEvaluationException,
//			PDPConfigurationException, AttributeException, FunctionException {
//		String path = "src/main/resources";
//		File file = new File(path);
//		String absolutePath = file.getAbsolutePath();
//
//		return EmbeddedPolicyDecisionPoint.builder().withFilesystemPDPConfigurationProvider(absolutePath + "/policies")
//				.withFilesystemPolicyRetrievalPoint(absolutePath + "/policies", IndexType.SIMPLE)
//				.withPolicyInformationPoint(new EthereumPrinterPip())
//				.withPolicyInformationPoint(new EthereumPolicyInformationPoint()).build();
//	}

	private Disposable printerAccessDecision() {
		if (printerDisposable != null)
			printerDisposable.dispose();
		AuthorizationSubscription sub = new AuthorizationSubscription(mapper.convertValue(user, JsonNode.class),
				JSON.textNode("start"), JSON.textNode(printerSelect.getValue()), null);
		LOGGER.info("New printer subscription: {}", sub);
		final Flux<AuthorizationDecision> printerAccessDecision = pdp.decide(sub);
		Disposable disposable = printerAccessDecision.subscribe(decision -> {
			LOGGER.info("New printer access decision: {}", decision.getDecision());
			getUI().ifPresent(ui -> ui.access(() -> {
				if (Decision.PERMIT == decision.getDecision()) {
					printerButton.setEnabled(true);
					printerStatus.setText("You are certified for the current printer.");
					printerStatus.getStyle().set("color", "green");
					ui.push();
				}
				else {
					printerButton.setEnabled(false);
					printerStatus.setText("You are not certified for the current printer.");
					printerStatus.getStyle().set("color", "red");
					ui.push();
				}
			}));
		});
		return disposable;

	}

	private boolean itemEnabledCheck(String item) {
		if (disabledItems.contains(item))
			return false;
		return true;
	}

}
