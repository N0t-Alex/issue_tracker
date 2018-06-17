package com.cs673.teamA.Iteration2;

import javax.servlet.annotation.WebServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.CustomLayout;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;


@Theme("mytheme")
@SuppressWarnings("serial")
@SpringUI
public class MainUI extends UI {

    private int clickCounter = 0;
    private Label clickCounterLabel;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        Label sideMenuTitle = new Label("Projects");
        sideMenuTitle.addStyleName("centerlabel");

        //TODO - Later try using list of buttons.
        Button project1 = new Button("Project 1", e -> getNavigator().navigateTo("view1"));
        project1.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
        Button project2 = new Button("Project 2", e -> getNavigator().navigateTo("view2"));
        project2.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

        CssLayout sideMenu = new CssLayout(sideMenuTitle, project1, project2);
        sideMenu.addStyleName(ValoTheme.MENU_ROOT);

        CustomLayout mainPanelLayout = new CustomLayout("main_panel_layout");
        mainPanelLayout.setSizeFull();
        setContent(mainPanelLayout);
        /*
        HorizontalLayout rootLayout = new HorizontalLayout(sideMenu, mainPanelLayout);
        rootLayout.setSizeFull();
        setContent(mainPanelLayout);
        */
        /*
        CssLayout viewContainer = new CssLayout();

        HorizontalLayout mainLayout = new HorizontalLayout(sideMenu, viewContainer);
        mainLayout.setSizeFull();
        setContent(mainLayout);

        Navigator navigator = new Navigator(this, viewContainer);
        navigator.addView("", DefaultView.class);
        navigator.addView("view1", View1.class);
        navigator.addView("view2", View2.class);
        */
    }

}
