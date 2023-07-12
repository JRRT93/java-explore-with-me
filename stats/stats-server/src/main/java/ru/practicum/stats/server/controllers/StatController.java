package ru.practicum.stats.server.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.StatRecordIn;
import ru.practicum.stats.dto.StatRecordOut;
import ru.practicum.stats.server.servises.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService service;

    @GetMapping("/stats")
    public List<StatRecordOut> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("/stats REQUEST received. Options: start={}, end={}, urisList={}, unique={}",
                start, end, uris, unique);
        return service.getAllStatRecords(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveStatRecord(@RequestBody StatRecordIn statRecordIn) {
        log.info("/hit REQUEST received. StatRecordIn={}", statRecordIn);
        service.saveStatRecord(statRecordIn);
    }
}