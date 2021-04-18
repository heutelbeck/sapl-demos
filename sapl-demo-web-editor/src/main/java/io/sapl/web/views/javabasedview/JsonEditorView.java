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

	private JsonEditor jsonEditor;
	
	public JsonEditorView() {
		setId("json-editor-view");
		
		jsonEditor = new JsonEditor(new JsonEditorConfiguration());
		jsonEditor.addDocumentChangedListener(this);
		add(jsonEditor);
		
		Button getJsonDocumentButton = new Button();
		getJsonDocumentButton.setText("Get Document (JSON)");
		getJsonDocumentButton.addClickListener(e -> {
			String document = jsonEditor.getDocument();
			System.out.println("Get Document (JSON): " + document);
		});
		add(getJsonDocumentButton);

		Button setJsonDocumentButton = new Button();
		setJsonDocumentButton.setText("Set Document (JSON)");
		setJsonDocumentButton.addClickListener(e -> {
			String document = getDefaultJsonString();
			System.out.println("Set Document (JSON): " + document);
			jsonEditor.setDocument(document);
		});
		add(setJsonDocumentButton);
		
		jsonEditor.setDocument(getDefaultJsonString());
	}
	
	private String getDefaultJsonString() {
		return "[\r\n"
				+ " {\r\n"
				+ "  _id: \"post 1\",\r\n"
				+ "  \"author\": \"Bob\",\r\n"
				+ "  \"content\": \"...\",\r\n"
				+ "  \"page_views\": 5\r\n"
				+ " },\r\n"
				+ " {\r\n"
				+ "  \"_id\": \"post 2\",\r\n"
				+ "  \"author\": \"Bob\",\r\n"
				+ "  \"content\": \"...\",\r\n"
				+ "  \"page_views\": 9\r\n"
				+ " },\r\n"
				+ " {\r\n"
				+ "  \"_id\": \"post 3\",\r\n"
				+ "  \"author\": \"Bob\",\r\n"
				+ "  \"content\": \"...\",\r\n"
				+ "  \"page_views\": 8\r\n"
				+ " }\r\n"
				+ "]\r\n"
				+ "";
	}

	@Override
	public void onDocumentChanged(DocumentChangedEvent event) {
		System.out.println("JSON value changed: " + event.getNewValue());
	}
}
