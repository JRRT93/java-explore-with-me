package ru.practicum.main.users.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.users.dto.UserDto;
import ru.practicum.main.users.dto.UserPrivacyConfig;
import ru.practicum.main.users.dto.UserShortDto;
import ru.practicum.main.users.enums.EventVisionMode;
import ru.practicum.main.users.enums.SubscribersMode;
import ru.practicum.main.users.mappers.UserMapper;
import ru.practicum.main.users.mappers.UserShortMapper;
import ru.practicum.main.users.model.User;
import ru.practicum.main.users.repositories.UserJpaRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserJpaRepository repository;
    private final UserMapper userMapper;
    private final UserShortMapper userShortMapper;

    @Override
    public UserDto saveUser(UserDto userDto) {
        try {
            User user = userMapper.dtoToModel(userDto);
            user.setCreatedEventVisionMode(EventVisionMode.FOR_ALL_SUBSCRIBERS);
            user.setParticipationEventVisionMode(EventVisionMode.FOR_ALL_SUBSCRIBERS);
            user.setSubscribersMode(SubscribersMode.ALLOWED_FOR_ALL);
            return userMapper.modelToDto(repository.save(user));
        } catch (ConstraintViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("%s not saved due Constraint violations", "User"));
        }
    }

    @Override
    public void deleteById(Long userId) {
        if (repository.existsById(userId)) {
            repository.deleteById(userId);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("%s with id = %d does not exist in database", "User", userId));
        }
    }

    @Override
    public List<UserShortDto> findSubscribes(Long bloggerId, Boolean isIncoming) {
        User blogger = checkAndGetUser(bloggerId);
        if (isIncoming) {
            return blogger.getSubscribers().stream()
                    .map(userShortMapper::modelToDto)
                    .collect(Collectors.toList());
        } else {
            return blogger.getBloggersSubscribed().stream()
                    .map(userShortMapper::modelToDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public UserPrivacyConfig updatePrivacyConfiguration(Long bloggerId, UserPrivacyConfig config) {
        User blogger = checkAndGetUser(bloggerId);

        if (config.getSubscribersMode() != null) {
            blogger.setSubscribersMode(SubscribersMode.valueOf(config.getSubscribersMode()));
        }
        if (config.getParticipationEventVisionMode() != null) {
            blogger.setParticipationEventVisionMode(EventVisionMode.valueOf(config.getParticipationEventVisionMode()));
        }
        if (config.getCreatedEventVisionMode() != null) {
            blogger.setCreatedEventVisionMode(EventVisionMode.valueOf(config.getCreatedEventVisionMode()));
        }

        repository.save(blogger);
        return userMapper.modelToPrivacyConfig(blogger);
    }

    @Override
    public List<UserShortDto> putSubscriberInBlackList(Long bloggerId, Long subscriberId) {
        User blogger = checkAndGetUser(bloggerId);
        User subscriberForBan = checkAndGetUser(subscriberId);
        Set<Long> subscribers = blogger.getSubscribers().stream().map(User::getId).collect(Collectors.toSet());
        if (subscribers.contains(subscriberId)) {
            blogger.getEventVisionBlackList().add(subscriberForBan);
            repository.save(blogger);
            return blogger.getEventVisionBlackList().stream()
                    .map(userShortMapper::modelToDto)
                    .collect(Collectors.toList());
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("%s with id = %d is not your subscriber", "User", subscriberId));
        }
    }

    @Override
    public List<UserDto> findUsers(List<Long> userIds, Pageable pageable) {
        List<User> users;
        if (userIds != null) {
            users = repository.findAllByIdIn(userIds);
        } else {
            users = repository.findAll(pageable).getContent();
        }
        return users.stream()
                .map(userMapper::modelToDto)
                .collect(Collectors.toList());
    }

    private User checkAndGetUser(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("%s with id = %d does not exist in database", "User", userId)));
    }
}