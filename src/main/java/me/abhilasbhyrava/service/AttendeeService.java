package me.abhilasbhyrava.service;

import lombok.RequiredArgsConstructor;
import me.abhilasbhyrava.model.Attendee;
import me.abhilasbhyrava.model.Organizer;
import me.abhilasbhyrava.model.dto.AttendeeDto;
import me.abhilasbhyrava.model.Event;
import me.abhilasbhyrava.model.User;
import me.abhilasbhyrava.model.exception.BaseException;
import me.abhilasbhyrava.repository.AttendeeRepository;
import me.abhilasbhyrava.repository.EventRepository;
import me.abhilasbhyrava.repository.OrganizerRepository;
import me.abhilasbhyrava.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private final UserRepository userRepository;
    private final AttendeeRepository attendeeRepository;
    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;

    public AttendeeDto getAttendee(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new BaseException(404, "No such user present!");

        Optional<Attendee> optionalAttendee = attendeeRepository.findByUserId(user.get().getId());
        if(optionalAttendee.isEmpty())
            throw new BaseException(404, "No Attendee found");

        return AttendeeDto.builder()
                .name(user.get().getName())
                .email(user.get().getEmail())
                .events(optionalAttendee.get().getEvents())
                .build();
    }


    public void addAttendee(AttendeeDto attendeeDto) {
        Optional<User> user = userRepository.findByEmail(attendeeDto.getEmail());
        if(user.isEmpty())
            throw new BaseException(404, "No such user present!");

        Optional<Attendee> optionalAttendee = attendeeRepository.findByUserId(user.get().getId());
        if(optionalAttendee.isPresent())
            throw new BaseException(400, "User already registered as attendee!");

        List<Event> events = attendeeDto.getEvents().stream().map(eventRepository::save).toList();

        Attendee attendee = Attendee.builder()
                .user(user.get())
                .events(events)
                .build();

        attendee = attendeeRepository.save(attendee);
        if(ObjectUtils.isEmpty(attendee))
            throw new BaseException(400, "Couldn't register attendee.");
    }

    public AttendeeDto updateAttendee(String email, List<Event> events) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new BaseException(404, "No such user present!");

        Optional<Attendee> optionalAttendee = attendeeRepository.findByUserId(user.get().getId());
        if(optionalAttendee.isEmpty())
            throw new BaseException(404, "No Attendee to update");

        events = events.stream().map(eventRepository::save).toList();

        Attendee attendee = optionalAttendee.get();
        attendee.setEvents(events);
        attendee = attendeeRepository.save(attendee);

        return AttendeeDto.builder()
                .name(user.get().getName())
                .email(user.get().getEmail())
                .events(attendee.getEvents())
                .build();
    }

    public void addEventToAttendee(String email_organizer, String email_attendee, Event event) {
        Optional<User> userOrganizer = userRepository.findByEmail(email_organizer);
        if(userOrganizer.isEmpty())
            throw new BaseException(404, "No such Organizer present!");

        Optional<User> userAttendee = userRepository.findByEmail(email_attendee);
        if(userAttendee.isEmpty())
            throw new BaseException(404, "No such Attendee present!");

        Optional<Attendee> optionalAttendee = attendeeRepository.findByUserId(userAttendee.get().getId());
        if(optionalAttendee.isEmpty())
            throw new BaseException(404, "No Attendee to add Event to");

        Optional<Organizer> optionalOrganizer = organizerRepository.findByUserId(userOrganizer.get().getId());
        if(optionalOrganizer.isEmpty())
            throw new BaseException(404, "No such Organizer");

        GoogleCalendarService calendarService = new GoogleCalendarService(userOrganizer.get().getToken().getAccessToken());
        Organizer organizer = organizerRepository.findByUserId(userOrganizer.get().getId())
                .orElseThrow(() -> new BaseException(400, "No such Organizer"));

        String calendarId = calendarService.checkCalendlyCloneCalendarPresent();
        organizer.setCalendarId(calendarId);
        organizerRepository.save(organizer);

        event = eventRepository.save(event);

        calendarService.addEventOrganizerToAttendee(calendarId, optionalAttendee.get(), event);

        Attendee attendee = optionalAttendee.get();
        attendee.getEvents().add(event);
        attendeeRepository.save(attendee);
    }

}
