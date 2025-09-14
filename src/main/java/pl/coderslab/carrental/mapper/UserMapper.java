package pl.coderslab.carrental.mapper;

import org.springframework.stereotype.Component;
import pl.coderslab.carrental.dto.UserDto;
import pl.coderslab.carrental.model.User;

import java.util.HashSet;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .surname(user.getSurname())
                .password(user.getPassword())
                .roles(new HashSet<>(user.getRoles()))
                .build();
    }

    public User toUser(UserDto userDto) {

        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .phone(userDto.getPhone())
                .surname(userDto.getSurname())
                .password(userDto.getPassword())
                .roles(userDto.getRoles())
                .build();
    }
}
