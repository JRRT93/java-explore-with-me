package ru.practicum.main.eventRequests.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.events.enums.PublicStatus;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.events.repositories.EventJpaRepository;
import ru.practicum.main.eventRequests.dto.ParticipationRequestDto;
import ru.practicum.main.eventRequests.enums.RequestStatus;
import ru.practicum.main.eventRequests.mappers.RequestMapper;
import ru.practicum.main.eventRequests.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.eventRequests.model.EventRequestStatusUpdateResult;
import ru.practicum.main.eventRequests.model.ParticipationRequest;
import ru.practicum.main.eventRequests.repositories.RequestJpaRepository;
import ru.practicum.main.users.model.User;
import ru.practicum.main.users.repositories.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestJpaRepository requestRepository;
    private final EventJpaRepository eventRepository;
    private final UserJpaRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto saveRequest(Long userId, Long eventId) {
        User initiator = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);

        if (event.getInitiator().equals(initiator)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request for your own event is prohibited");
        }
        if (!event.getState().equals(PublicStatus.PUBLISHED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Status conflict. Event is not Published");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request is already exists");
        }
        if (!event.getParticipantLimit().equals(0) && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No more available places for Event");
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .event(event)
                .requester(initiator)
                .created(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .build();

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            request.setStatus(RequestStatus.CONFIRMED);
            eventRepository.save(event);
            initiator.getConfirmedEvents().add(event);
            userRepository.save(initiator);
        }
        return requestMapper.modelToDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> findRequestsByOwnerEvent(Long userId, Long eventId) {
        checkAndGetUser(userId);
        checkAndGetEvent(eventId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::modelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> findAllRequests(Long userId) {
        checkAndGetUser(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::modelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkAndGetUser(userId);
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId);
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.modelToDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        User initiator = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);

        if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No more available places for Event");
        }
        if (!event.getInitiator().equals(initiator)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not Initiator for that Event.");
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndIdIn(eventId, request.getRequestIds()).stream()
                .peek(req -> {
                    if (req.getStatus() != RequestStatus.PENDING) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Status changing is prohibited");
                    }
                    if (request.getStatus() == RequestStatus.REJECTED) {
                        req.setStatus(request.getStatus());
                        rejected.add(requestMapper.modelToDto(req));
                    }
                    if (event.getConfirmedRequests() < event.getParticipantLimit() &&
                            request.getStatus() == RequestStatus.CONFIRMED) {
                        req.setStatus(request.getStatus());
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        confirmed.add(requestMapper.modelToDto(req));
                        User participant = req.getRequester(); //todo возможно будет падать из-за этого места
                        participant.getConfirmedEvents().add(event);
                        userRepository.save(participant);
                    } else {
                        req.setStatus(RequestStatus.REJECTED);
                        rejected.add(requestMapper.modelToDto(req));
                    }
                })
                .collect(Collectors.toList());

        eventRepository.save(event);
        requestRepository.saveAll(requests);
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    private User checkAndGetUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("%s with id = %d does not exist in database", "User", userId)));
    }

    private Event checkAndGetEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s with id = %d does not exist in database", "Event", eventId)));
    }
}