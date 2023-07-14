package ru.practicum.main.events.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.categories.mappers.CategoryMapper;
import ru.practicum.main.events.dto.*;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.locations.mappers.LocationMapper;
import ru.practicum.main.users.mappers.UserShortMapper;

@Mapper(componentModel = "spring", uses = {LocationMapper.class, CategoryMapper.class, UserShortMapper.class})
public interface EventMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    Event dtoToModel(NewEventDto newEventDto);

    EventShortDto toEventShortDto(Event event);

    EventFullDto toEventFullDto(Event event);

    EventSimpleFieldsForUpdate toSimpleEvent(UpdateEventUserRequest dto);

    EventSimpleFieldsForUpdate toSimpleEvent(UpdateEventAdminRequest dto);
}