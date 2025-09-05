package pl.coderslab.carrental.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import pl.coderslab.carrental.dto.UserDto;
import pl.coderslab.carrental.mapper.UserMapper;
import pl.coderslab.carrental.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private static final String USER_WITH_ID_S_NOT_FOUND = "User with id %s not found";
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto findById(Long id) {

        log.info("Invoked find user by id method");

        return userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format(USER_WITH_ID_S_NOT_FOUND, id)));

    }

    public List<UserDto> findAll() {
        log.info("Invoked find all users");

        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public UserDto addUser(UserDto userDto) {
        log.info("Invoked add user");

        if (userDto != null) {

            var userEntity = userMapper.toUser(userDto);
            hashPassword(userDto.getPassword());
            userEntity.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt()));

            return userMapper.toUserDto(userRepository.save(userEntity));
        } else {
            throw new IllegalArgumentException("UserDto is null");
        }
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Invoked update user");
        if (userDto != null && id != null) {

            var userEntity = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(USER_WITH_ID_S_NOT_FOUND, id)));
            userEntity.setName(userDto.getName());
            userEntity.setEmail(userDto.getEmail());
            userEntity.setPassword(userDto.getPassword());
            userEntity.setPhone(userDto.getPhone());
            userEntity.setRoles(userDto.getRoles());

            log.info("User with id {} updated", id);
            return userMapper.toUserDto(userRepository.save(userEntity));
        } else {
            throw new IllegalArgumentException("UserDto and/or id cannot be empty.");
        }
    }

    public void deleteUser(Long id) {
        log.info("Invoked delete user");

        if (id != null) {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                log.info("User with id {} deleted", id);
            } else {
                throw new EntityNotFoundException(String.format(USER_WITH_ID_S_NOT_FOUND, id));
            }
        } else {
            throw new IllegalArgumentException("Id cannot be empty.");
        }
    }

    private String hashPassword(String password) {

        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


}
