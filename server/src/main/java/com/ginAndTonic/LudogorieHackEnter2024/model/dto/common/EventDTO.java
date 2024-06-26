package com.ginAndTonic.LudogorieHackEnter2024.model.dto.common;

import com.ginAndTonic.LudogorieHackEnter2024.model.entity.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private Long id;

    private String name;

    private String description;

    private String address;

    private File picture;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
