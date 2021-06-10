package io.sapl.playground.views;

import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.annotation.UIScope;

import io.sapl.playground.views.content.ContentView;

@Component
@UIScope
public class ExampleSelectedViewBus {
	private ContentView contentView;

    public ExampleSelectedViewBus() {
    }

    public ContentView getContentView() {
        return this.contentView;
    }
    public void setContentView(ContentView contentView) {
        this.contentView = contentView;
    }
}
