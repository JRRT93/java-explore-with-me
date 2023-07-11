package ru.practicum.main.events.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.categories.model.Category;
import ru.practicum.main.categories.repositories.CategoryJpaRepository;
import ru.practicum.main.events.dto.*;
import ru.practicum.main.events.enums.AdminStatus;
import ru.practicum.main.events.enums.PrivateStatus;
import ru.practicum.main.events.enums.PublicStatus;
import ru.practicum.main.events.mappers.EventMapper;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.events.repositories.EventJpaRepository;
import ru.practicum.main.events.utils.EventUpdateUtil;
import ru.practicum.main.locations.mappers.LocationMapper;
import ru.practicum.main.locations.model.Location;
import ru.practicum.main.locations.repositories.LocationJpaRepository;
import ru.practicum.main.users.model.User;
import ru.practicum.main.users.repositories.UserJpaRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.StatRecordOut;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventJpaRepository eventRepository;
    private final UserJpaRepository userRepository;
    private final CategoryJpaRepository categoryRepository;
    private final LocationJpaRepository locationRepository;

    private final LocationMapper locationMapper;
    private final EventMapper eventMapper;

    private final StatsClient statsClient;

    private static final Long INITIAL_VIEWS = 0L;
    private static final Integer INITIAL_CONFIRMED_REQUEST = 0;

    @Override
    public EventFullDto saveEvent(Long userId, NewEventDto newEventDto) { //todo ready
        User user = checkAndGetUser(userId);

        Long categoryId = newEventDto.getCategory();
        Category category = checkAndGetCategory(categoryId);

        Location location = locationMapper.dtoToModel(newEventDto.getLocation());
        location = checkAndGetLocation(location);

        Event event = eventMapper.toEvent(newEventDto);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PublicStatus.PENDING);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setConfirmedRequests(INITIAL_CONFIRMED_REQUEST);
        event.setViews(INITIAL_VIEWS);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventFullByOwner(Long userId, Long eventId) { //todo ready
        checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsShortByOwner(Long userId, Pageable pageable) { //todo ready
        checkAndGetUser(userId);
        return eventRepository.findAllByInitiatorId(userId, pageable)
                .map(eventMapper::toEventShortDto)
                .getContent();
    }

    @Override
    public EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest) { //todo ready
        checkAndGetUser(userId);
        Event eventForUpdate = checkAndGetEvent(eventId);
        EventSimpleFieldsForUpdate simpleEvent = eventMapper.toSimpleEvent(eventUserRequest);
        EventUpdateUtil.simpleUpdateEvent(simpleEvent, eventForUpdate);

        if (eventForUpdate.getState() == PublicStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event already has Published state.");
        }
        if (eventUserRequest.getCategory() != null) {
            eventForUpdate.setCategory(checkAndGetCategory(eventUserRequest.getCategory()));
        }
        if (eventUserRequest.getLocation() != null) {
            Location location = locationMapper.dtoToModel(eventUserRequest.getLocation());
            location = checkAndGetLocation(location);
            eventForUpdate.setLocation(location);
        }
        if (eventUserRequest.getStateAction() != null) {
            PrivateStatus statePrivate = PrivateStatus.valueOf(eventUserRequest.getStateAction());
            if (statePrivate.equals(PrivateStatus.SEND_TO_REVIEW)) {
                eventForUpdate.setState(PublicStatus.PENDING);
            } else if (statePrivate.equals(PrivateStatus.CANCEL_REVIEW)) {
                eventForUpdate.setState(PublicStatus.CANCELED);
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(eventForUpdate));
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventUpdReqAdm) { //todo ready
        Event eventForUpdate = checkAndGetEvent(eventId);

        if (eventUpdReqAdm.getStateAction() != null) {
            AdminStatus adminStatus = AdminStatus.valueOf(eventUpdReqAdm.getStateAction());
            if (adminStatus.equals(AdminStatus.REJECT_EVENT) && eventForUpdate.getState().equals(PublicStatus.PUBLISHED)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Status conflict. Event is not Published");
            }
            if (!eventForUpdate.getState().equals(PublicStatus.PENDING) && adminStatus.equals(AdminStatus.PUBLISH_EVENT)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Status conflict. Event is not Pending");
            }
        }

        EventSimpleFieldsForUpdate simpleEvent = eventMapper.toSimpleEvent(eventUpdReqAdm);
        EventUpdateUtil.simpleUpdateEvent(simpleEvent, eventForUpdate);

        if (eventUpdReqAdm.getCategory() != null) {
            eventForUpdate.setCategory(checkAndGetCategory(eventUpdReqAdm.getCategory()));
        }
        if (eventUpdReqAdm.getLocation() != null) {
            Location location = locationMapper.dtoToModel(eventUpdReqAdm.getLocation());
            location = checkAndGetLocation(location);
            eventForUpdate.setLocation(location);
        }
        if (eventUpdReqAdm.getStateAction() != null) {
            AdminStatus statePrivate = AdminStatus.valueOf(eventUpdReqAdm.getStateAction());
            if (statePrivate.equals(AdminStatus.PUBLISH_EVENT)) {
                eventForUpdate.setState(PublicStatus.PUBLISHED);
            } else if (statePrivate.equals(AdminStatus.REJECT_EVENT)) {
                eventForUpdate.setState(PublicStatus.CANCELED);
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(eventForUpdate));
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Boolean onlyAvailable, String sort, Integer from,
                                         Integer size, HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong timestamps of START or END.");
        }
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);

        Specification<Event> specification = Specification.where(null);

        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), PublicStatus.PUBLISHED));

        List<Event> resultEvents = eventRepository.findAll(specification, pageable).getContent();
        setViewsOfEvents(resultEvents);

        return resultEvents.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong timestamps of START or END.");
        }
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);

        Specification<Event> specification = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("initiator").get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("state").as(String.class).in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("category").get("id").in(categories));
        }

        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        return eventRepository.findAll(specification, pageable).map(eventMapper::toEventFullDto).getContent();
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = checkAndGetEvent(eventId);
        if (event.getState() != PublicStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found.");
        }
        setViewsOfEvents(List.of(event));
        event.setViews(event.getViews() + 1);
        return eventMapper.toEventFullDto(event);
    }

    private Event checkAndGetEvent(Long eventId) {
        return eventRepository.findById(eventId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s with id = %d does not exist in database", "Event", eventId)));
    }

    private Location checkAndGetLocation(Location locationToCheck) {
        if (locationRepository.existsByLatAndLon(locationToCheck.getLat(), locationToCheck.getLon())) {
            return locationToCheck;
        } else {
            return locationRepository.save(locationToCheck);
        }
    }

    private User checkAndGetUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s with id = %d does not exist in database", "User", userId)));
    }

    private Category checkAndGetCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("%s with id = %d does not exist in database", "Category", categoryId)));
    }

    private void setViewsOfEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        List<StatRecordOut> viewStatsList = statsClient.getStats("2000-01-01 00:00:00", "2100-01-01 00:00:00", uris, false);

        for (Event event : events) {
            StatRecordOut currentViewStats = viewStatsList.stream()
                    .filter(statsDto -> {
                        Long eventIdOfViewStats = Long.parseLong(statsDto.getUri().substring("/events/".length()));
                        return eventIdOfViewStats.equals(event.getId());
                    })
                    .findFirst()
                    .orElse(null);

            Long views = (currentViewStats != null) ? currentViewStats.getHits() : 0;
            event.setViews(views);
        }
        eventRepository.saveAll(events);
    }
}