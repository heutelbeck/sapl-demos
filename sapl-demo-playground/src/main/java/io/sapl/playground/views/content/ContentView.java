package io.sapl.playground.views.content;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.interpreter.DefaultSAPLInterpreter;
import io.sapl.interpreter.EvaluationContext;
import io.sapl.interpreter.SAPLInterpreter;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.playground.models.BasicExample;
import io.sapl.playground.models.Example;
import io.sapl.playground.models.MockingModel;
import io.sapl.playground.views.main.MainView;
import io.sapl.test.mocking.MockingAttributeContext;
import io.sapl.test.mocking.MockingFunctionContext;
import io.sapl.test.steps.AttributeMockReturnValues;
import io.sapl.vaadin.DocumentChangedEvent;
import io.sapl.vaadin.Issue;
import io.sapl.vaadin.JsonEditor;
import io.sapl.vaadin.JsonEditorConfiguration;
import io.sapl.vaadin.SaplEditor;
import io.sapl.vaadin.SaplEditorConfiguration;
import io.sapl.vaadin.ValidationFinishedEvent;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.Step;

@SuppressWarnings("serial")
@Route(value = "", layout = MainView.class)
@PageTitle("SAPL Playground")
@RouteAlias(value = "", layout = MainView.class)
@CssImport("./styles/views/content/content-view.css")
public class ContentView extends Div {
	//UI element references
	private SaplEditor saplEditor;
	
	private JsonEditor mockDefinitionEditor;
	private Paragraph mockDefinitionJsonInputError;
	
	private JsonEditor authSubEditor;
	private Paragraph authSubJsonInputError;
	
	private JsonEditor jsonOutput;
	private Paragraph evaluationError;

	//Internal global variables
	private final SAPLInterpreter saplInterpreter;
	private List<AttributeMockReturnValues> attrReturnValues;
	private final ObjectMapper objectMapper;
	private ArrayNode aggregatedResult;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
    public ContentView() {
    	
    	this.saplInterpreter = new DefaultSAPLInterpreter();
    	this.objectMapper = new ObjectMapper();
    	
        setId("content-view");
        
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        horizontalLayout.setId("dividePageHorizontal");
        
        horizontalLayout.add(createLeftSide());

        horizontalLayout.add(createRightSide());
		
        add(horizontalLayout);
        
        initalizeExample();
    }
    

	private Component createLeftSide() {
        Div leftSide = new Div();
        leftSide.setId("leftSide");
        
		Div saplEditorDiv = new Div();
		saplEditorDiv.setId("saplEditorDiv");
		SaplEditorConfiguration saplConfig = new SaplEditorConfiguration();
		saplConfig.setHasLineNumbers(true);
		saplConfig.setTextUpdateDelay(500);
		this.saplEditor = new SaplEditor(saplConfig);
		this.saplEditor.addDocumentChangedListener(event -> this.onSaplPolicyChanged(event));
		this.saplEditor.addValidationFinishedListener(this::onValidationFinished);
		saplEditorDiv.add(this.saplEditor);
		
        leftSide.add(this.saplEditor);		
		return leftSide;
	}
	
	private Component createRightSide() {
		Div rightSide = new Div();
        rightSide.setId("rightSide");
        
        VerticalLayout rightSideVertical = new VerticalLayout();
        rightSideVertical.setId("rightSideVertical");
		rightSideVertical.add(createRightUpperSide());
		rightSideVertical.add(createRightLowerSide());
        rightSide.add(rightSideVertical);
        
        return rightSide;
	}
	
