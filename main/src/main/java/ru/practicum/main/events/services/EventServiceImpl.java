package ru.practicum.main.events.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.categories.mappers.CategoryMapper;
import ru.practicum.main.categories.model.Category;
import ru.practicum.main.categories.repositories.CategoryJpaRepository;
import ru.practicum.main.categories.services.CategoryService;
import ru.practicum.main.events.dto.*;
import ru.practicum.main.events.enums.AdminStatus;
import ru.practicum.main.events.enums.PrivateStatus;
import ru.practicum.main.events.enums.PublicStatus;
import ru.practicum.main.events.mappers.EventMapper;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.events.repositories.EventJpaRepository;
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
    private final CategoryMapper categoryMapper;
    private final EventMapper eventMapper;

    private final CategoryService categoryService;
    private final StatsClient statsClient;

    @Override
    public EventFullDto saveEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s with id = %d does not exist in database", "User", userId)));

        Long categoryId = newEventDto.getCategory(); //todo тут может быть проблем из-за несоответствия классов. Хотя обёртка
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s with id = %d does not exist in database", "Category", categoryId)));

        Location location = locationMapper.dtoToModel(newEventDto.getLocation());
        location = checkAndSetLocation(location);

        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PublicStatus.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0L);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventFullByOwner(Long userId, Long eventId) {
        return eventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId));
    }

    @Override
    public List<EventShortDto> getEventsShortByOwner(Long userId, Integer from, Integer size) {
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);
        return eventRepository.findAllByInitiatorId(userId, pageable).map(eventMapper::toEventShortDto).getContent();
    }

    @Override
    public EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event.getState() == PublicStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Can't change, because it already Published.");
        }
        if (eventUserRequest.getCategory() != null) {
            event.setCategory(categoryMapper.dtoToModel(categoryService.findById(eventUserRequest.getCategory())));
        }
        if (eventUserRequest.getAnnotation() != null) {
            event.setAnnotation(eventUserRequest.getAnnotation());
        }
        if (eventUserRequest.getDescription() != null) {
            event.setDescription(eventUserRequest.getDescription());
        }
        if (eventUserRequest.getEventDate() != null) {
            event.setEventDate(eventUserRequest.getEventDate());
        }
        if (eventUserRequest.getLocation() != null) {
            Location location = locationMapper.dtoToModel(eventUserRequest.getLocation());
            location = checkAndSetLocation(location);
            event.setLocation(location);
        }
        if (eventUserRequest.getPaid() != null) {
            event.setPaid(eventUserRequest.getPaid());
        }
        if (eventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUserRequest.getParticipantLimit());
        }
        if (eventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(eventUserRequest.getRequestModeration());
        }
        if (eventUserRequest.getTitle() != null) {
            event.setTitle(eventUserRequest.getTitle());
        }
        if (eventUserRequest.getStateAction() != null) {
            PrivateStatus statePrivate = PrivateStatus.valueOf(eventUserRequest.getStateAction());
            if (statePrivate.equals(PrivateStatus.SEND_TO_REVIEW)) {
                event.setState(PublicStatus.PENDING);
            } else if (statePrivate.equals(PrivateStatus.CANCEL_REVIEW)) {
                event.setState(PublicStatus.CANCELED);
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventUpdReqAdm) {
        Event eventForUpdate = eventRepository.findById(eventId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("%s with id = %d does not exist in database", "Event", eventId)));

        if (eventUpdReqAdm.getStateAction() != null) {
            AdminStatus adminStatus = AdminStatus.valueOf(eventUpdReqAdm.getStateAction());
            if (adminStatus.equals(AdminStatus.REJECT_EVENT) && eventForUpdate.getState().equals(PublicStatus.PUBLISHED)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Status conflict. Event is not Published");
            }
            if (!eventForUpdate.getState().equals(PublicStatus.PENDING) && adminStatus.equals(AdminStatus.PUBLISH_EVENT)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Status conflict. Event is not Pending");
            }
        }
        if (eventUpdReqAdm.getCategory() != null) {
            eventForUpdate.setCategory(categoryMapper.dtoToModel(categoryService.findById(eventUpdReqAdm.getCategory())));
        }
        if (eventUpdReqAdm.getAnnotation() != null) {
            eventForUpdate.setAnnotation(eventUpdReqAdm.getAnnotation());
        }
        if (eventUpdReqAdm.getDescription() != null) {
            eventForUpdate.setDescription(eventUpdReqAdm.getDescription());
        }
        if (eventUpdReqAdm.getEventDate() != null) {
            eventForUpdate.setEventDate(eventUpdReqAdm.getEventDate());
        }
        if (eventUpdReqAdm.getLocation() != null) {
            Location location = locationMapper.dtoToModel(eventUpdReqAdm.getLocation());
            location = checkAndSetLocation(location);
            eventForUpdate.setLocation(location);
        }
        if (eventUpdReqAdm.getPaid() != null) {
            eventForUpdate.setPaid(eventUpdReqAdm.getPaid());
        }
        if (eventUpdReqAdm.getParticipantLimit() != null) {
            eventForUpdate.setParticipantLimit(eventUpdReqAdm.getParticipantLimit());
        }
        if (eventUpdReqAdm.getRequestModeration() != null) {
            eventForUpdate.setRequestModeration(eventUpdReqAdm.getRequestModeration());
        }
        if (eventUpdReqAdm.getTitle() != null) {
            eventForUpdate.setTitle(eventUpdReqAdm.getTitle());
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
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        if (event.getState() != PublicStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found.");
        }
        setViewsOfEvents(List.of(event));
        event.setViews(event.getViews() + 1);
        return eventMapper.toEventFullDto(event);
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

    private Location checkAndSetLocation(Location locationToCheck) {
        if (locationRepository.existsByLatAndLon(locationToCheck.getLat(), locationToCheck.getLon())) {
            return locationToCheck;
        } else {
            return locationRepository.save(locationToCheck);
        }
    }
}