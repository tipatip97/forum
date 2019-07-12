package ru.pon.demo.model;

import ru.pon.demo.model.validator.PasswordMatches;
import ru.pon.demo.model.validator.ValidPassword;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@PasswordMatches
public class UserDTO {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    @ValidPassword
    private String password;
    private String matchingPassword;

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