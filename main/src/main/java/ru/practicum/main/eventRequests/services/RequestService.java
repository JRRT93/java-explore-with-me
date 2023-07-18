package ru.practicum.main.eventRequests.services;

import ru.practicum.main.eventRequests.dto.ParticipationRequestDto;
import ru.practicum.main.eventRequests.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.eventRequests.model.EventRequestStatusUpdateResult;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto saveRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> findRequestsByOwnerEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> findAllRequests(Long userId);

    EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}