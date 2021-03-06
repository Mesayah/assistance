package pl.mesayah.assistance.user;

import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import pl.mesayah.assistance.security.SecurityUtils;
import pl.mesayah.assistance.security.role.Role;
import pl.mesayah.assistance.ui.list.AbstractListView;

import java.util.Collection;
import java.util.List;

@SpringView(name = UserListView.VIEW_NAME)
public class UserListView extends AbstractListView<User> {

    public static final String VIEW_NAME = "users";

    @Autowired
    private UserService userService;


    @Override
    protected User createEmptyEntity() {

        return new User();
    }


    @Override
    protected List<Button> initializeAdditionalButtons() {

        return null;
    }


    @Override
    public Grid<User> initializeListing() {

        Grid<User> grid = new Grid<>(User.class);
        grid.setSizeFull();
        grid.setColumns("id", "username");
        grid.addColumn((ValueProvider<User, Integer>) user -> user.getRoles().size());
        grid.getColumns().get(2).setCaption("Roles");
        return grid;
    }


    @Override
    protected boolean isGridEditable() {

        return false;
    }


    @Override
    protected Button initializeDetailsButton() {
        Button detailsButton = new Button("Details", VaadinIcons.EYE);
        if(!SecurityUtils.hasRole(Role.SUPER_ADMIN)) {
            detailsButton.setEnabled(false);
        }
        return detailsButton;
    }


    @Override
    protected Button initializeNewButton() {
        Button newButton = new Button("New", VaadinIcons.PLUS);
        if(!SecurityUtils.hasRole(Role.SUPER_ADMIN)) {
            newButton.setEnabled(false);

        }
        return newButton;
    }


    @Override
    protected Button initializeEditButton() {


        Button editButton = new Button("Edit", VaadinIcons.PENCIL);
        if(!SecurityUtils.hasRole(Role.SUPER_ADMIN)) {
            editButton.setEnabled(false);
            editButton.setDescription("t");
        }
        return editButton;
    }


    @Override
    protected Button initializeDeleteButton() {


        Button deleteButton = new Button("Delete", VaadinIcons.TRASH);
        if(!SecurityUtils.hasRole(Role.SUPER_ADMIN)) {
            deleteButton.setEnabled(false);
            deleteButton.setDescription("t");
        }
        return deleteButton;

    }


    @Override
    public Collection<User> fetchDataSet() {

        return userService.findAll();
    }
}
