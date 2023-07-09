package ru.practicum.main.users.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.users.dto.UserShortDto;
import ru.practicum.main.users.model.User;

@Mapper(componentModel = "spring")
public interface UserShortMapper {
    @Mapping(target = "id", source = "userDto.id")
    @Mapping(target = "name", source = "userDto.name")
    User dtoToModel(UserShortDto userDto);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    UserShortDto modelToDto(User user);
}