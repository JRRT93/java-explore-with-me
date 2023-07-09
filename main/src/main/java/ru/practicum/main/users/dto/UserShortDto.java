package ru.practicum.main.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {
    private Long id;
    @NotBlank(message = "Blank username is prohibited")
    @NotNull(message = "Empty username is prohibited")
    private String name;
}