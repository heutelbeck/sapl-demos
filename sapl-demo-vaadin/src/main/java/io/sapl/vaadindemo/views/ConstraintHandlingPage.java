package io.sapl.vaadindemo.views;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.StatusChangeListener;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.api.pdp.Decision;
import io.sapl.spring.constraints.api.ConsumerConstraintHandlerProvider;
import io.sapl.vaadin.PepBuilderService;
import io.sapl.vaadin.constraint.providers.FieldValidationConstraintHandlerProvider;
import io.sapl.vaadindemo.pizzaform.PizzaOrder;
import io.sapl.vaadindemo.shared.Utilities;
import jakarta.annotation.security.PermitAll;

/**
 *  This page demonstrates the Vaadin-Sapl constraint handling.
 */
@PermitAll
@PageTitle("Constraint Handling Page")
@Route(value = "constraint-handling-page", layout = MainLayout.class)
public class ConstraintHandlingPage extends VerticalLayout {

    private final IntegerField cheese = new IntegerField("Cheese Pizza");
    private final IntegerField veggie = new IntegerField("Veggie Pizza");
    private final IntegerField pepperoni = new IntegerField("Pepperoni Pizza");
    private final EmailField email = new EmailField("E-Mail");
    private final TimePicker time = new TimePicker("Delivery time");
    private final IntegerField beer = new IntegerField("+Beer 🍺");

    private final Button submit = new Button("Submit order");
    private final BeanValidationBinder<PizzaOrder> binder = new BeanValidationBinder<>(PizzaOrder.class);
    private boolean submitAllowed = false;
    private Integer minPizza = 0;
    private final PepBuilderService pepBuilderService;

    public ConstraintHandlingPage(PepBuilderService pepBuilderService, PizzaOrder data) {
        add(Utilities.getInfoText("This page demonstrates JSON-Schema-based constraint handling functionality " +
                "while using a form."));
        add(Utilities.getDefaultHeader("Constraint Handling Page"));

        this.pepBuilderService = pepBuilderService;
        // PepBuilderService is used to enforce policy decisions on Vaadin components or events
        setWidth(null);
        setHeightFull();
        final var pizzaImageHeight = "140px";

        // cheese pizza
        cheese.setValueChangeMode(ValueChangeMode.EAGER);
        var cheeseImage = new Image("images/cheese.jpg", "Cheese Pizza");
        cheeseImage.setHeight(pizzaImageHeight);
        var cheeseLayout = new VerticalLayout(cheeseImage, cheese);
        cheeseLayout.setAlignItems(Alignment.CENTER);

        // veggie pizza
        veggie.setValueChangeMode(ValueChangeMode.EAGER);
        var veggieImage = new Image("images/veggie.jpg", "Veggie Pizza");
        veggieImage.setHeight(pizzaImageHeight);
        var veggieLayout = new VerticalLayout(veggieImage, veggie);
        veggieLayout.setAlignItems(Alignment.CENTER);

        // pepperoni pizza
        pepperoni.setValueChangeMode(ValueChangeMode.EAGER);
        var pepperoniImage = new Image("images/salami.jpg", "Pepperoni Pizza");
        pepperoniImage.setHeight(pizzaImageHeight);
        var pepperoniLayout = new VerticalLayout(pepperoniImage, pepperoni);
        pepperoniLayout.setAlignItems(Alignment.CENTER);

        add(Utilities.getInfoText("The number of allowed pizza(s) is controlled by a time-based sapl policy and a local constraint handler provider."));

        // minPizza message
        NativeLabel lblMinPizza = new NativeLabel();

        // beer
        beer.setValueChangeMode(ValueChangeMode.EAGER);

        // current time
        NativeLabel lblTime = new NativeLabel();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                lblTime.getUI().ifPresent(
                        ui -> ui.access(() -> lblTime.setText("time: " + LocalDateTime.now().format(formatter))));
            }
        }, 0, 1000); // each second
        this.addDetachListener(event -> t.cancel());
        // add components to the UI
        add(new HorizontalLayout(cheeseLayout, veggieLayout, pepperoniLayout));
        add(lblMinPizza);
        email.setValueChangeMode(ValueChangeMode.EAGER);
        add(Utilities.getInfoText("Beer is only allowed between 0 seconds and 30 seconds. The beer-field use JSON-schema and SAPL policies for validation."));
        add(beer);
        add(lblTime);
        add(email);
        add(time);
        add(submit);

        // enable/disable button when valid or not
        submit.setEnabled(false);
        binder.addStatusChangeListener((StatusChangeListener) event -> {
            boolean minPizzaSelected = (
                    zeroIfNull(cheese.getValue()) +
                    zeroIfNull(pepperoni.getValue()) +
                    zeroIfNull(veggie.getValue())) >= minPizza;

            lblMinPizza.setText("You need to order at least " + minPizza + " pizza.");
            lblMinPizza.getStyle().set("color", minPizzaSelected ? "green" : "red");

            submit.setEnabled(submitAllowed && minPizzaSelected && binder.isValid());
        });

        // apply sapl field validation constraints for JSON-Schema-based validation
        pepBuilderService.with(this)
                .action("display")
                .resource("pizzaForm")
                .addConsumerConstraintHandlerProvider(
                        new FieldValidationConstraintHandlerProvider(binder, this)
                                .bindField(cheese)
                                .bindField(veggie)
                                .bindField(pepperoni)
                                .bindField(email)
                                .bindField(time)
                                .bindField(beer)
                )
                .addConsumerConstraintHandlerProvider(
                        new ConsumerConstraintHandlerProvider<>() {
                            @Override
                            public Consumer<UI> getHandler(JsonNode constraint) {
                                return params -> minPizza = constraint.get("min").asInt();
                            }

                            @Override
                            public boolean isResponsible(JsonNode constraint) {
                                if (constraint == null) {
                                    return false;
                                }
                                return constraint.has("type") &&
                                        "saplVaadin".equals(constraint.get("type").asText()) &&
                                        constraint.has("id") &&
                                        "minPizza".equals(constraint.get("id").asText());
                            }

                            @Override
                            public Class<UI> getSupportedType() {
                                return null;
                            }
                        }
                )
                .onDecisionDo(authorizationDecision ->
                        submitAllowed = authorizationDecision.getDecision() == Decision.PERMIT)
                .build();

        // additional validations need to be added before binding
        binder.bindInstanceFields(this);

        // position does not matter
        binder.setBean(data);

        this.enforceOpeningHours();
    }

    /**
     * Apply the Opening Hours Policy.
     * Shows a dialog when the site is visited outside opening hours.
     */
    private void enforceOpeningHours() {
        var resourceNode = JsonNodeFactory.instance.objectNode();
        resourceNode.put("startTime", "10:00");
        resourceNode.put("endTime", "16:00");
        this.pepBuilderService.with(this)
                .resource(resourceNode)
                .action("opening_hours_dialog")
                .onDecisionDo((authorizationDecision, component) -> {})
                .build();
    }

    private Integer zeroIfNull(Integer value){
        return value != null ? value : 0;
    }
}


