package ru.practicum.main.subscriptionRequests.model;

import lombok.*;
import ru.practicum.main.subscriptionRequests.enums.SubscriprionRequestStatus;
import ru.practicum.main.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "subscriprion_request")
public class SubscriptionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriprionRequestStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id")
    private User subscriber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blogger_id")
    private User blogger;
}