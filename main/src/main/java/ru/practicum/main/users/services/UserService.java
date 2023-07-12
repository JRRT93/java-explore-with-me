package ru.practicum.main.users.services;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.users.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    void deleteById(Long userId);

    List<UserDto> findUsers(List<Long> userIds, Pageable pageable);
}