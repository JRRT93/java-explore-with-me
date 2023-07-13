package ru.practicum.main.events.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.events.dto.EventFullDto;
import ru.practicum.main.events.dto.EventShortDto;
import ru.practicum.main.events.dto.NewEventDto;
import ru.practicum.main.events.dto.UpdateEventUserRequest;
import ru.practicum.main.events.services.EventService;
import ru.practicum.main.requests.dto.ParticipationRequestDto;
import ru.practicum.main.requests.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.requests.model.EventRequestStatusUpdateResult;
import ru.practicum.main.requests.services.RequestService;
import ru.practicum.main.utils.GetPageableUtil;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Slf4j
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto saveEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("POST request for /users/{}/events received. Provided DTO: {}", userId, newEventDto);
        return eventService.saveEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventFullByOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET request for private /users/{}/events/{} received.", userId, eventId);
        return eventService.findEventFullByOwner(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getEventShortByOwner(@PathVariable Long userId,
                                             @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("GET request for private /users/{}/events received. from={}, size={}", userId, from, size);
        return eventService.findEventsShortByOwner(userId, GetPageableUtil.getPageable(from, size));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByOwner(@PathVariable Long userId, @PathVariable Long eventId,
                                           @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("PATCH request for private /users/{}/events/{} received. Provided DTO: {}",
                userId, eventId, updateEventUserRequest);
        return eventService.updateEventByOwner(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequests(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("PATCH request for private /users/{}/events/{}/request", userId, eventId);
        return requestService.updateStatusRequest(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByOwnerEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET request for private /users/{}/events/{}/requests", userId, eventId);
        return requestService.findRequestsByOwnerEvent(userId, eventId);
    }
}