package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Integer> friends;
    private Set<Integer> likedFilms;

    public void addFriend(int id) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(id);
    }

    public void removeFriend(int id) {
        if (friends != null) {
            friends.remove(id);
        }
    }

    public Set<Integer> getLikedFilms() {
        if (likedFilms == null) {
            likedFilms = new HashSet<>();
        }
        return likedFilms;
    }

    public void addLike(int filmId) {
        if (likedFilms == null) {
            likedFilms = new HashSet<>();
        }
        likedFilms.add(filmId);
    }

    public void removeLike(int filmId) {
        if (likedFilms == null) {
            likedFilms = new HashSet<>();
        }
        likedFilms.remove(filmId);
    }
}