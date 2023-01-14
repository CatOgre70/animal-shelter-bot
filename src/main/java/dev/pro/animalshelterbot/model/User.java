package dev.pro.animalshelterbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.pro.animalshelterbot.constants.Shelter;

import javax.persistence.*;
import java.util.Objects;

/**
 * Represents client of the animal shelter. User class corresponds to the @Entity(name = "users") in PostgreSQL.
 * Model for the UsersRepository interface
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * userId - User identifier, primary key of the users table in PostgreSQL
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User first name
     */
    private String firstName;

    /**
     * User second name
     */
    private String secondName;

    /**
     * User nickname
     */
    private String nickName;

    /**
     * User home address where animal will live after adoption
     */
    private String address;

    /**
     * User mobile phone number
     */
    private String mobilePhone;

    /**
     * User chatId from Telegram
     */
    private Long chatId;

    /**
     * Adopted pet/animal
     */
    @JsonIgnoreProperties("owner")
    @OneToOne(mappedBy = "owner")
    private Animal adoptedAnimal;

    /**
     * Shelter chosen by user
     */
    private Shelter shelter;

    /**
     * Is this user a volunteer or not
     */
    private boolean isVolunteer;

    /**
     * User class empty constructor for Spring JPA and Hibernate
     */
    public User() {
        this.id = 0L;
        this.firstName = null;
        this.secondName = null;
        this.nickName = null;
        this.address = null;
        this.mobilePhone = null;
        this.chatId = 0L;
        this.isVolunteer = false;
    }

    /**
     * ChatConfig class constructor for using in the AnimalShelterBotApplication
     */
    public User(String firstName, String lastName, String nickName, String address, String mobilePhone, Long chatId) {
        this.id = 0L;
        this.firstName = firstName;
        this.secondName = lastName;
        this.nickName = nickName;
        this.address = address;
        this.mobilePhone = mobilePhone;
        this.chatId = chatId;
        this.isVolunteer = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String lastName) {
        this.secondName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Animal getAdoptedAnimal() {
        return adoptedAnimal;
    }

    public void setAdoptedAnimal(Animal adoptedAnimal) {
        this.adoptedAnimal = adoptedAnimal;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    public boolean isVolunteer() {
        return isVolunteer;
    }

    public void setVolunteer(boolean volunteer) {
        isVolunteer = volunteer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(firstName, user.firstName) &&
               Objects.equals(secondName, user.secondName) &&
               Objects.equals(nickName, user.nickName) &&
               Objects.equals(address, user.address) &&
               Objects.equals(mobilePhone, user.mobilePhone) &&
               Objects.equals(chatId, user.chatId) &&
               Objects.equals(shelter, user.shelter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, secondName, nickName, address, mobilePhone, chatId, shelter);
    }
}
