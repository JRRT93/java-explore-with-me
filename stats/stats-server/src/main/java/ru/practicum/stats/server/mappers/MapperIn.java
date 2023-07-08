package ru.practicum.stats.server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.dto.StatRecordIn;
import ru.practicum.stats.server.models.StatRecord;

@Mapper(componentModel = "spring")
public interface MapperIn {
    @Mapping(target = "app", source = "dtoIn.app")
    @Mapping(target = "uri", source = "dtoIn.uri")
    @Mapping(target = "ip", source = "dtoIn.ip")
    @Mapping(target = "requestDateTime", source = "dtoIn.timestamp")
    StatRecord dtoToModel(StatRecordIn dtoIn);

    @Mapping(target = "app", source = "statRecord.app")
    @Mapping(target = "uri", source = "statRecord.uri")
    @Mapping(target = "ip", source = "statRecord.ip")
    @Mapping(target = "timestamp", source = "statRecord.requestDateTime")
    StatRecordIn modelToDto(StatRecord statRecord);
}