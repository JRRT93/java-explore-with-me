package ru.practicum.main.compilations.dto;

import lombok.*;
import ru.practicum.main.events.dto.EventShortDto;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}