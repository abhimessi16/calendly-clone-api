package me.abhilasbhyrava.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.abhilasbhyrava.model.Event;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class AttendeeDto {

    private String name;
    private String email;
    private List<Event> events;
}
