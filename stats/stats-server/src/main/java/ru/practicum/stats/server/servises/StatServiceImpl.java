package ru.practicum.stats.server.servises;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
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
    private MapperIn mapperIn = Mappers.getMapper(MapperIn.class);

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

        List<StatRecordOut> statRecords;
        if (isUnique) {
            if (uris != null) {
                statRecords = repository.findStatRecordUniqueIpWithUri(uris, start, end);
            }
            statRecords = repository.findStatRecordUniqueIpNoUri(start, end);
        } else {
            if (uris != null) {
                statRecords = repository.findAllStatRecordWithUri(uris, start, end);
            }
            statRecords = repository.findAllStatRecordNoUri(start, end);
        }
        return statRecords;
    }
}