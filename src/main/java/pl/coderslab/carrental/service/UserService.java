package pl.coderslab.carrental.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final RoleService roleService;

    @Cacheable(value = "user", key = "#id")
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {

        log.info("Invoked find user by id method");

        if (id != null) {
            var user = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(USER_WITH_ID_S_NOT_FOUND, id)));

            return userMapper.toUserDto(user);
        } else {
            throw new IllegalArgumentException("Id is null");
        }
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

            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new EntityExistsException("User already exists with that data");
            }

            if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
                userDto.getRoles().forEach(role ->
                        roleService.getRoleById(role.getId()));
            }

            var userEntity = userMapper.toUser(userDto);
            userRepository.save(userEntity);
            return userMapper.toUserDto(userEntity);
        } else {
            throw new IllegalArgumentException("UserDto is null");
        }
    }

    @CachePut(value = "user", key = "#id")
    public UserDto updateUser(Long id, UserDto userDto) {

        log.info("Invoked update user");

        if (userDto != null && id != null) {

            if (userRepository.existsByEmail(userDto.getEmail()) && userRepository.userIdDiffers(id)) {
                throw new EntityExistsException("User already exists with that data");
            }
            var userEntity = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(String.format(USER_WITH_ID_S_NOT_FOUND, id)));

            userEntity.setSurname(userDto.getSurname());
            userEntity.setPhone(userDto.getPhone());

            log.info("User with id {} updated", id);
            return userMapper.toUserDto(userRepository.save(userEntity));
        } else {
            throw new IllegalArgumentException("UserDto and/or id cannot be empty.");
        }
    }

    @CacheEvict(value = "user", key = "#id")
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
}
