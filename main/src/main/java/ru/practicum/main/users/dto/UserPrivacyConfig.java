package ru.practicum.main.users.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserPrivacyConfig {
    private String subscribersMode;
    private String createdEventVisionMode;
    private String participationEventVisionMode;
}