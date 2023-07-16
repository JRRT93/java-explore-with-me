package ru.practicum.main.eventRequests.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.eventRequests.dto.ParticipationRequestDto;
import ru.practicum.main.eventRequests.services.RequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
public class RequestController {
    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto saveRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("POST request for /users/{}/requests?eventId={} received", userId, eventId);
        return service.saveRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("PATCH request for /users/{}/requests/{}/cancel received", userId, requestId);
        return service.cancelRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> findAllRequests(@PathVariable Long userId) {
        log.info("PATCH request for /users/{}/requests received", userId);
        return service.findAllRequests(userId);
    }
}