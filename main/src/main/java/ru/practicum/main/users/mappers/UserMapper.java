package ru.practicum.main.users.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.users.dto.UserDto;
import ru.practicum.main.users.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "email", source = "dto.email")
    User dtoToModel(UserDto dto);

    @Mapping(target = "id", source = "model.id")
    @Mapping(target = "name", source = "model.name")
    @Mapping(target = "email", source = "model.email")
    UserDto modelToDto(User model);
}