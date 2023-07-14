package ru.practicum.main.users.mappers;

import org.mapstruct.Mapper;
import ru.practicum.main.users.dto.UserDto;
import ru.practicum.main.users.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User dtoToModel(UserDto dto);

    UserDto modelToDto(User model);
}