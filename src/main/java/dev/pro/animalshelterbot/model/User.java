package dev.pro.animalshelterbot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Represents client of the animal shelter. User class corresponds to the @Entity(name = "users") in PostgreSQL.
 * Model for the UsersRepository interface
 */
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String firstName;

    private String secondName;

    private String nickName;

    private String address;

    private String mobilePhone;

    private Long chatId;

    @OneToMany
    List<Animal> adoptedAnimals;

    public User() {
        this.firstName = null;
        this.secondName = null;
        this.nickName = null;
        this.address = null;
        this.mobilePhone = null;
        this.chatId = 0L;
    }

    public User(String firstName, String lastName, String nickName, String address, String mobilePhone, Long chatId) {
        this.firstName = firstName;
        this.secondName = lastName;
        this.nickName = nickName;
        this.address = address;
        this.mobilePhone = mobilePhone;
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return secondName;
    }

    public void setLastName(String lastName) {
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

    public List<Animal> getAdoptedAnimals() {
        return adoptedAnimals;
    }

    public void setAdoptedAnimals(List<Animal> adoptedAnimals) {
        this.adoptedAnimals = adoptedAnimals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(firstName, user.firstName) && Objects.equals(secondName, user.secondName) && Objects.equals(nickName, user.nickName) && Objects.equals(address, user.address) && Objects.equals(mobilePhone, user.mobilePhone) && Objects.equals(chatId, user.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, firstName, secondName, nickName, address, mobilePhone, chatId);
    }
}
