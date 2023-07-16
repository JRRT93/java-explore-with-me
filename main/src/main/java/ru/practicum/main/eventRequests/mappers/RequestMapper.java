package ru.practicum.main.eventRequests.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.eventRequests.dto.ParticipationRequestDto;
import ru.practicum.main.eventRequests.model.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto modelToDto(ParticipationRequest request);
}