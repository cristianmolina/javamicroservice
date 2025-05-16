package com.example.user.infrastructure.controller.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.List;

//@Data
//@NoArgsConstructor
public class UserRequestDto {

    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=(.*\\d){2})(?=.*[a-zA-Z]).{8,12}$",
            message = "Password must have one uppercase letter, two digits, and be 8-12 characters long"
    )
    private String password;

    private List<PhoneDto> phones;

    public UserRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<PhoneDto> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneDto> phones) {
        this.phones = phones;
    }
}
