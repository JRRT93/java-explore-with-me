package ru.practicum.main.requests.services;

import ru.practicum.main.requests.dto.ParticipationRequestDto;
import ru.practicum.main.requests.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.requests.model.EventRequestStatusUpdateResult;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto saveRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> findRequestsByOwnerEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> findAllRequests(Long userId);

    EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}