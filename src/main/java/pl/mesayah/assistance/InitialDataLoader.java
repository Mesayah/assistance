package pl.mesayah.assistance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.mesayah.assistance.security.privilege.PrivilegeService;
import pl.mesayah.assistance.security.role.Role;
import pl.mesayah.assistance.security.role.RoleRepository;
import pl.mesayah.assistance.security.role.RoleService;
import pl.mesayah.assistance.user.User;
import pl.mesayah.assistance.user.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class does everything that need to be done on application startup like creating roles and privileges.
 */
@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PrivilegeService privilegeService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        if (alreadySetup) {
            return;
        }

        privilegeService.createPrivileges();
        roleService.createRoles();

        // TODO: Delete line below on production phase.
        createTestUsers();

        alreadySetup = true;
    }


    private void createTestUsers() {

        Set<Role> adminRoles = new HashSet<>(Collections.singletonList(roleRepository.findByName(Role.SUPER_ADMIN)));
        User testUserWithAllRoles = new User(
                "admin",
                adminRoles,
                "Test",
                "User",
                passwordEncoder.encode("admin"),
                true);
        userRepository.save(testUserWithAllRoles);

        Set<Role> noRoles = null;
        User testUserWithNoRoles = new User(
                "user",
                noRoles,
                "Test",
                "User",
                passwordEncoder.encode("12345"),
                true);
        userRepository.save(testUserWithNoRoles);

        Set<Role> clientRoles = new HashSet<>(Collections.singletonList(roleRepository.findByName(Role.CLIENT)));
        User client = new User(
                "client",
                clientRoles,
                "Client",
                "User",
                passwordEncoder.encode("client"),
                true);
        userRepository.save(client);
    }
}
