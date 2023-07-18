package ru.practicum.main.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.events.model.Event;
import ru.practicum.main.users.enums.EventVisionMode;
import ru.practicum.main.users.enums.SubscribersMode;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @ManyToMany
    @JoinTable(
            name = "participant_confirmed_events",
            joinColumns = @JoinColumn(name = "participant_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> confirmedEvents;
    @ManyToMany
    @JoinTable(
            name = "users_subscriptions",
            joinColumns = @JoinColumn(name = "blogger_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> subscribers;
    @ManyToMany(mappedBy = "subscribers")
    private Set<User> bloggersSubscribed;
    @ManyToMany
    @JoinTable(
            name = "users_event_vision_blacklist",
            joinColumns = @JoinColumn(name = "blogger_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> eventVisionBlackList;
    @Enumerated(EnumType.STRING)
    @Column(name = "created_event_vision_mode", length = 30)
    private EventVisionMode createdEventVisionMode;
    @Enumerated(EnumType.STRING)
    @Column(name = "participation_event_vision_mode", length = 30)
    private EventVisionMode participationEventVisionMode;
    @Enumerated(EnumType.STRING)
    @Column(name = "subscribers_mode", length = 30)
    private SubscribersMode subscribersMode;
}