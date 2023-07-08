package ru.practicum.stats.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.StatRecordOut;
import ru.practicum.stats.server.models.StatRecord;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatJpaRepository extends JpaRepository<StatRecord, Long> {
    String NEW_STAT_RECORD_OUT_DTO = "new ru.practicum.stats.dto.StatRecordOut";

    @Query("SELECT " + NEW_STAT_RECORD_OUT_DTO + "(s.app, s.uri, COUNT(DISTINCT s.ip)) " + "FROM StatRecord AS s " +
            "WHERE s.requestDateTime BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<StatRecordOut> findStatRecordUniqueIpNoUri(LocalDateTime start, LocalDateTime end);

    @Query("SELECT " + NEW_STAT_RECORD_OUT_DTO + "(s.app, s.uri, COUNT(DISTINCT s.ip)) " + "FROM StatRecord AS s " +
            "WHERE s.uri IN (?1) AND s.requestDateTime BETWEEN ?2 AND ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<StatRecordOut> findStatRecordUniqueIpWithUri(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT " + NEW_STAT_RECORD_OUT_DTO + "(s.app, s.uri, COUNT(s.uri)) " + "FROM StatRecord AS s " +
            "WHERE s.requestDateTime BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.uri) DESC")
    List<StatRecordOut> findAllStatRecordNoUri(LocalDateTime start, LocalDateTime end);

    @Query("SELECT " + NEW_STAT_RECORD_OUT_DTO + "(s.app, s.uri, COUNT(s.uri)) " + "FROM StatRecord AS s " +
            "WHERE s.uri IN (?1) AND s.requestDateTime BETWEEN ?2 AND ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.uri) DESC")
    List<StatRecordOut> findAllStatRecordWithUri(List<String> uris, LocalDateTime start, LocalDateTime end);
}