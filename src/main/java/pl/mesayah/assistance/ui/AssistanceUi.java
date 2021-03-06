package pl.mesayah.assistance.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.mesayah.assistance.AssistanceApplication;
import pl.mesayah.assistance.Entity;
import pl.mesayah.assistance.security.role.Role;
import pl.mesayah.assistance.utils.Repositories;
import pl.mesayah.assistance.issue.Issue;
import pl.mesayah.assistance.milestone.Milestone;
import pl.mesayah.assistance.project.Project;
import pl.mesayah.assistance.security.AssistanceUserDetails;
import pl.mesayah.assistance.security.SecurityUtils;
import pl.mesayah.assistance.security.ui.AuthenticationView;
import pl.mesayah.assistance.task.Task;
import pl.mesayah.assistance.team.Team;
import pl.mesayah.assistance.utils.DetailsViews;
import pl.mesayah.assistance.utils.ListViews;
import pl.mesayah.assistance.user.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Main user interface class of the application.
 */
@Theme("mytheme")
@SpringUI
@SpringViewDisplay
public class AssistanceUi extends UI implements ViewDisplay {

    /**
     * A manager for authentication processes.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * A navigator which manages views and navigates between them.
     */
    @Autowired
    private SpringNavigator navigator;

    /**
     * A top bar with user information and navigation links.
     */
    private HorizontalLayout topBarLayout;

    /**
     * A container for views presenting different parts of the application.
     */
    private VerticalLayout viewDisplay;

    /**
     * A container for the whole user interface.
     */
    private VerticalLayout rootLayout;


    @Override
    protected void init(VaadinRequest vaadinRequest) {

        navigator.setErrorView(new ErrorView());

        initializeViewDisplay();

        // Show login form or application main user interface
        getPage().setTitle(AssistanceApplication.NAME);
        if (!SecurityUtils.isLoggedIn()) {
            showLoginForm();
        } else {

            showApplicationUi();
        }
    }


    /**
     * Initializes a layout for showing different application views.
     */
    private void initializeViewDisplay() {

        viewDisplay = new VerticalLayout();
        viewDisplay.setId("view-display");
        viewDisplay.setSizeFull();
        viewDisplay.setMargin(false);
    }


    /**
     * Sets content of this UI to login form.
     */
    private void showLoginForm() {

        setContent(new AuthenticationView(this::login));
    }


    /**
     * Sets content of this UI to a main application user interface.
     */
    private void showApplicationUi() {

        initializeTopBarLayout();
        initializeRootLayout();

        setContent(rootLayout);
    }


    /**
     * Initializes top bar with user information and navigation links.
     */
    private void initializeTopBarLayout() {

        topBarLayout = new HorizontalLayout();
        topBarLayout.setId("top-bar");
        topBarLayout.setWidth("100%");
        topBarLayout.setHeight("-1px");
        Layout navigationLayout = new NavigationLayout();
        Layout userInfoLayout = new UserInfoLayout();
        topBarLayout.addComponents(navigationLayout, userInfoLayout);
        topBarLayout.setExpandRatio(navigationLayout, 1.0f);
    }


    /**
     * Initializes a container for the whole user interface.
     */
    private void initializeRootLayout() {

        rootLayout = new VerticalLayout();
        rootLayout.setId("root");
        rootLayout.setMargin(false);
        rootLayout.addComponents(topBarLayout, viewDisplay);
        rootLayout.setComponentAlignment(viewDisplay, Alignment.TOP_CENTER);
        rootLayout.setSizeFull();
        rootLayout.setExpandRatio(viewDisplay, 1.0f);
        navigator.navigateTo(ListViews.getListViewNameFor(Task.class));
    }


    public <T extends Entity> void showDeleteWindow(T itemToDelete) {

        Set<T> set = new HashSet<>();
        set.add(itemToDelete);
        showDeleteWindow(set);
    }


    public <T extends Entity> void showDeleteWindow(Collection<T> itemsToDelete) {

        if (itemsToDelete.size() > 0) {
            T first = itemsToDelete.iterator().next();
            CrudRepository repository = Repositories.getRepositoryFor(first.getClass());

            String caption, message;
            if (itemsToDelete.size() > 1) {
                caption = "Delete " + itemsToDelete.size() + " " + first.getClass().getSimpleName() + "s";
                message = "Are you sure you want to delete " + itemsToDelete.size() + " " + first.getClass().getSimpleName() + "s?";
            } else {
                caption = "Delete a " + first.getClass().getSimpleName();
                message = "Are you sure you want to delete this " + first.getClass().getSimpleName() + "?";
            }

            YesNoDialog confirmDialog = new YesNoDialog(caption,
                    message, clickEvent -> {

                for (T item : itemsToDelete) {
                    repository.delete(item);
                }

                String listViewState = ListViews.getListViewNameFor(first.getClass());
                if (listViewState != null) {
                    navigator.navigateTo(listViewState);
                } else {
                    navigator.navigateTo("");
                }
            });
            getUI().addWindow(confirmDialog);
        }
    }


