package ru.practicum.main.users.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Blank username is prohibited")
    @NotNull(message = "Empty username is prohibited")
    @Size(min = 2, max = 250)
    private String name;
    @NotBlank(message = "Blank email is prohibited")
    @NotNull(message = "Empty email is prohibited")
    @Email(message = "Email should be like email pattern")
    @Size(min = 6, max = 254)
    private String email;
}