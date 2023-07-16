package ru.practicum.main.users.services;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.users.dto.UserDto;
import ru.practicum.main.users.dto.UserPrivacyConfig;
import ru.practicum.main.users.dto.UserShortDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    void deleteById(Long userId);

    List<UserDto> findUsers(List<Long> userIds, Pageable pageable);

    List<UserShortDto> findSubscribes(Long bloggerId, Boolean isIncoming);

    UserPrivacyConfig updatePrivacyConfiguration(Long bloggerId, UserPrivacyConfig config);

    List<UserShortDto> putSubscriberInBlackList(Long bloggerId, Long subscriberId);
}