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
    public EventFullDto saveEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) { //todo ready
        log.info("POST request for /users/{}/events received. Provided DTO: {}", userId, newEventDto);
        return eventService.saveEvent(userId, newEventDto);
    }

    @GetMapping
    List<EventShortDto> getEventShortByOwner(@PathVariable Long userId,
                                             @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получаем запрос на список эвентов от пользователя: userId={}, from={}, size={}", userId, from, size);
        List<EventShortDto> eventShortDtoList = eventService.getEventsShortByOwner(userId, from, size);
        log.info("Возвращаем {} элемент(а/ов)", eventShortDtoList.size());
        return eventShortDtoList;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventFullByOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получаем запрос на эвент от пользователя: userId={}, eventId={}", userId, eventId);
        EventFullDto eventFullDto = eventService.getEventFullByOwner(userId, eventId);
        log.info("Возвращаем eventFullDto={}", eventFullDto);
        return eventFullDto;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByOwner(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid UpdateEventUserRequest eventUserRequest) {
        log.info("Получаем запрос на обновление: userId={}, eventId={}, updateEventUserRequest={}", userId, eventId, eventUserRequest);
        EventFullDto eventFullDto = eventService.updateEventByOwner(userId, eventId, eventUserRequest);
        log.info("Возвращаем обновленный эвент: eventFullDto={}", eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByOwnerEvent(@PathVariable Long userId,
                                                                 @PathVariable Long eventId) {
        log.info("Получаем запрос на список заявок: userId={}, eventId={}", userId, eventId);
        List<ParticipationRequestDto> requestDtoList = requestService.getRequestsByOwnerEvent(userId, eventId);
        log.info("Возвращаем {} элемент(а/ов)", requestDtoList.size());
        return requestDtoList;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequests(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Получаем запрос на изменение статуса события: userId={}, eventId={}, request={}", userId, eventId, request);
        EventRequestStatusUpdateResult updateResult = requestService.updateStatusRequests(userId, eventId, request);
        log.info("Возвращаем измененную заявку: {}", updateResult);
        return updateResult;
    }
}