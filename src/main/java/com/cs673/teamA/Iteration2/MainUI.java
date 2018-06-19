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
import com.vaadin.ui.Component;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.server.Sizeable.Unit;
import java.io.File;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Image;

import java.util.*;


@Theme("mytheme")
@SuppressWarnings("serial")
@SpringUI
public class MainUI extends UI {
    // Project filter
    private  TextField projectFilter;
    private  Button projectFilterBtn;

    //Issue Tickets filter
    private  TextField issueFilter;
    private  Button issueFilterBtn;

    private int clickCounter = 0;
    private Label clickCounterLabel;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        // Project filter
        this.projectFilter = new TextField();
        this.projectFilter.setPlaceholder("Project Name");
        this.projectFilterBtn = new Button("Search");

        HorizontalLayout sideMenuSearchDiv = new HorizontalLayout(this.projectFilter, this.projectFilterBtn);

        //Issue Tickets filter
        this.issueFilter = new TextField();
        this.issueFilter.setPlaceholder("Issue Title");
        this.issueFilterBtn = new Button("Search");

        Label sideMenuTitle = new Label("Projects");
        sideMenuTitle.addStyleName("side_menu_title");
        Label sideMenuPageNavi = new Label("Page Navigator");
        sideMenuPageNavi.addStyleName("pageNavigatorTemp");

        //Dummy project names.
        String []projectNames = {"Jalapeno", "Marlboro",
            "Instax mini", "Mindhunter", "Natalie's"};

        CustomLayout sideMenuLayout = new CustomLayout("side_menu");

        //Use ArrayList to collect the buttons.
        List<Button> projectButtons = new ArrayList<>();

        //Add the project names to the list.
        for (int i=0; i<projectNames.length; i++) {
            Button temp = new Button(projectNames[i]);
            temp.addStyleName("sideMenuButton");
            temp.setWidth(300, Unit.PIXELS);
            temp.setHeight(80, Unit.PIXELS);
            //temp.setSizeFull();
            projectButtons.add(temp);
        }

        sideMenuLayout.addComponent(sideMenuTitle, "Projects");
        sideMenuLayout.addComponent(sideMenuSearchDiv, "searchProjects");
        sideMenuLayout.addComponent(sideMenuPageNavi, "PageNavigator");
        //Five buttons at most per page.
        for (int i=0; i<5; i++) {
            String location = "Project_Btn";
            sideMenuLayout.addComponent(projectButtons.get(i), location + Integer.toString(i+1));
        }

        //TODO - The size of mainPanelLayout should also listens to size changes.
        CustomLayout mainPanelLayout = new CustomLayout("main_panel_layout");

        //Header
        Button pmBtn = new Button("Project Management Tool");
        pmBtn.setHeight(65, Unit.PIXELS);
        pmBtn.addStyleName("headerButton");
        Button chatBtn = new Button("Communication Tool");
        chatBtn.setHeight(65, Unit.PIXELS);
        chatBtn.addStyleName("headerButton");
        Label userLabel = new Label("Hello, I-Yang!");
        // Get the user profile icon from resource.
        String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        FileResource resource = new FileResource(new File(basepath + "/WEB-INF/images/user.png"));
        Image profileIcon = new Image("", resource);
        profileIcon.setHeight(100, Unit.PIXELS);

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.addComponent(pmBtn);
        headerLayout.setComponentAlignment(pmBtn, Alignment.MIDDLE_RIGHT);
        headerLayout.addComponent(chatBtn);
        headerLayout.setComponentAlignment(chatBtn, Alignment.MIDDLE_RIGHT);
        headerLayout.addComponent(userLabel);
        headerLayout.setComponentAlignment(userLabel, Alignment.MIDDLE_RIGHT);
        headerLayout.addComponent(profileIcon);
        headerLayout.setComponentAlignment(profileIcon, Alignment.MIDDLE_RIGHT);

        Panel mainPanel = new Panel();
        mainPanel.setWidth(1000, Unit.PIXELS);
        mainPanel.setHeight(500, Unit.PIXELS);
        mainPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        CustomLayout mainPanelContent = new CustomLayout("panel_content");
        mainPanelContent.setHeightUndefined();
        HorizontalLayout issueFilterLayout = new HorizontalLayout();
        issueFilterLayout.addComponent(this.issueFilterBtn);
        issueFilterLayout.setComponentAlignment(this.issueFilterBtn, Alignment.MIDDLE_LEFT);
        issueFilterLayout.addComponent(this.issueFilter);
        issueFilterLayout.setComponentAlignment(this.issueFilter, Alignment.MIDDLE_LEFT);
        mainPanelContent.addComponent(issueFilterLayout, "issueFilter");
        HorizontalLayout issueButtonsLayout = new HorizontalLayout();
        Button newIssueBtn = new Button("New issue");
        issueButtonsLayout.addComponent(newIssueBtn);
        issueButtonsLayout.setComponentAlignment(newIssueBtn, Alignment.MIDDLE_RIGHT);
        mainPanelContent.addComponent(issueButtonsLayout, "issueButtons");
        mainPanel.setContent(mainPanelContent);
        //Adding tickets to the panel.
        for (int i=1; i<16; i++) {
            // Get the opened icon from resource.
            basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            resource = new FileResource(new File(basepath + "/WEB-INF/images/opened.png"));
            Image openedIcon = new Image("", resource);
            openedIcon.setHeight(25, Unit.PIXELS);

            // Get the comment icon from resource.
            basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            resource = new FileResource(new File(basepath + "/WEB-INF/images/comment.png"));
            Image commentIcon = new Image("", resource);
            commentIcon.setHeight(20, Unit.PIXELS);
            Label commentNum = new Label("9");

            CustomLayout ticket = new CustomLayout("issue_ticket");
            String ticketLoc = "issueTickets-" + Integer.toString(i);

            Label issueTitle = new Label("issue title must be a long sentence");
            issueTitle.addStyleName("myIssueTitle");
            Label issueStatus = new Label("Something describe the status of thie issue");
            issueStatus.addStyleName("myIssueStatus");

            ticket.addComponent(openedIcon, "resolved");
            ticket.addComponent(issueTitle, "issueTitle");
            ticket.addComponent(issueStatus, "issueStatus");
            ticket.addComponent(commentIcon, "commentIcon");
            ticket.addComponent(commentNum, "commentNum");
            
            mainPanelContent.addComponent(ticket, ticketLoc);
        }

        Label footerLayout = new Label("CS673 - Team A, Issue Management Tool UI, Created by I-Yang Chen");
        footerLayout.addStyleName("footerLayout");

        mainPanelLayout.addComponent(headerLayout, "header");
        mainPanelLayout.addComponent(mainPanel, "list_view");
        mainPanelLayout.addComponent(footerLayout, "footerLayout");
        
        CustomLayout rootLayout = new CustomLayout("root_layout");
        rootLayout.addComponent(sideMenuLayout, "left");
        rootLayout.addComponent(mainPanelLayout, "right");
        setContent(rootLayout);        
    }
}
