package me.abhilasbhyrava.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.abhilasbhyrava.model.Slot;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class OrganizerDto {
    private String name;
    private String email;
    private List<Slot> slots;
}
