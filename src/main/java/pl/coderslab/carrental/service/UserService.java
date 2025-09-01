package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pl.coderslab.carrental.dto.UserDto;
import pl.coderslab.carrental.mapper.UserMapper;
import pl.coderslab.carrental.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto findById(Long id){

        return userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s not found", id)));

    }
}
