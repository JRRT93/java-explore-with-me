package ru.practicum.main.events.services;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.events.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    EventFullDto findEventFullByOwner(Long userId, Long eventId);

    List<EventShortDto> findEventsShortByOwner(Long userId, Pageable pageable);

    EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest);

    EventFullDto findEventById(Long eventId);

    List<EventShortDto> findEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Pageable pageable);

    List<EventFullDto> findFullEvents(List<Long> users, List<String> states, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);
}