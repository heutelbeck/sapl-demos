/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.playground.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import io.sapl.api.interpreter.Val;
import io.sapl.api.pdp.AuthorizationDecision;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.functions.FilterFunctionLibrary;
import io.sapl.functions.StandardFunctionLibrary;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.grammar.sapl.SAPL;
import io.sapl.interpreter.DefaultSAPLInterpreter;
import io.sapl.interpreter.DocumentEvaluationResult;
import io.sapl.interpreter.InitializationException;
import io.sapl.interpreter.SAPLInterpreter;
import io.sapl.interpreter.context.AuthorizationContext;
import io.sapl.interpreter.functions.AnnotationFunctionContext;
import io.sapl.interpreter.pip.AnnotationAttributeContext;
import io.sapl.interpreter.pip.AttributeContext;
import io.sapl.pip.TimePolicyInformationPoint;
import io.sapl.playground.examples.BasicExample;
import io.sapl.playground.examples.Example;
import io.sapl.playground.models.MockDefinitionParsingException;
import io.sapl.playground.models.MockingModel;
import io.sapl.test.mocking.attribute.MockingAttributeContext;
import io.sapl.test.mocking.function.MockingFunctionContext;
import io.sapl.test.steps.AttributeMockReturnValues;
import io.sapl.vaadin.*;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.StepVerifier.Step;
import reactor.util.context.Context;

import java.time.Clock;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
@PageTitle("SAPL Playground")
@Route(value = "", layout = MainLayout.class)
public class PlaygroundView extends VerticalLayout {
    private static final long serialVersionUID = 6521235098267757690L;

	// UI element references
	private SaplEditor saplEditor;

	private JsonEditor mockDefinitionEditor;

	private Paragraph mockDefinitionJsonInputError;

	private JsonEditor authzSubEditor;

	private Paragraph authzSubJsonInputError;

	private List<MockingModel> currentMockingModel;

	private AuthorizationSubscription currentAuthzSub;

	private SAPL currentPolicy;

	private JsonEditor jsonOutput;

	private Paragraph evaluationError;

	// Internal global variables
	private final SAPLInterpreter saplInterpreter;

	private List<AttributeMockReturnValues> attrReturnValues;

	private final ObjectMapper objectMapper;

	private final AnnotationAttributeContext defaultAttrContext;

	private final AnnotationFunctionContext defaultFunctionContext;

	private boolean ignoreNextPolicyEditorChangedEvent = false;

	private boolean ignoreNextAuthzSubJsonEditorChangedEvent = false;

	private boolean ignoreNextMockJsonEditorChangedEvent = false;

	public PlaygroundView(ExampleSelectedViewBus exampleSelectedViewBus) throws InitializationException {

		exampleSelectedViewBus.setContentView(this);

		this.saplInterpreter    = new DefaultSAPLInterpreter();
		this.objectMapper       = new ObjectMapper();
		this.defaultAttrContext = new AnnotationAttributeContext();
		this.defaultAttrContext.loadPolicyInformationPoint(new TimePolicyInformationPoint(Clock.systemUTC()));
		this.defaultFunctionContext = new AnnotationFunctionContext();
		this.defaultFunctionContext.loadLibrary(new FilterFunctionLibrary());
		this.defaultFunctionContext.loadLibrary(new StandardFunctionLibrary());
		this.defaultFunctionContext.loadLibrary(new TemporalFunctionLibrary());

		var horizontalSplitLayout = new SplitLayout(policyEditor(), createRightSide());
		horizontalSplitLayout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
		horizontalSplitLayout.setSizeFull();
		horizontalSplitLayout.setSplitterPosition(50);

		add(horizontalSplitLayout);
	}

