package dev.pro.animalshelterbot.service;

import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * event recording process
     */
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * saving the animal in the database
     * the repository method is used {@link JpaRepository#save(Object)}
     * event recording process
     * @param user, must not be null
     * @return the user stored in the database
     */
    public User addUser(User user) {
        logger.info("Metod \"UserService.addUser()\" was called");
        return userRepository.save(user);
    }
    /**
     * find for an user by ID in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * event recording process
     * @param id user, must not be null
     * @return found user
     */
    public User findUser(long id) {
        logger.info("Metod \"UserService.findUser()\" was called");
        return userRepository.findById(id).orElse(null);
    }
    /**
     * edit for an animal by ID in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * fetching data from the database and modifying it
     * @param user
     * @return making changes to the database
     */
    public User editUser(User user) {
        logger.info("Metod \"UserService.editUser()\" was called");
        Optional<User> optional = userRepository.findById(user.getUserId());
        if(!optional.isPresent()) {
            return null;
        }
        else {
            User fromDb = optional.get();
            fromDb.setFirstName(user.getFirstName());
            fromDb.setSecondName(user.getSecondName());
            fromDb.setAddress(user.getAddress());
            fromDb.setMobilePhone(user.getMobilePhone());
            return userRepository.save(fromDb);
        }
    }
    /**
     * delete for an animal by ID in the database
     * the repository method is used {@link JpaRepository#deleteById(Object)}
     * event recording process
     * @param id, must not be null
     */
    public void deleteUser(long id) {
        logger.info("Metod \"UserService.deleteStudent()\" was called");
        userRepository.deleteById(id);
    }
}
