package com.cs673.teamA.Iteration2;

import com.vaadin.navigator.View;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;
import com.vaadin.ui.CustomLayout;
import com.vaadin.annotations.Theme;
import com.vaadin.ui.UI;

public class View2 extends Composite implements View {
	public View2() {
		setCompositionRoot(new Label("This is view 2"));
	}
}