	/**
	 * After all components are attached -> initialize & evaluate default example
	 */
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		setExample(new BasicExample(), false);
	}

	private Component policyEditor() {
		var saplConfig = new SaplEditorConfiguration();
		saplConfig.setHasLineNumbers(true);
		saplConfig.setTextUpdateDelay(500);
		saplConfig.setDarkTheme(true);
		this.saplEditor = new SaplEditor(saplConfig);
		this.saplEditor.addDocumentChangedListener(this::onSaplPolicyChanged);
		this.saplEditor.addValidationFinishedListener(this::onValidationFinished);
		return saplEditor;
	}

	private Component createRightSide() {
		var rightSideSplit = new SplitLayout(createRightUpperSide(), resultsDisplay());
		rightSideSplit.setOrientation(SplitLayout.Orientation.VERTICAL);
		rightSideSplit.setSizeFull();
		rightSideSplit.setSplitterPosition(50);
		return rightSideSplit;
	}

	private Component createRightUpperSide() {
		var tabSheet = new TabSheet();
		tabSheet.add("Authorization Subscription", createAuthorizationSubscriptionEditor());
		tabSheet.add("Mocks", mocksEditorAndError());
		tabSheet.add("Mock Help", mockingInformation());
		return tabSheet;
	}

	private Component mockingInformation() {
		return new Html(
				"""
						<div>
						  <p>
						    Expecting an array of JSON objects, each object consisting of the following properties:"
						  </p>
						  <ul>
						    <li>
						            (Required): Allowed values are "ATTRIBUTE" or "FUNCTION"
						          </li>
						          <li>
						            "importName" - (Required): The name the function or attribute referenced in your policy (for example "time.dayOfWeekFrom").
						          </li>
						          <li>
						            "always" - (Optional): A JSON value to be returned by this attribute or to be returned by a function every time the function is called.
						          </li>
						          <li>
						            "sequence" - (Optional): An array of JSON values to be returned by the attribute or function.
						          </li>
						           </ul>
						           <p>
						             Exactly one of "always" or "sequence" is required.
						           </p>
						</div>
						""");
	}

	private Component mocksEditorAndError() {
		var mockInput            = new VerticalLayout();
		mockInput.setClassName(LumoUtility.Padding.NONE);

		var mockJsonEditorConfig = new JsonEditorConfiguration();
		mockJsonEditorConfig.setTextUpdateDelay(500);
		mockJsonEditorConfig.setDarkTheme(true);
		this.mockDefinitionEditor = new JsonEditor(mockJsonEditorConfig);
		this.mockDefinitionEditor.addDocumentChangedListener(this::onMockingJsonEditorInputChanged);
		mockInput.add(this.mockDefinitionEditor);

		this.mockDefinitionJsonInputError = new Paragraph("Input JSON is not valid");
		this.mockDefinitionJsonInputError.setVisible(false);
		this.mockDefinitionJsonInputError.setClassName(LumoUtility.TextColor.ERROR);
		mockInput.add(this.mockDefinitionJsonInputError);
		return mockInput;
	}

	private Component createAuthorizationSubscriptionEditor() {
		var authzSubscriptionEditor = new VerticalLayout();
		authzSubscriptionEditor.setClassName(LumoUtility.Padding.NONE);

		var authzSubEditorConfig    = new JsonEditorConfiguration();
		authzSubEditorConfig.setTextUpdateDelay(500);
		authzSubEditorConfig.setDarkTheme(true);
		this.authzSubEditor = new JsonEditor(authzSubEditorConfig);
		this.authzSubEditor.addDocumentChangedListener(this::onAuthzSubJsonInputChanged);

		authzSubscriptionEditor.add(this.authzSubEditor);

		this.authzSubJsonInputError = new Paragraph("Input JSON is not valid");
		this.authzSubJsonInputError.setVisible(false);
		this.authzSubJsonInputError.setClassName(LumoUtility.TextColor.ERROR);
		authzSubscriptionEditor.add(this.authzSubJsonInputError);
		return authzSubscriptionEditor;
	}

	private Component resultsDisplay() {
		JsonEditorConfiguration jsonOutputEditorConfiguration = new JsonEditorConfiguration();
		jsonOutputEditorConfiguration.setDarkTheme(true);
		this.jsonOutput      = new JsonEditor(jsonOutputEditorConfiguration);
		this.evaluationError = new Paragraph();
		this.evaluationError.setVisible(false);
		this.evaluationError.setClassName(LumoUtility.TextColor.ERROR);
		VerticalLayout resultsDisplayLayout = new VerticalLayout(jsonOutput, evaluationError);
		resultsDisplayLayout.addClassNames(LumoUtility.Padding.Bottom.NONE);
		return resultsDisplayLayout;
	}

	private void onValidationFinished(ValidationFinishedEvent event) {
		log.debug("validation finished");
		Issue[] issues = event.getIssues();
		log.debug("issue count: " + issues.length);
		for (Issue issue : issues) {
			log.debug(issue.getDescription());
		}
	}

	public void setExample(Example example, boolean ignoreNextChangedEvents) {

		updateComponentsWithNewExample(example, ignoreNextChangedEvents);

		this.currentAuthzSub     = getAuthzSubForJsonString(example.getAuthzSub());
		this.currentPolicy       = getSAPLDocument(example.getPolicy());
		this.currentMockingModel = parseMockingModels(example.getMockDefinition());

		evaluatePolicy();
	}

	/**
	 * updating the editor components triggers the document changed event and
	 * therefore multiple evaluations of the policy to prevent these multiple
	 * concurrent evaluations, ignore the documentChanged events
	 */
	private void updateComponentsWithNewExample(Example example, boolean ignoreNextChangedEvents) {
		this.getUI().ifPresent(ui -> ui.access(() -> {

			// TODO: still needed ? Should there not be a flag in the value changed event to
			// indicate user or system input ?
//			if (ignoreNextChangedEvents) {
//
//				this.ignoreNextPolicyEditorChangedEvent = true;
//
//				Component selectedTab = tabs.getSelectedTab();
//				if (selectedTab.equals(tab2MockInput)) {
//					this.ignoreNextMockJsonEditorChangedEvent = true;
//				}
//				if (selectedTab.equals(tab1AuthzSubInput)) {
//					this.ignoreNextAuthzSubJsonEditorChangedEvent = true;
//				}
//
//			}

			this.saplEditor.setDocument(example.getPolicy());
			this.authzSubEditor.setDocument(example.getAuthzSub());
			this.mockDefinitionEditor.setDocument(example.getMockDefinition());
		}));
	}

	private void onMockingJsonEditorInputChanged(DocumentChangedEvent event) {
		log.debug("Mock Json Editor changed");

		if (this.ignoreNextMockJsonEditorChangedEvent) {
			log.debug("Ignore this Mock Json Editor Document Changed Event");
			this.ignoreNextMockJsonEditorChangedEvent = false;
			return;
		}

		this.mockDefinitionJsonInputError.setVisible(false);

		this.currentMockingModel = parseMockingModels(event.getNewValue());

		evaluatePolicy();
	}

	private void onAuthzSubJsonInputChanged(DocumentChangedEvent event) {
		log.debug("AuthzSub Editor changed");

		if (this.ignoreNextAuthzSubJsonEditorChangedEvent) {
			log.debug("Ignore this AuthzSub Editor Editor Document Changed Event");
			this.ignoreNextAuthzSubJsonEditorChangedEvent = false;
			return;
		}

		this.authzSubJsonInputError.setVisible(false);

		this.currentAuthzSub = getAuthzSubForJsonString(event.getNewValue());

		evaluatePolicy();
	}

	private void onSaplPolicyChanged(DocumentChangedEvent event) {
		log.debug("Policy Editor changed");

		if (this.ignoreNextPolicyEditorChangedEvent) {
			log.debug("Ignore this Policy Editor Document Changed Event");
			this.ignoreNextPolicyEditorChangedEvent = false;
			return;
		}

		this.evaluationError.setVisible(false);

		var saplString = event.getNewValue();
		if (saplString == null || saplString.isEmpty() || !this.saplInterpreter.analyze(saplString).isValid()) {
			updateErrorParagraph(this.evaluationError, "Policy isn't valid!", true);
			return;
		}

		this.currentPolicy = getSAPLDocument(saplString);

		evaluatePolicy();
	}

	private boolean isPolicyMatchingAuthzSub() {
		var attributeCtx  = new MockingAttributeContext(this.defaultAttrContext);
		var matchesResult = this.currentPolicy.matches()
				.contextWrite(ctx -> getEvalContextForMockJson(ctx, attributeCtx, this.currentMockingModel,
						this.currentAuthzSub))
				.block();

		if (matchesResult == null || !matchesResult.isBoolean()) {
			updateErrorParagraph(this.evaluationError, String.valueOf(matchesResult), true);
			return false;
		}

		return matchesResult.getBoolean();
	}

	private void evaluatePolicy() {
		log.debug("Evaluating Policy");

		checkEvaluationDataNotNull();

		this.evaluationError.setVisible(false);
		ArrayNode aggregatedResult = this.objectMapper.createArrayNode();

		if (!isPolicyMatchingAuthzSub()) {

			StepVerifier.create(Flux.just(AuthorizationDecision.NOT_APPLICABLE))
					.consumeNextWith(consumeAuthDecision(aggregatedResult)).thenCancel().verify(Duration.ofSeconds(10));

		} else {

			var attributeCtx = new MockingAttributeContext(this.defaultAttrContext);

			Step<AuthorizationDecision> steps = StepVerifier.create(
					this.currentPolicy.evaluate().map(DocumentEvaluationResult::getAuthorizationDecision).contextWrite(
							ctx -> getEvalContextForMockJson(ctx, attributeCtx, this.currentMockingModel,
									this.currentAuthzSub)));

			steps = emitTestPublishersInStepVerifier(attributeCtx, steps);

			steps = consumeDecisionsFromStepVerifier(aggregatedResult, steps);

			verifyStepVerifierAndCatchAssertions(steps);

		}

		printResultsToOutput(aggregatedResult);

	}

	private void printResultsToOutput(ArrayNode aggregatedResult) {
		try {
			this.jsonOutput
					.setDocument(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(aggregatedResult));
		} catch (JsonProcessingException e) {
			log.error("Error deserializing AuthorizationDecisions: " + e);
			updateErrorParagraph(this.evaluationError, "Error printing Evaluation Result!", false);
		}
	}

	private void verifyStepVerifierAndCatchAssertions(Step<AuthorizationDecision> steps) {
		try {
			steps.thenCancel().verify(Duration.ofSeconds(10));
			log.debug("Evaluation finished");
		} catch (AssertionError err) {
			log.debug("Evaluation error", err);
			// do nothing because number of generated AuthorizationDecision's were not
			// equals number of maximal generated AuthzDec
		}
	}

	private Step<AuthorizationDecision> consumeDecisionsFromStepVerifier(
			ArrayNode aggregatedResult,
			Step<AuthorizationDecision> steps) {
		for (int i = 0; i < countNumberOfMaximalExpectedDecisions(); i++) {
			steps = steps.consumeNextWith(consumeAuthDecision(aggregatedResult));
		}
		return steps;
	}

	private Step<AuthorizationDecision> emitTestPublishersInStepVerifier(
			AttributeContext attributeCtx,
			Step<AuthorizationDecision> steps) {
		for (AttributeMockReturnValues mock : this.attrReturnValues) {
			String fullName = mock.getFullName();
			for (Val val : mock.getMockReturnValues()) {
				steps = steps.then(
						() -> ((MockingAttributeContext) attributeCtx).mockEmit(fullName, val));
			}
		}
		return steps;
	}

	private void checkEvaluationDataNotNull() {
		if (this.currentAuthzSub == null || this.currentMockingModel == null || this.currentPolicy == null) {
			throw new RuntimeException("Invalid internal state: Some evaluation data is null");
		}
	}

	private SAPL getSAPLDocument(String saplString) {
		if (saplString == null || saplString.isEmpty() || !this.saplInterpreter.analyze(saplString).isValid()) {
			updateErrorParagraph(this.evaluationError, "Policy isn't valid!", true);
			return null;
		} else {
			return this.saplInterpreter.parse(saplString);
		}
	}

	private List<MockingModel> parseMockingModels(String json) {
		JsonNode mockInput;
		try {
			mockInput = this.objectMapper.readTree(json);
		} catch (JsonProcessingException e) {
			updateErrorParagraph(this.mockDefinitionJsonInputError, "Cannot parse JSON!", true);
			return Collections.emptyList();
		}
		List<MockingModel> mocks = null;
		try {
			mocks = MockingModel.parseMockingJsonInputToModel(mockInput);
		} catch (MockDefinitionParsingException e) {
			updateErrorParagraph(this.mockDefinitionJsonInputError, e.getMessage(), true);
		}

		return mocks;
	}

	private Context getEvalContextForMockJson(
			Context ctx,
			MockingAttributeContext attributeCtx,
			List<MockingModel> mocks,
			AuthorizationSubscription authzSubscription) {
		var functionCtx = new MockingFunctionContext(this.defaultFunctionContext);
		var variables   = new HashMap<String, JsonNode>(1);
		this.attrReturnValues = new LinkedList<>();

		for (var mock : mocks) {
			switch (mock.getType()) {
			case ATTRIBUTE:
				if (mock.getAlways() != null) {
					attributeCtx.markAttributeMock(mock.getImportName());
					this.attrReturnValues
							.add(AttributeMockReturnValues.of(mock.getImportName(), List.of(mock.getAlways())));
				} else {
					attributeCtx.markAttributeMock(mock.getImportName());
					this.attrReturnValues.add(
							AttributeMockReturnValues.of(mock.getImportName(),
									new LinkedList<>(mock.getSequence())));
				}
				break;
			case FUNCTION:
				if (mock.getAlways() != null) {
					functionCtx.loadFunctionMockAlwaysSameValue(mock.getImportName(), mock.getAlways());
				} else {
					functionCtx.loadFunctionMockReturnsSequence(mock.getImportName(),
							mock.getSequence().toArray(new Val[0]));
				}
				break;
			default:
				break;
			}
		}
		ctx = AuthorizationContext.setAttributeContext(ctx, attributeCtx);
		ctx = AuthorizationContext.setFunctionContext(ctx, functionCtx);
		ctx = AuthorizationContext.setVariables(ctx, variables);
		ctx = AuthorizationContext.setSubscriptionVariables(ctx, authzSubscription);
		return ctx;
	}

	private AuthorizationSubscription getAuthzSubForJsonString(String jsonInputString) {
		JsonNode jsonInput;
		if (jsonInputString == null) {
			return null;
		}
		try {
			jsonInput = objectMapper.readTree(jsonInputString);
		} catch (JsonProcessingException e) {
			updateErrorParagraph(this.authzSubJsonInputError, "Input JSON is not valid", true);
			return null;
		}

		return AuthorizationSubscription.of(jsonInput.findValue("subject"), jsonInput.findValue("action"),
				jsonInput.findValue("resource"), jsonInput.findValue("environment"));
	}

	private Consumer<AuthorizationDecision> consumeAuthDecision(ArrayNode aggregatedResult) {
		return authDecision -> aggregatedResult.add(convertAuthDecisionToPrintableJsonNode(authDecision));
	}

	private JsonNode convertAuthDecisionToPrintableJsonNode(AuthorizationDecision authDecision) {
		ObjectNode printableDecision = objectMapper.createObjectNode();
		printableDecision.put("decision", authDecision.getDecision().toString());

		authDecision.getObligations().ifPresent(obligations -> printableDecision.set("obligations", obligations));
		authDecision.getAdvice().ifPresent(advice -> printableDecision.set("advice", advice));
		authDecision.getResource().ifPresent(resource -> printableDecision.set("resource", resource));

		return printableDecision;
	}

	private int countNumberOfMaximalExpectedDecisions() {
		int biggestNumberOfValuesEmittedByOneMock = 1;

		Map<String, Integer> countValues = new HashMap<>();

		for (AttributeMockReturnValues mock : this.attrReturnValues) {
			if (countValues.containsKey(mock.getFullName())) {
				countValues.put(mock.getFullName(),
						countValues.get(mock.getFullName() + mock.getMockReturnValues().size()));
			} else {
				countValues.put(mock.getFullName(), mock.getMockReturnValues().size());
			}
		}

		for (Integer i : countValues.values()) {
			if (i > biggestNumberOfValuesEmittedByOneMock) {
				biggestNumberOfValuesEmittedByOneMock = i;
			}
		}

		return biggestNumberOfValuesEmittedByOneMock;

	}

	private void updateErrorParagraph(Paragraph paragraph, String text, boolean clearOutput) {

		getUI().ifPresent(ui -> ui.access(() -> {
			log.trace("updateErrorParagraph()");
			paragraph.setVisible(true);
			paragraph.setText(text);

			if (clearOutput) {
				this.jsonOutput.setDocument("");
			}

		}));
	}

}
