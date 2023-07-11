package ru.practicum.main.events.services;

import ru.practicum.main.events.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventFullByOwner(Long userId, Long eventId);

    List<EventShortDto> getEventsShortByOwner(Long userId, Integer from, Integer size);

    EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest);

    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  Boolean onlyAvailable, String sort, Integer from, Integer size,
                                  HttpServletRequest request);

    List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    Integer from, Integer size);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}