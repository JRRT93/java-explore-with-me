package ru.practicum.main.locations.mappers;

import org.mapstruct.Mapper;
import ru.practicum.main.locations.dto.LocationDto;
import ru.practicum.main.locations.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location dtoToModel(LocationDto dto);

    LocationDto modelToDto(Location model);
}