    private boolean login(String username, String password) {

        try {
            Authentication token = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            // Reinitialize the session to protect against session fixation attacks. This does not work
            // with websocket communication.
            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
            SecurityContextHolder.getContext().setAuthentication(token);
            // Now when the session is reinitialized, we can enable websocket communication. Or we could have just
            // used WEBSOCKET_XHR and skipped this step completely.
            getPushConfiguration().setTransport(Transport.WEBSOCKET);
            getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
            // Show the main UI
            showApplicationUi();
            return true;
        } catch (AuthenticationException ex) {
            return false;
        }
    }


    private void handleError(com.vaadin.server.ErrorEvent event) {

        Throwable t = DefaultErrorHandler.findRelevantThrowable(event.getThrowable());
        if (t instanceof AccessDeniedException) {
            Notification.show("You do not have permission to perform this operation",
                    Notification.Type.WARNING_MESSAGE);
        } else {
            DefaultErrorHandler.doDefault(event);
        }
    }


    @Override
    public void showView(View view) {

        viewDisplay.removeAllComponents();
        viewDisplay.addComponent((Component) view);
    }


    /**
     * A layout containing current user information.
     */

    class UserInfoLayout extends HorizontalLayout {


        @Autowired
        private AssistanceUserDetails userDetails;

        public UserInfoLayout() {

            final int marigin = 40;

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userDetails = (AssistanceUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            setSizeUndefined();
            this.setMargin(true);
            this.setId("userInfoLayout");
            // Notification window
            MenuBar userMenu = new MenuBar();
            final Window window = new Window("Notifications");
            window.setWidth(300.0f, Unit.PIXELS);
            VerticalLayout newWindowContent = new VerticalLayout();
            newWindowContent.setMargin(true);
            window.setContent(newWindowContent);
            window.setWidth("30%");
            window.setResizable(false);
            window.setDraggable(false);

            HorizontalLayout hor = new HorizontalLayout();
            //Simple notify view
            Label date = new Label("08.03 11:11");
            Label title = new Label("Test");
            Label desc = new Label("This is a test notification");
            desc.setWidth("70%");
            hor.addComponents(date, title, desc);
            newWindowContent.addComponent(hor);
            //end of simple notify


            userMenu.addItem("", VaadinIcons.FLAG, c -> {
                addWindow(window);
                window.setPosition((int) (getPage().getBrowserWindowWidth() * 0.7) - marigin, marigin);

            });
            userMenu.addItem(username, VaadinIcons.USER, c -> {
                navigator.navigateTo(DetailsViews.getDetailsViewNameFor(User.class) + "/" + userDetails.getID().toString());
            });
            userMenu.addItem("Sign Out", VaadinIcons.SIGN_OUT, c -> logout());

            this.addComponents(userMenu);
        }


        private void logout() {

            getPage().reload();
            getSession().close();
        }
    }

    /**
     * A layout containing navigation links at the top bar of the user interface.
     */
    class NavigationLayout extends HorizontalLayout {

        /**
         * Constructs a layout with navigation links and sets their click listeners.
         */
        public NavigationLayout() {

            this.setWidth("100%");
            this.setHeight("100%");

            setMargin(true);

            MenuBar menuBar = new MenuBar();

            menuBar.addItem("Tasks", c -> navigator.navigateTo(ListViews.getListViewNameFor(Task.class)));
            menuBar.addItem("Projects", c -> navigator.navigateTo(ListViews.getListViewNameFor(Project.class)));
            menuBar.addItem("Teams", c -> navigator.navigateTo(ListViews.getListViewNameFor(Team.class)));
            menuBar.addItem("Issues", c -> navigator.navigateTo(ListViews.getListViewNameFor(Issue.class)));
            menuBar.addItem("Milestones", c -> navigator.navigateTo(ListViews.getListViewNameFor(Milestone.class)));
            if(SecurityUtils.hasRole(Role.PROJECT_MANAGER) ||SecurityUtils.hasRole(Role.DEVELOPER) ||SecurityUtils.hasRole(Role.SUPER_ADMIN)) {
                menuBar.addItem("Users", c -> navigator.navigateTo(ListViews.getListViewNameFor(User.class)));
            }
            addComponent(menuBar);
        }
    }
}

