package com.cs673.teamA.Iteration2;

import com.vaadin.navigator.View;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;

public class DefaultView extends Composite implements View {
	public DefaultView() {
		setCompositionRoot(new Label("This is the default view"));
	}
}