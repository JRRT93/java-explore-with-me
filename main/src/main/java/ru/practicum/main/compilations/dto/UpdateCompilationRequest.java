package ru.practicum.main.compilations.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50)
    String title;
    Boolean pinned;
    List<Long> events;
}