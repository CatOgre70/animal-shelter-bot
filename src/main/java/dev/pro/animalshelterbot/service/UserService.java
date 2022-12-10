package dev.pro.animalshelterbot.service;

import dev.pro.animalshelterbot.model.User;
import dev.pro.animalshelterbot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
     * saving the user in the database
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
     * find for a user by ID in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * event recording process
     * @param id user, must not be null
     * @return found user
     */
    public User findUser(long id) {
        logger.info("Method \"UserService.findUser()\" was called");
        return userRepository.findById(id).orElse(null);
    }
    /**
     * edit user in the database
     * the repository method is used {@link JpaRepository#findById(Object)}
     * fetching data from the database and modifying it
     * @param user
     * @return making changes to the database
     */
    public User editUser(User user) {
        logger.info("Metod \"UserService.editUser()\" was called");
        Optional<User> optional = userRepository.findById(user.getId());
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
     * delete user from the database
     * the repository method is used {@link JpaRepository#deleteById(Object)}
     * event recording process
     * @param id, must not be null
     */
    public void deleteUser(long id) {
        logger.info("Method \"UserService.deleteStudent()\" was called");
        userRepository.deleteById(id);
    }
    /**
     * find all users in the database
     * the repository method is used {@link JpaRepository#findAll}
     * @return Collection of found users
     */
    public Collection<User> getAllUser() {
        logger.info("Metod \"UserService.getAllUser()\" was called");
        return userRepository.findAll();
    }


    /**
     * find users in the database contains substrings in the firstName, secondName and nickName
     * @return Collection of found users
     */
    public Collection<User> findByThreeSubstrings(String firstNameSubstring,
                                                  String secondNameSubstring,
                                                  String nickNameSubstring){
        return userRepository.findByThreeSubstrings(firstNameSubstring, secondNameSubstring, nickNameSubstring);

    }

    /**
     * find users in the database by chatId
     * @return Collection of found users
     * There should be only one User in the Collection!!!
     */
    public Collection<User> findByChatId(Long chatId) {
        logger.info("Method \"UserService.findByChatId()\" was called");
        return userRepository.findByChatId(chatId);
    }

    /**
     * Checks if user with selected chatId already exists in the database
     * @return boolean (true - if user exists, false - if user is not found)
     */
    public boolean checkByChatId(Long chatId) {
        logger.info("Method \"UserService.checkByChatId()\" was called");
        return userRepository.existsByChatId(chatId);
    }

}
