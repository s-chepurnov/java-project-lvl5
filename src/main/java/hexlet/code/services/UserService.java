package hexlet.code.services;

import hexlet.code.dtos.UserDto;
import hexlet.code.model.User;

import java.util.List;


public interface UserService {

    List<User> findAll();

    User findUserById(Long id);

    User findUserByEmail(String email);

    User createUser(UserDto userDto);

    User updateUser(long id, UserDto userDto);

    void deleteUser(long id);
}
