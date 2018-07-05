package com.cs673.teamA.Iteration2;

import javax.servlet.annotation.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Image;
import java.util.*;
import com.vaadin.ui.TextArea;
import com.vaadin.server.Sizeable.Unit;


@Theme("mytheme")
@SuppressWarnings("serial")
@SpringUI
public class MainUI extends UI {
	//Project filter, which is in the side menu.
    private  TextField projectFilter;
    private  Button projectFilterBtn;

    //Fixed size main operation panel, reloading it when needed.
    private Panel mainPanel;
    
    //Repositories and other DB objects
  	@Autowired
  	private IssueRepository iRepo;
  	
  	@Autowired
  	private CommentRepository cRepo;
  	
  	@Autowired
  	private ProjectRepository pRepo;
  	
  	@Autowired
  	private UserRepository uRepo;
  	
  	private List<Long> issues;
  	private List<Long> projects;
  	private Long selectedProject;
    //Search all the issue comments.
    private Long selectedIssue;
  	private List<Long> users;
  	private List<Comment> comments;
  	private Optional<User> loggedIn;
    
    //Define max display of dynamically added components.
    private final int MAX_PROJECTS_NUM = 5;
    private final int MAX_ISSUES_NUM = 25;
    private final int MAX_COMMENTS_NUM = 100;
    
    //This is the rootRootLayout for adding pop up window.
    private AbsoluteLayout rootRootLayout;
    private PopupView popupNewIssue;
    private PopupView popupNewComment;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
    public static class Servlet extends VaadinServlet {
    }

