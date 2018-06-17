package com.cs673.teamA.Iteration2;

import com.vaadin.navigator.View;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Panel;

public class View1 extends Composite implements View {
	public View1() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeightUndefined();

		//Add 50 lines of label, test the view.
		for (int i=0; i<50; i++) {
			mainLayout.addComponent(new Label("This is View 1-" + Integer.toString(i)));
		}

		Panel mainPanel = new Panel();
		mainPanel.setHeight("300px");
		mainPanel.setScrollTop(100);
		mainPanel.setContent(mainLayout);
		/*
		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();
		rootLayout.addComponent(mainPanel);
		*/

		setCompositionRoot(mainPanel);
	}
}