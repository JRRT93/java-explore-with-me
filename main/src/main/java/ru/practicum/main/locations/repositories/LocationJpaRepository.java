package ru.practicum.main.locations.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.locations.model.Location;

@Repository
public interface LocationJpaRepository extends JpaRepository<Location, Long> {
    Boolean existsByLatAndLon(Float lat, Float lon);
}