	private Component createRightUpperSide() {
        Div div = new Div();
        div.setId("rightSideInputDiv");
		
        //Tab 1
        
        Tab tab1AuthSubInput = new Tab("AuthorizationSubscription");
        Div page1JsonEditorDiv = new Div();
        page1JsonEditorDiv.setId("jsonEditorDiv");
        JsonEditorConfiguration authSubEditorConfig = new JsonEditorConfiguration();
        authSubEditorConfig.setTextUpdateDelay(500);
		this.authSubEditor = new JsonEditor(authSubEditorConfig);
		this.authSubEditor.addDocumentChangedListener(event -> this.onAuthSubJsonInputChanged(event));
		page1JsonEditorDiv.add(this.authSubEditor);
		//div.add(jsonEditorDiv);
		
		this.authSubJsonInputError = new Paragraph("Input JSON is not valid");
		this.authSubJsonInputError.setVisible(false);
		this.authSubJsonInputError.setClassName("errorText");
		page1JsonEditorDiv.add(this.authSubJsonInputError);
		
		
		
		//Tab 2
		
		Tab tab2MockInput = new Tab("Mocks");
		Div page2MockInput = new Div();
		page2MockInput.setVisible(false);

        JsonEditorConfiguration mockJsonEditorConfig = new JsonEditorConfiguration();
        mockJsonEditorConfig.setTextUpdateDelay(500);
	    this.mockDefinitionEditor = new JsonEditor(mockJsonEditorConfig);
	    this.mockDefinitionEditor.addDocumentChangedListener(event -> this.onMockingJsonEditorInputChanged(event));
		page2MockInput.add(this.mockDefinitionEditor);
		
		this.mockDefinitionJsonInputError = new Paragraph("Input JSON is not valid");
		this.mockDefinitionJsonInputError.setVisible(false);
		this.mockDefinitionJsonInputError.setClassName("errorText");
		page2MockInput.add(this.mockDefinitionJsonInputError);

				
		
		//Tab 3
        
        Tab tab3MockHelpText = new Tab("Mock Format");
        Div page3MockHelpText = new Div();
        page3MockHelpText.setVisible(false);
        page3MockHelpText.setId("mockInputHelpTextDiv");
        page3MockHelpText.add(new Paragraph("Expecting an array of JSON objects, each object consisting of the following properties:"));
        page3MockHelpText.add(new Paragraph("\"type\" (Required): Allowed values are \"ATTRIBUTE\" or \"FUNCTION\""));
        page3MockHelpText.add(new Paragraph("\"importName\" (Required): The name the function or attribute is referenced in your policy (for example \"time.dayOfWeekFrom\")."));
        page3MockHelpText.add(new Paragraph("\"always\" (Optional): A JSON value to be returned by this attribute or to be returned by a function every time the function is called."));
        page3MockHelpText.add(new Paragraph("\"sequence\" (Optional): An array of JSON values to be returned by the attribute or function."));
        page3MockHelpText.add(new Paragraph("One of \"always\" or \"sequence\" is required"));

		//Icon helpLogo = new Icon(VaadinIcon.INFO_CIRCLE);
		//div.add(helpLogo);
		
		
		
		//Tab visible logic
		
		Map<Tab, Component> tabsToPages = new HashMap<>();
		tabsToPages.put(tab1AuthSubInput, page1JsonEditorDiv);
		tabsToPages.put(tab2MockInput, page2MockInput);
		tabsToPages.put(tab3MockHelpText, page3MockHelpText);
		Tabs tabs = new Tabs(tab1AuthSubInput, tab2MockInput, tab3MockHelpText);

		tabs.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
		    selectedPage.setVisible(true);
		});
		
		
		
		div.add(tabs, page1JsonEditorDiv, page2MockInput, page3MockHelpText);
		return div;
	}
	
	private Component createRightLowerSide() {
        Div div = new Div();
        div.setId("rightSideOutputDiv");
        

        Div jsonOutputDiv = new Div();
        jsonOutputDiv.setId("jsonOutputDiv");
        jsonOutput = new JsonEditor(new JsonEditorConfiguration());
		jsonOutputDiv.add(jsonOutput);
		div.add(jsonOutputDiv);
        
		this.evaluationError = new Paragraph();
		this.evaluationError.setVisible(false);
		this.evaluationError.setClassName("errorText");
		div.add(this.evaluationError);
		
		return div;
	}
	
	private void onValidationFinished(ValidationFinishedEvent event) {
		System.out.println("validation finished");
		Issue[] issues = event.getIssues();
		System.out.println("issue count: " + issues.length);
		for (Issue issue : issues) {
			System.out.println(issue.getDescription());
		}
	}
	
	private void initalizeExample() {
		 Example initExample = new BasicExample();
		 
		 this.saplEditor.setDocument(initExample.getPolicy());
		 this.authSubEditor.setDocument(initExample.getAuthSub());
		 this.mockDefinitionEditor.setDocument(initExample.getMockDefinition());
		 
		 evaluatePolicy();
	 }
	 

	    
    private void onMockingJsonEditorInputChanged(DocumentChangedEvent event) {
    	log.debug("Mock Json Editor changed");
    	this.mockDefinitionJsonInputError.setVisible(false);
		evaluatePolicy();
    }

    
    private void onAuthSubJsonInputChanged(DocumentChangedEvent event) {
    	log.debug("AuthSub Editor changed");
    	this.authSubJsonInputError.setVisible(false);		
		evaluatePolicy();
    }
   
    
    
    private void onSaplPolicyChanged(DocumentChangedEvent event) {
    	log.debug("Policy Editor changed");
		this.evaluationError.setVisible(false);
	
		var saplString = this.saplEditor.getDocument();
		if(saplString == null || saplString.isEmpty() || !this.saplInterpreter.analyze(saplString).isValid()) {
			this.evaluationError.setVisible(true);
			this.evaluationError.setText("Policy isn't valid!");
			return;
		}
		
		evaluatePolicy();
    }
    
    private void evaluatePolicy() {
    	log.debug("Evaluating Policy");
    	
    	this.evaluationError.setVisible(false);
    	
    	//initialize output
    	this.aggregatedResult = this.objectMapper.createArrayNode();
	
    	
    	//prepare policy evaluation
    	var ctx = getEvalContextForMockJson(this.mockDefinitionEditor.getDocument());
    	if(ctx == null)
    		return;
    	var authSub = getAuthSubForJsonString(this.authSubEditor.getDocument());
    	if(authSub == null)
    		return;
    	var sapl = getSAPLDocument(this.saplEditor.getDocument());
    	if(sapl == null)
    		return;
    	
    	
    	//check if authSub matches Policy
    	var ctxForAuthSub = ctx.forAuthorizationSubscription(authSub);
    	var matchesResult = sapl.matches(ctxForAuthSub).block();
    	if(!matchesResult.isBoolean()) {
    		this.evaluationError.setVisible(true);
			this.evaluationError.setText(matchesResult.toString());
			this.jsonOutput.setDocument("");
			return;    		
    	}
    	if(!matchesResult.getBoolean()) {
    		this.evaluationError.setVisible(true);
			this.evaluationError.setText("The policy does not match the AuthorizationSubscription!");
			this.jsonOutput.setDocument("");
			return;
    	}
    	
    	
    	//setup StepVerifier
		Step<AuthorizationDecision> steps = StepVerifier.create(sapl.evaluate(ctxForAuthSub));
		
		//emit TestPublishers in given order
		for (AttributeMockReturnValues mock : this.attrReturnValues) {
			String fullname = mock.getFullname();
			for (Val val : mock.getMockReturnValues()) {
				steps = steps.then(() -> ((MockingAttributeContext)ctx.getAttributeCtx()).mockEmit(fullname, val));
			}
		}
		
		//consume decisions
		for(int i = 0; i < countNumberOfExpectedDecisions(); i++) {
			steps = steps.consumeNextWith(consumeAuthDecision());
		}		
		
		//execute policy evaluation and catch AssertionErros
		try {
			steps.thenCancel().verify(Duration.ofSeconds(1));
	    	log.debug("Evaluation finished");
		} catch (AssertionError err) {
	    	log.debug("Evaluation error", err);
			this.evaluationError.setVisible(true);
			String errorMessage = "";
			for(Throwable t : err.getSuppressed()) {
				errorMessage = errorMessage + t.getMessage() + "; ";
			}
			this.evaluationError.setText(errorMessage);
		}
    }
    
    
    private SAPL getSAPLDocument(String saplString) {
		if(saplString == null || saplString.isEmpty() || !this.saplInterpreter.analyze(saplString).isValid()) {
			this.evaluationError.setVisible(true);
			this.evaluationError.setText("Policy isn't valid!");
			return null;
		} else {
			return this.saplInterpreter.parse(saplString);
		}
    }
    
    
	private EvaluationContext getEvalContextForMockJson(String json) {
		var attributeCtx = new MockingAttributeContext(new AnnotationAttributeContext());
		var functionCtx = new MockingFunctionContext(new AnnotationFunctionContext());
		var variables = new HashMap<String, JsonNode>(1);
		this.attrReturnValues = new LinkedList<>();
		
		
		JsonNode mockInput;
		try {
			mockInput = this.objectMapper.readTree(json);
		} catch (JsonProcessingException e) {
			this.mockDefinitionJsonInputError.setText("Cannot parse JSON!");
			this.mockDefinitionJsonInputError.setVisible(true);
			return null;
		}
		
		List<MockingModel> mocks = MockingModel.parseMockingJsonInputToModel(mockInput, this.mockDefinitionJsonInputError);
		if(mocks == null) {
			return null;
		}
		
		for(var mock : mocks) {
			switch (mock.getType()) {
			case ATTRIBUTE:
				if(mock.getAlways() != null) {
					attributeCtx.markAttributeMock(mock.getImportName());
					this.attrReturnValues.add(AttributeMockReturnValues.of(mock.getImportName(), new Val[]{mock.getAlways()}));
				} else {
					attributeCtx.markAttributeMock(mock.getImportName());
					this.attrReturnValues.add(AttributeMockReturnValues.of(mock.getImportName(), mock.getSequence().toArray(new Val[0])));
				}
				break;
			case FUNCTION:
				if(mock.getAlways() != null) {
					functionCtx.loadFunctionMockAlwaysSameValue(mock.getImportName(), mock.getAlways());
				} else {
					functionCtx.loadFunctionMockReturnsSequence(mock.getImportName(), mock.getSequence().toArray(new Val[0]));
				}
				break;
			default:
				break;
			}
		}
		
		return new EvaluationContext(attributeCtx, functionCtx, variables);
	}
	
	 
    private AuthorizationSubscription getAuthSubForJsonString(String jsonInputString) {
    	JsonNode jsonInput = null;
		if(jsonInputString == null) {
			return null;
		}
		try {
			jsonInput = objectMapper.readTree(jsonInputString);
		} catch (JsonProcessingException e) {
			this.authSubJsonInputError.setText("Input JSON is not valid");
			this.authSubJsonInputError.setVisible(true);
			return null;
		}
		
		return AuthorizationSubscription.of(
				jsonInput.findValue("subject"), 
				jsonInput.findValue("action"), 
				jsonInput.findValue("resource"), 
				jsonInput.findValue("environment")
		);		
    }
    
    
    private Consumer<AuthorizationDecision> consumeAuthDecision() {
    	return authDecision -> {
    		this.aggregatedResult.add(convertAuthDecisionToPrintableJsonNode(authDecision));
    		
    		try {
    			this.jsonOutput.setDocument(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.aggregatedResult));
    		} catch (JsonProcessingException e) {
    			System.out.println(e);
    			this.evaluationError.setVisible(true);
    			this.evaluationError.setText("Error printing Evaluation Result!");
    		}
    		return;
    	};
    }
    
    
    private JsonNode convertAuthDecisionToPrintableJsonNode(AuthorizationDecision authDecision) {
    	ObjectNode printableDecision = objectMapper.createObjectNode();
		printableDecision.put("decision", authDecision.getDecision().toString());
		
		if(authDecision.getObligations().isPresent()) {
			printableDecision.set("obligations", authDecision.getObligations().get());
		}
		
		if(authDecision.getAdvices().isPresent()) {
			printableDecision.set("advices", authDecision.getAdvices().get());
		}
		
		if(authDecision.getResource().isPresent()) {
			printableDecision.set("resource", authDecision.getResource().get());
		}
		
		return printableDecision;
    }
    
    private int countNumberOfExpectedDecisions() {
    	int biggestNumberOfValuesEmittedByOneMock = 1;
    	
    	Map<String, Integer> countValues = new HashMap<>();
    	
    	for(AttributeMockReturnValues mock : this.attrReturnValues) {
    		if(countValues.containsKey(mock.getFullname())) {
    			countValues.put(mock.getFullname(), countValues.get(mock.getFullname() + mock.getMockReturnValues().length));
    		} else {
    			countValues.put(mock.getFullname(), mock.getMockReturnValues().length);
    		}
    	}
    	
    	
    	for(Integer i : countValues.values()) {
    		if(i > biggestNumberOfValuesEmittedByOneMock) {
    			biggestNumberOfValuesEmittedByOneMock = i;
    		}
    	}
    	
    	return biggestNumberOfValuesEmittedByOneMock;
    	
    }
}
