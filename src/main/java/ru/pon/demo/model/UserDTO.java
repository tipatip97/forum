package ru.pon.demo.model;

import ru.pon.demo.model.validator.PasswordMatches;
import ru.pon.demo.model.validator.ValidPassword;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@PasswordMatches
public class UserDTO {

    @NotNull
    @NotEmpty(message = "Не может быть пустым")
    private String firstName;

    @NotNull
    @NotEmpty(message = "Не может быть пустым")
    private String lastName;

    @NotNull
    @NotEmpty(message = "Не может быть пустым")
    private String username;

    @NotNull
    @NotEmpty(message = "Не может быть пустым")
    @ValidPassword
    private String password;

    @NotEmpty(message = "Не может быть пустым")
    private String matchingPassword;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }
}
