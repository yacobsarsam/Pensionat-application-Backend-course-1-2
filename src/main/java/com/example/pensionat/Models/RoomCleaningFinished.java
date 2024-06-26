package com.example.pensionat.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomCleaningFinished extends RumEvent{
    private int roomNumber;
    private LocalDateTime eventTime;
    private String eventType;
    private String person;

}
