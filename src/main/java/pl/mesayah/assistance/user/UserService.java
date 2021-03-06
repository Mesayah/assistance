package pl.mesayah.assistance.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Service for managing user accounts.
 */
@Service
public class UserService {

    /**
     * Repository fetching users from database.
     */
    @Autowired
    private UserRepository userRepository;


    /**
     * Default constructor.
     */
    public UserService() {

    }

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     * Saves a given user entity in database.
     *
     * @param user user entity to save.
     */
    public void save(User user) {

        userRepository.save(user);
    }


    /**
     * Retrieves an user entity by its id.
     *
     * @param id given id.
     * @return the user entity with the given id or null if none found.
     */
    public User findById(Long id) {

        return userRepository.findOne(id);
    }


    /**
     * Retrieves an user entity with the given username.
     *
     * @param username given username.
     * @return the entity with the given username or null if none found.
     */
    public User findByUsername(String username) {

        return userRepository.findByUsername(username);
    }


    /**
     * Deletes the user entity with the given id.
     *
     * @param id given id.
     */
    public void delete(Long id) {

        userRepository.delete(id);
    }


    public Collection<User> findAll() {

        return (Collection<User>) userRepository.findAll();

    }
}
