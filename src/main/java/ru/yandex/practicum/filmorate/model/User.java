package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class User {
    @EqualsAndHashCode.Include
    private int id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent
    private Date birthday;
}