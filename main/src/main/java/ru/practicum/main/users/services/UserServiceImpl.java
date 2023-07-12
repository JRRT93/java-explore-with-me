package ru.practicum.main.users.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.users.dto.UserDto;
import ru.practicum.main.users.mappers.UserMapper;
import ru.practicum.main.users.model.User;
import ru.practicum.main.users.repositories.UserJpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserJpaRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserDto saveUser(UserDto userDto) {
        try {
            return userMapper.modelToDto(repository.save(userMapper.dtoToModel(userDto)));
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
}