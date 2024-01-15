package me.abhilasbhyrava.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityRequest {

    private int year;
    private int month;
    private int day;
    private int weekDay;

}
