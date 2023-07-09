package ru.practicum.main.locations.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.locations.dto.LocationDto;
import ru.practicum.main.locations.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    @Mapping(target = "lat", source = "dto.lat")
    @Mapping(target = "lon", source = "dto.lon")
    Location dtoToModel(LocationDto dto);

    @Mapping(target = "lat", source = "model.lat")
    @Mapping(target = "lon", source = "model.lon")
    LocationDto modelToDto(Location model);
}