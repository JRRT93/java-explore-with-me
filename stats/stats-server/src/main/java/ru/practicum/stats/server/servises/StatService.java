package ru.practicum.stats.server.servises;

import ru.practicum.stats.dto.StatRecordIn;
import ru.practicum.stats.dto.StatRecordOut;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void saveStatRecord(StatRecordIn statRecordIn);

    List<StatRecordOut> getAllStatRecords(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}