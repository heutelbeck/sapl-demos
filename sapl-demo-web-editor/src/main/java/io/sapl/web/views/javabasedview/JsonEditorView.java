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
package io.sapl.web.views.javabasedview;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import io.sapl.vaadin.DocumentChangedEvent;
import io.sapl.vaadin.DocumentChangedListener;
import io.sapl.vaadin.JsonEditor;
import io.sapl.vaadin.JsonEditorConfiguration;
import io.sapl.web.MainView;

@Route(value = "jsoneditor", layout = MainView.class)
@PageTitle("JSON Editor Demo")
@CssImport("./styles/views/javabasedview/javabased-view-view.css")
public class JsonEditorView extends Div implements DocumentChangedListener {

	private final JsonEditor jsonEditor;

	private final Button addDocumentChangedListenerButton;

	private Button removeDocumentChangedListenerButton;

	public JsonEditorView() {
		final String defaultJsonString = "[\r\n" + " {\r\n" + "  _id: \"post 1\",\r\n" + "  \"author\": \"Bob\",\r\n"
				+ "  \"content\": \"...\",\r\n" + "  \"page_views\": 5\r\n" + " },\r\n" + " {\r\n"
				+ "  \"_id\": \"post 2\",\r\n" + "  \"author\": \"Bob\",\r\n" + "  \"content\": \"...\",\r\n"
				+ "  \"page_views\": 9\r\n" + " },\r\n" + " {\r\n" + "  \"_id\": \"post 3\",\r\n"
				+ "  \"author\": \"Bob\",\r\n" + "  \"content\": \"...\",\r\n" + "  \"page_views\": 8\r\n" + " }\r\n"
				+ "]\r\n" + "";

		setId("json-editor-view");

		jsonEditor = new JsonEditor(new JsonEditorConfiguration());
		jsonEditor.addDocumentChangedListener(this);
		add(jsonEditor);

		addDocumentChangedListenerButton = new Button();
		addDocumentChangedListenerButton.setText("Add Change Listener");
		addDocumentChangedListenerButton.addClickListener(e -> {
			jsonEditor.addDocumentChangedListener(this);
			addDocumentChangedListenerButton.setEnabled(false);
			removeDocumentChangedListenerButton.setEnabled(true);
		});
		addDocumentChangedListenerButton.setEnabled(false);
		add(addDocumentChangedListenerButton);

		removeDocumentChangedListenerButton = new Button();
		removeDocumentChangedListenerButton.setText("Remove Change Listener");
		removeDocumentChangedListenerButton.addClickListener(e -> {
			jsonEditor.removeDocumentChangedListener(this);
			addDocumentChangedListenerButton.setEnabled(true);
			removeDocumentChangedListenerButton.setEnabled(false);
		});
		add(removeDocumentChangedListenerButton);

		Button showJsonDocumentButton = new Button();
		showJsonDocumentButton.setText("Show Document in Console");
		showJsonDocumentButton.addClickListener(e -> {
			String document = jsonEditor.getDocument();
			System.out.println("Current JSON value: " + document);
		});
		add(showJsonDocumentButton);

		Button setJsonDocumentButton = new Button();
		setJsonDocumentButton.setText("Set Document to Default");
		setJsonDocumentButton.addClickListener(e -> {
			jsonEditor.setDocument(defaultJsonString);
		});
		add(setJsonDocumentButton);

		Button toggleReadOnlyButton = new Button("Toggle ReadOnly");
		toggleReadOnlyButton.addClickListener(e -> jsonEditor.setReadOnly(!jsonEditor.isReadOnly()));
		add(toggleReadOnlyButton);

		jsonEditor.setDocument(defaultJsonString);
	}

	@Override
	public void onDocumentChanged(DocumentChangedEvent event) {
		System.out.println("JSON value changed: " + event.getNewValue());
	}

}
