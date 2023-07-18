package ru.practicum.main.eventRequests.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.eventRequests.model.ParticipationRequest;

import java.util.List;

@Repository
public interface RequestJpaRepository extends JpaRepository<ParticipationRequest, Long> {

    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    ParticipationRequest findByIdAndRequesterId(Long requestId, Long userId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    List<ParticipationRequest> findAllByEventIdAndIdIn(Long eventId, List<Long> requestId);
}