    private void loadIssueComments(Long issueId) {
    	IssueTicket issue = iRepo.findById(issueId).get();
        //TODO: Reset the comments each time, consider doing it locally.
        comments = new ArrayList<Comment>();
        comments.addAll(cRepo.findByIssueId(issue.getIssueId()));
        CustomLayout commentsBoardContent = new CustomLayout("discussion_board");
        Label issueName = new Label(issue.getName());
        issueName.addStyleName("myCommentTitle");
        Label issueInfo = new Label(issue.getDescription());
        issueInfo.addStyleName("myCommentStatus");

        Button backButton = new Button("Back");
        backButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                loadIssueTickets(issue.getProjectId()); //Go back and reload the whole issue ticket.
            }
        });
        
        VerticalLayout commentsRoot = new VerticalLayout();
        //Adding comments to the comments board.
        for (int i=0; i<MAX_COMMENTS_NUM; i++) {
        	if (i == comments.size()) {
        		break;
        	}
            // Get the profile icon from resource.
            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            FileResource resource = new FileResource(new File(basepath + "/WEB-INF/images/mySelfie.jpg"));
            Image mySelfie = new Image("", resource);
            mySelfie.setHeight(80, Unit.PIXELS);

            CustomLayout commentSection = new CustomLayout("comment");
            commentSection.addComponent(mySelfie, "profileIcon");
            Label comment = new Label(comments.get(i).getContent());
            comment.setWidth(700, Unit.PIXELS);
            commentSection.addComponent(comment, "comment");
            
            commentsRoot.addComponent(commentSection);
    	}
        //Add the add comment button.
        Button addComment = new Button("Add Comment");
        addComment.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                popupNewComment.setPopupVisible(true);
            }
        });
        //Add a refresh button for "close to" real time user experience.
        Button refreshComment = new Button("Refresh");
        refreshComment.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                //reload comment page.
                loadIssueComments(selectedIssue);
            }
        });
        commentsRoot.addComponent(addComment);
        commentsRoot.setComponentAlignment(addComment, Alignment.MIDDLE_CENTER);
        //Add the refresh button to the right.
        commentsRoot.addComponent(refreshComment);
        commentsRoot.setComponentAlignment(refreshComment, Alignment.MIDDLE_CENTER);
        commentsBoardContent.addComponent(commentsRoot, "commentsRoot");
        commentsBoardContent.addComponent(issueName, "title");
        commentsBoardContent.addComponent(issueInfo, "info");
        commentsBoardContent.addComponent(backButton, "backButton");
        this.mainPanel.setContent(commentsBoardContent);
    }
    
    /*
     * TODOs:
     * (1) Edit issue tickets button.
     * (2) Add issue tickets button.
     * (3) Control tickets number displayed.
     */
    private void loadIssueTickets(Long projectId) {
        //Issue Tickets filter
        TextField issueFilter;
        Button issueFilterBtn;

        CustomLayout mainPanelContent = new CustomLayout("panel_content");
        //Issue Tickets filter
        issueFilter = new TextField();
        issueFilter.setPlaceholder("Issue Title");
        issueFilterBtn = new Button("Search");
        mainPanelContent.setHeightUndefined();
        HorizontalLayout issueFilterLayout = new HorizontalLayout();
        issueFilterLayout.addComponent(issueFilterBtn);
        issueFilterLayout.setComponentAlignment(issueFilterBtn, Alignment.MIDDLE_LEFT);
        issueFilterLayout.addComponent(issueFilter);
        issueFilterLayout.setComponentAlignment(issueFilter, Alignment.MIDDLE_LEFT);
        mainPanelContent.addComponent(issueFilterLayout, "issueFilter");
        HorizontalLayout issueButtonsLayout = new HorizontalLayout();
        Button newIssueBtn = new Button("New issue");
        newIssueBtn.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                popupNewIssue.setPopupVisible(true);
            }
        });
        issueButtonsLayout.addComponent(newIssueBtn);
        issueButtonsLayout.setComponentAlignment(newIssueBtn, Alignment.MIDDLE_RIGHT);
        mainPanelContent.addComponent(issueButtonsLayout, "issueButtons");

        //Dynamically add the tickets to the panel.
        VerticalLayout issueTicketsBoard = new VerticalLayout();
        if (!issues.isEmpty()) {
        	issues.clear();
        }
    	iRepo.findByProjectId(projectId).forEach(issue -> issues.add(issue.getIssueId()));
        
        for (int i=0; i<MAX_ISSUES_NUM; i++) {
        	
            if (i == issues.size()) {
                break;
            }
            IssueTicket issue = iRepo.findById(issues.get(i)).get();
            // Get the opened icon from resource.
            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            FileResource resource = new FileResource(new File(basepath + "/WEB-INF/images/opened.png"));
            Image openedIcon = new Image("", resource);
            openedIcon.setHeight(25, Unit.PIXELS);

            // Get the comment icon from resource.
            basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            resource = new FileResource(new File(basepath + "/WEB-INF/images/comment.png"));
            Image commentIcon = new Image("", resource);
            commentIcon.setHeight(20, Unit.PIXELS);
            Label commentNum = new Label("9");

            String tempIssueName = issue.getName();
            // Create customized issue tickets.
            CustomLayout ticket = new CustomLayout("issue_ticket");
            Button issueTitle = new Button(tempIssueName);
            issueTitle.setIconAlternateText(tempIssueName);
            issueTitle.addStyleName("titleOnClick");
            issueTitle.addClickListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    //Use the issue ID to query the database.
                    selectedIssue = issue.getIssueId();
                    loadIssueComments(issue.getIssueId());
                }
            });
            //Layout for issue status, owner and assignee.
            HorizontalLayout issueInfo = new HorizontalLayout();
            Label issueStatus = new Label(issue.getDescription());
            Label issueOwner = new Label("Owner: " + uRepo.findById(issue.getOwnerId()).get().getUsername());
            Label issueAssignee = new Label("Assignee: " + uRepo.findById(issue.getAssigneeId()).get().getUsername());
            issueInfo.addComponent(issueStatus);
            issueInfo.addComponent(issueOwner);
            issueInfo.addComponent(issueAssignee);
            issueStatus.addStyleName("myIssueStatus");
            //Layout for edit issue button and comment icon.
            HorizontalLayout editIssueAndCommentIcon = new HorizontalLayout();
            Button editIssueButton = new Button("Edit");
            editIssueButton.addClickListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    //Pop up a window for editing issue information.
                    popupNewIssue.setPopupVisible(true);
                }
            });
            editIssueAndCommentIcon.addComponent(editIssueButton);
            editIssueAndCommentIcon.addComponent(commentIcon);
            //Add components to the customized ticket layout.
            ticket.addComponent(openedIcon, "resolved");
            ticket.addComponent(issueTitle, "issueTitle");
            ticket.addComponent(issueInfo, "issueStatus");
            ticket.addComponent(editIssueAndCommentIcon, "commentIcon");
            ticket.addComponent(commentNum, "commentNum");
            
            issueTicketsBoard.addComponent(ticket);
        }
        mainPanelContent.addComponent(issueTicketsBoard, "issueTicketsBoard");

        //Set the panel content to the fixed size panel.
        this.mainPanel.setContent(mainPanelContent);
    }
        
    @Override
    protected void init(VaadinRequest request) {
        //Dummy projects
    	Project projA = new Project();
    	projA.setProjectName("Project A");
    	pRepo.save(projA);
    	Project projB = new Project();
    	projB.setProjectName("Project B");
    	pRepo.save(projB);
    	Project projC = new Project();
    	projC.setProjectName("Project C");
    	pRepo.save(projC);
    	Project projX = new Project();
    	projX.setProjectName("Project X");
    	pRepo.save(projX);
    	Project projY = new Project();
    	projY.setProjectName("Project Y");
    	pRepo.save(projY);
    	Project projZ = new Project();
    	projZ.setProjectName("Project Z");
    	pRepo.save(projZ);
    	
    	projects = new ArrayList<Long>();
    	pRepo.findAll().forEach(project -> projects.add(project.getProjectId()));
    	
    	//Dummy users
    	User alex = new User();
    	alex.setUsername("Alex Andrade");
    	uRepo.save(alex);
    	User iYang = new User();
    	iYang.setUsername("I-Yang Chen");
    	uRepo.save(iYang);
    	
    	users = new ArrayList<Long>();
    	uRepo.findAll().forEach(user -> users.add(user.getUserId()));
    	
    	issues = new ArrayList<Long>();
        //Do the same lambda here, so issues will contain all the IDs.
        iRepo.findAll().forEach(issue -> issues.add(issue.getIssueId()));

    	comments = new ArrayList<Comment>();
    	
    	// User "logged in"
    	loggedIn = uRepo.findById(users.get(0));
    	
        /**
         * UI - Side Menu
         */

        // Project filter
        this.projectFilter = new TextField();
        this.projectFilter.setPlaceholder("Project Name");
        this.projectFilterBtn = new Button("Search");
        HorizontalLayout sideMenuSearchDiv = new HorizontalLayout(this.projectFilter, this.projectFilterBtn);

        Label sideMenuTitle = new Label("Projects");
        sideMenuTitle.addStyleName("side_menu_title");
        Label sideMenuPageNavi = new Label("Page Navigator");
        sideMenuPageNavi.addStyleName("pageNavigatorTemp");

        CustomLayout sideMenuLayout = new CustomLayout("side_menu");
        sideMenuLayout.addComponent(sideMenuTitle, "Projects");
        sideMenuLayout.addComponent(sideMenuSearchDiv, "searchProjects");
        sideMenuLayout.addComponent(sideMenuPageNavi, "PageNavigator");

        /*
         * TODOs:
         * (1) Loading project names from the database, need an algorithm to deal with more than
         * five projects.
         * (2) Add button listener to reload the main panel content, which is the issue tickets.
         */

        List<Button> projectButtons = new ArrayList<>();
        for (int i=0; i<projects.size(); i++) {
        	if (i == projects.size()) {
        		break;
        	}
        	Project project = pRepo.findById(projects.get(i)).get();
            Button temp = new Button(project.getProjectName());
            temp.addStyleName("sideMenuButton");
            temp.setWidth(300, Unit.PIXELS);
            temp.setHeight(80, Unit.PIXELS);
            temp.addClickListener(new Button.ClickListener() {
            	public void buttonClick(ClickEvent event) {
            		selectedProject = project.getProjectId();
            		loadIssueTickets(selectedProject);
            	}
            });
            projectButtons.add(temp);
        }
        for (int i=0; i<MAX_PROJECTS_NUM; i++) {
            String location = "Project_Btn";
            sideMenuLayout.addComponent(projectButtons.get(i), location + Integer.toString(i+1));
        }

        /**
         * UI - Main Page 
         */

        CustomLayout mainPanelLayout = new CustomLayout("main_panel_layout");

        /**
         * UI - Header 
         *
         * TODOs:
         * (1) When click on "Project Management Tool" button, navigate to that page.
         * (2) When click on "Communication Tool" button, navigate to that page.
         * (3) When click on "User Profile" icon, show user profile, status, settings, etc.
         */
        Button pmBtn = new Button("Project Management Tool");
        pmBtn.setHeight(65, Unit.PIXELS);
        pmBtn.addStyleName("headerButton");

        Button chatBtn = new Button("Communication Tool");
        chatBtn.setHeight(65, Unit.PIXELS);
        chatBtn.addStyleName("headerButton");
        
        Label userLabel = new Label("Hello, " + loggedIn.get().getUsername());

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
        mainPanelLayout.addComponent(headerLayout, "header");

        
        //Define the fixed size operation panel here. 
        this.mainPanel = new Panel();
        mainPanel.setWidth(1000, Unit.PIXELS);
        mainPanel.setHeight(500, Unit.PIXELS);
        mainPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);

        selectedProject = projects.get(0);
        this.loadIssueTickets(selectedProject);

        //Add the fixed size panel to the main panel layout.
        mainPanelLayout.addComponent(mainPanel, "list_view");

        /**
         * UI - Footer
         */
        Label footerLayout = new Label("CS673 - Team A, Issue Management Tool, UI Created by I-Yang Chen");
        footerLayout.addStyleName("footerLayout");
        mainPanelLayout.addComponent(footerLayout, "footerLayout");
        
        //Root, add everything together and display. 
        CustomLayout rootLayout = new CustomLayout("root_layout");
        rootLayout.addComponent(sideMenuLayout, "left");
        rootLayout.addComponent(mainPanelLayout, "right");

        // Content for the PopupView of creating new issue.
        VerticalLayout popupContent = new VerticalLayout();
        popupContent.setSizeFull();
        TextField issueTitleText = new TextField("Issue Title");
        issueTitleText.setWidth(300, Unit.PIXELS);
        TextArea issueContentText = new TextArea("Issue Content");
        issueContentText.setHeight(200, Unit.PIXELS);
        issueContentText.setWidth(300, Unit.PIXELS);
        TextField assigneeText = new TextField("Assignee");
        assigneeText.setWidth(300, Unit.PIXELS);
        TextField ownerText = new TextField("Owner");
        ownerText.setWidth(300, Unit.PIXELS);
        Button addIssueButton = new Button("Add");
        addIssueButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
            	//Check that the issue has a title first.
            	if (issueTitleText.isEmpty()) {
            		Notification.show("Issue must have a title.", Notification.Type.ERROR_MESSAGE);
            		return;
            	}
        		
            	//Search the database for users containing the entered values for owner and assignee.
            	List<User> ownerSearch;
            	List<User> assigneeSearch;
            	Long ownerId = null;
            	Long assigneeId = null;
            	if (!ownerText.isEmpty()) {
            		ownerSearch = uRepo.findByUsernameContaining(ownerText.getValue());
            		if (ownerSearch.size() == 0) {
            			Notification.show("No users found with a username containing " 
            				+ ownerText.getValue() + ". Please try a different username.", 
            				Notification.Type.ERROR_MESSAGE);
            			return;
            		}
            		else if (ownerSearch.size() > 1) {
            			Notification.show("More than one user was found with a username containing " 
                				+ ownerText.getValue() + ". Please try a different username.", 
                				Notification.Type.ERROR_MESSAGE);
                			return;
            		}
            		else {
            			ownerId = ownerSearch.get(0).getUserId();
            		}
            	}
            	else {
            		ownerId = loggedIn.get().getUserId();
            	}
            	if (!assigneeText.isEmpty()) {
            		assigneeSearch = uRepo.findByUsernameContaining(assigneeText.getValue());
            		if (assigneeSearch.size() == 0) {
            			Notification.show("No users found with a username containing " 
            				+ assigneeText.getValue() + ". Please try a different username.", 
            				Notification.Type.ERROR_MESSAGE);
            			return;
            		}
            		else if (assigneeSearch.size() > 1) {
            			Notification.show("More than one user was found with a username containing " 
                				+ assigneeText.getValue() + ". Please try a different username.", 
                				Notification.Type.ERROR_MESSAGE);
                			return;
            		}
            		else {
            			assigneeId = assigneeSearch.get(0).getUserId();
            		}
            	}
            	else {
            		assigneeId = loggedIn.get().getUserId();
            	}
                //Find the project issue counter.
                for (int i=0; i<projects.size(); i++) {

                }
            	//Add the ticket to the database.
            	IssueTicket newIssue = new IssueTicket();
            	newIssue.setName(issueTitleText.getValue());
            	newIssue.setDescription(issueContentText.getValue());
            	newIssue.setAssigneeId(assigneeId);
            	newIssue.setOwnerId(ownerId);
            	newIssue.setDateCreated(new Date());
            	newIssue.setResolved(false);
            	newIssue.setProjectId(selectedProject);
                iRepo.save(newIssue);
                Notification.show(issueTitleText.getValue(), "is created.",
                  Notification.Type.HUMANIZED_MESSAGE);
                popupNewIssue.setPopupVisible(false);

                //Reload the issue page.
                loadIssueTickets(selectedProject);
            }
        });
        popupContent.addComponent(issueTitleText);
        popupContent.addComponent(issueContentText);
        popupContent.addComponent(assigneeText);
        popupContent.addComponent(ownerText);
        popupContent.addComponent(addIssueButton);
        popupContent.setComponentAlignment(addIssueButton, Alignment.MIDDLE_CENTER);
        // The component itself
        popupNewIssue = new PopupView(null, popupContent);
        popupNewIssue.setHideOnMouseOut(false);

        // Content for the PopupView of creating new comment.
        VerticalLayout popupContent2 = new VerticalLayout();
        popupContent2.setSizeFull();
        TextArea commentContentText = new TextArea("Comment Content");
        commentContentText.setHeight(200, Unit.PIXELS);
        commentContentText.setWidth(300, Unit.PIXELS);
        Button addCommentButton = new Button("Add");
        addCommentButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                //Add the ticket to the database.
                Comment newComment = new Comment();
                newComment.setContent(commentContentText.getValue());
                newComment.setIssueId(selectedIssue);
                cRepo.save(newComment);
                popupNewComment.setPopupVisible(false);

                //Reload the comment board.
                loadIssueComments(selectedIssue);
            }
        });
        popupContent2.addComponent(commentContentText);
        popupContent2.addComponent(addCommentButton);
        popupContent2.setComponentAlignment(addCommentButton, Alignment.MIDDLE_CENTER);
        // The component itself
        popupNewComment = new PopupView(null, popupContent2);
        popupNewComment.setHideOnMouseOut(false);

        rootRootLayout = new AbsoluteLayout();
        rootRootLayout.addComponent(rootLayout);
        rootRootLayout.addComponent(popupNewIssue, "left: 50%; top: 50%;");
        rootRootLayout.addComponent(popupNewComment, "left: 50%; top: 50%;");
        setContent(rootRootLayout);  
    }
}
