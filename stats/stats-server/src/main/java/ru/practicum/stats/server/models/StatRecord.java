package ru.practicum.stats.server.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "records")
public class StatRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;
    @Column(name = "service", nullable = false)
    private String app;
    @Column(nullable = false)
    private String uri;
    @Column(name = "ip_address", nullable = false)
    private String ip;
    @Column(name = "handling_date", nullable = false)
    private LocalDateTime requestDateTime;
}