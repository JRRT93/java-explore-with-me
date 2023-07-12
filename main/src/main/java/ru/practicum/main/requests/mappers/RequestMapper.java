package ru.practicum.main.requests.mappers;

import ru.practicum.main.requests.dto.ParticipationRequestDto;
import ru.practicum.main.requests.model.ParticipationRequest;

public class RequestMapper {
    public static ParticipationRequestDto modelToDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .event(participationRequest.getEvent().getId())
                .created(participationRequest.getCreated())
                .status(participationRequest.getStatus())
                .requester(participationRequest.getRequester().getId())
                .build();
    }
}