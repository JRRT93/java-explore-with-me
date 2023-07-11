package ru.practicum.main.events.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main.categories.mappers.CategoryMapper;
import ru.practicum.main.events.dto.*;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.locations.mappers.LocationMapper;
import ru.practicum.main.users.mappers.UserShortMapper;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final LocationMapper locationMapper;
    private final CategoryMapper categoryMapper;
    private final UserShortMapper userShortMapper;

    public Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .id(newEventDto.getId())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(locationMapper.dtoToModel(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .initiator(userShortMapper.modelToDto(event.getInitiator()))
                .category(categoryMapper.modelToDto(event.getCategory()))
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .category(categoryMapper.modelToDto(event.getCategory()))
                .initiator(userShortMapper.modelToDto(event.getInitiator()))
                .location(locationMapper.modelToDto(event.getLocation()))
                .build();
    }

    public EventSimpleFieldsForUpdate toSimpleEvent(UpdateEventUserRequest dto) {
        return EventSimpleFieldsForUpdate.builder()
                .requestModeration(dto.getRequestModeration())
                .participantLimit(dto.getParticipantLimit())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .title(dto.getTitle())
                .paid(dto.getPaid())
                .build();
    }

    public EventSimpleFieldsForUpdate toSimpleEvent(UpdateEventAdminRequest dto) {
        return EventSimpleFieldsForUpdate.builder()
                .requestModeration(dto.getRequestModeration())
                .participantLimit(dto.getParticipantLimit())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .title(dto.getTitle())
                .paid(dto.getPaid())
                .build();
    }
}