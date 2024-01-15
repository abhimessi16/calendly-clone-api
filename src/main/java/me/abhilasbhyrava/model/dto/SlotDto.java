package me.abhilasbhyrava.model.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.abhilasbhyrava.model.Day;

@Getter
@Setter
@Builder
@ToString
public class SlotDto {

    private int start;
    private int end;
    private Day day;
    private boolean isAvailable;
}
