package ru.practicum.main.users.mappers;

import org.mapstruct.Mapper;
import ru.practicum.main.users.dto.UserShortDto;
import ru.practicum.main.users.model.User;

@Mapper(componentModel = "spring")
public interface UserShortMapper {
    User dtoToModel(UserShortDto userDto);

    UserShortDto modelToDto(User user);
}