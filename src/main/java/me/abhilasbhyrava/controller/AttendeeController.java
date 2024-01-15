package me.abhilasbhyrava.controller;

import lombok.RequiredArgsConstructor;
import me.abhilasbhyrava.model.dto.AttendeeDto;
import me.abhilasbhyrava.model.Event;
import me.abhilasbhyrava.service.AttendeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendee")
public class AttendeeController {

    private final AttendeeService attendeeService;

    @GetMapping("/{username}")
    public ResponseEntity<AttendeeDto> getAttendee(@PathVariable("username") String email){

        AttendeeDto attendeeDto = attendeeService.getAttendee(email);
        return ResponseEntity.ok(attendeeDto);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addAttendee(@RequestBody AttendeeDto attendeeDto){

        attendeeService.addAttendee(attendeeDto);
        return ResponseEntity.ok("You can now attend events!");
    }

    @PutMapping("/{username}")
    public ResponseEntity<AttendeeDto> updateEvent(@PathVariable("username") String email, @RequestBody List<Event> events){

        AttendeeDto attendeeDto = attendeeService.updateAttendee(email, events);
        return ResponseEntity.ok(attendeeDto);
    }

    @PostMapping("/{username_attendee}/add-event/{username_organizer}")
    public ResponseEntity<String> addEventToAttendee(
            @PathVariable("username_organizer") String email_organizer,
            @PathVariable("username_attendee") String email_attendee,
            @RequestBody Event event){

        attendeeService.addEventToAttendee(email_organizer, email_attendee, event);
        return ResponseEntity.ok("Event added to organizer " + email_organizer + " and attendee " + email_attendee);
    }

    @PostMapping("/{username_attendee}/add-event-v2/{username_organizer}")
    public ResponseEntity<String> addEventToOrganizer(
            @PathVariable("username_organizer") String email_organizer,
            @PathVariable("username_attendee") String email_attendee,
            @RequestBody Event event){

        attendeeService.addEventToAttendee(email_attendee, email_organizer, event);
        return ResponseEntity.ok("Event added to organizer " + email_attendee + " and attendee " + email_organizer);
    }
}
