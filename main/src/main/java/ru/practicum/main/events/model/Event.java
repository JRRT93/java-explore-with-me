package ru.practicum.main.events.model;

import lombok.*;
import ru.practicum.main.categories.model.Category;
import ru.practicum.main.events.enums.PublicStatus;
import ru.practicum.main.locations.model.Location;
import ru.practicum.main.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2000, nullable = false)
    private String annotation;
    @Column(length = 7000, nullable = false)
    private String description;
    @Column(length = 120, nullable = false)
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private PublicStatus state;
    @Column(name = "participants_limit", nullable = false)
    private Integer participantLimit;
    @Column(name = "confirmed_requests", nullable = false)
    private Integer confirmedRequests;
    @Column(name = "is_paid", nullable = false)
    private Boolean paid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locations_id", nullable = false)
    private Location location;
    @Column(name = "creation_date", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdOn;
    @Column(name = "event_date", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime eventDate;
    @Column(name = "published_on", columnDefinition = "TIMESTAMP")
    private LocalDateTime publishedOn;
    @Column(nullable = false)
    private Boolean requestModeration;
    @Column
    private Long views;
}