package ru.practicum.stats.server.servises;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.stats.dto.StatRecordIn;
import ru.practicum.stats.dto.StatRecordOut;
import ru.practicum.stats.server.mappers.MapperIn;
import ru.practicum.stats.server.models.StatRecord;
import ru.practicum.stats.server.repositories.StatJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatJpaRepository repository;
    private final MapperIn mapperIn;

    @Override
    public void saveStatRecord(StatRecordIn statRecordIn) {
        StatRecord statRecord = mapperIn.dtoToModel(statRecordIn);
        repository.save(statRecord);
    }

    @Override
    public List<StatRecordOut> getAllStatRecords(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean isUnique) {
        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request. Start date cant be after end date");
        }
        if (isUnique) {
            if (uris != null) {
                return repository.findStatRecordUniqueIpWithUri(uris, start, end);
            }
            return repository.findStatRecordUniqueIpNoUri(start, end);

        } else {
            if (uris != null) {
                return repository.findAllStatRecordWithUri(uris, start, end);
            }
            return repository.findAllStatRecordNoUri(start, end);
        }
    }
}