package hexlet.code.service;

import hexlet.code.model.User;
import hexlet.code.dto.UserDto;

public interface UserService {

    User createNewUser(UserDto userDto);

    User updateUser(long id, UserDto userDto);

    Long getCurrentUserId();

    User getCurrentUser();
}

