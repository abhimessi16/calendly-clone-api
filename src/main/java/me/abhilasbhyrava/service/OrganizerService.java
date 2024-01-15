package me.abhilasbhyrava.service;

import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import lombok.RequiredArgsConstructor;
import me.abhilasbhyrava.model.*;
import me.abhilasbhyrava.model.dto.OrganizerDto;
import me.abhilasbhyrava.model.dto.SlotDto;
import me.abhilasbhyrava.model.exception.BaseException;
import me.abhilasbhyrava.repository.EventRepository;
import me.abhilasbhyrava.repository.OrganizerRepository;
import me.abhilasbhyrava.repository.SlotRepository;
import me.abhilasbhyrava.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private final UserRepository userRepository;
    private final OrganizerRepository organizerRepository;
    private final SlotRepository slotRepository;
    private final EventRepository eventRepository;

    public Organizer checkOrganizerPresent(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new BaseException(404, "No such user present!");

        Optional<Organizer> optionalOrganizer = organizerRepository.findByUserId(user.get().getId());
        return optionalOrganizer.orElse(null);
    }

    public void add(OrganizerDto organizerDto) {
        Optional<User> user = userRepository.findByEmail(organizerDto.getEmail());
        if(user.isEmpty())
            throw new BaseException(404, "No such user present!");

        Optional<Organizer> optionalOrganizer = organizerRepository.findByUserId(user.get().getId());
        if(optionalOrganizer.isPresent())
            throw new BaseException(400, "Organizer already present!");

        GoogleCalendarService calendarService = new GoogleCalendarService(user.get().getToken().getAccessToken());
        String calendarId = calendarService.checkCalendlyCloneCalendarPresent();

        List<Slot> slots = organizerDto.getSlots().stream().map(slot -> {
            if(!ObjectUtils.isEmpty(slot.getEvent())){
                Event event = eventRepository.save(slot.getEvent());
                slot.setEvent(event);
            }
            return slotRepository.save(slot);
        }).toList();

        Organizer organizer = Organizer.builder()
                .user(user.get())
                .slots(slots)
                .calendarId(calendarId)
                .build();

        organizer = organizerRepository.save(organizer);
        if(ObjectUtils.isEmpty(organizer))
            throw new BaseException(400, "Couldn't add Organizer.");
    }

    public OrganizerDto getOrganizer(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new BaseException(404, "No such user present!");

        Optional<Organizer> optionalOrganizer = organizerRepository.findByUserId(user.get().getId());
        if(optionalOrganizer.isEmpty())
            throw new BaseException(404, "Organizer not found!");

        return OrganizerDto.builder()
                .name(user.get().getName())
                .email(user.get().getEmail())
                .slots(optionalOrganizer.get().getSlots())
                .build();
    }

    public OrganizerDto updateOrganizer(String email, List<Slot> slots) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new BaseException(404, "No such user present!");

        Optional<Organizer> optionalOrganizer = organizerRepository.findByUserId(user.get().getId());
        if(optionalOrganizer.isEmpty())
            throw new BaseException(404, "No organizer found to update");

        List<Slot> updatedSlots = slots.stream().map(slot -> {
            if(!ObjectUtils.isEmpty(slot.getEvent())){
                Event event = eventRepository.save(slot.getEvent());
                slot.setEvent(event);
            }
            return slotRepository.save(slot);
        }).toList();


        Organizer organizer = optionalOrganizer.get();
        organizer.getSlots().forEach(slotRepository::delete);
        organizer.setSlots(slots);
        organizer = organizerRepository.save(organizer);

        return OrganizerDto.builder()
                .name(user.get().getName())
                .email(user.get().getEmail())
                .slots(organizer.getSlots())
                .build();
    }

    public void deleteOrganizer(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty())
            throw new BaseException(404, "No such user present!");

        Optional<Organizer> optionalOrganizer = organizerRepository.findByUserId(user.get().getId());
        if(optionalOrganizer.isEmpty())
            throw new BaseException(404, "No Organizer to delete");

        organizerRepository.delete(optionalOrganizer.get());
    }

    public List<OrganizerDto> getAllOrganizers() {
        return organizerRepository.findAll().stream().map(
                organizer -> {
                    return OrganizerDto.builder()
                            .name(organizer.getUser().getName())
                            .email(organizer.getUser().getEmail())
                            .slots(organizer.getSlots())
                            .build();
                }
        ).toList();
    }

    public List<SlotDto> getSlotsAvailability(String email, String dateStr) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            throw new BaseException(404, "No user found!");
        }

        Optional<Organizer> optionalOrganizer = organizerRepository.findByUserId(optionalUser.get().getId());
        if(optionalOrganizer.isEmpty()){
            throw new BaseException(404, "No such organizer present");
        }

        System.out.println(dateStr + " in service " + dateStr.length());

        GoogleCalendarService googleCalendarService = new GoogleCalendarService(optionalUser.get().getToken().getAccessToken());
        List<TimePeriod> periods = googleCalendarService.getFreebusySlots(
                optionalOrganizer.get().getCalendarId(),
                dateStr);

        List<Slot> slots = optionalOrganizer.get().getSlots();

        return getSlotsDetails(periods, slots, dateStr);
    }

    public List<SlotDto> getSlotsDetails(List<TimePeriod> periods, List<Slot> slots, String dateStr){

        if(slots.isEmpty()){
            return new ArrayList<>();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS[XXX][X]");

        String weekDay = ZonedDateTime.parse(dateStr)
                .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
                .getDayOfWeek().toString();

        Set<Integer> slotStartDetails = new HashSet<>();
        periods.forEach(period -> {
            int start = ZonedDateTime.parse(period.getStart().toStringRfc3339(), formatter).getHour();
            int end = ZonedDateTime.parse(period.getEnd().toStringRfc3339(), formatter).getHour();
            for(int time = start; time < end; time++){
                slotStartDetails.add(time * 60);
            }
        });

        return slots.stream()
                .filter(slot -> slot.getDay().toString().equals(weekDay))
                .map(slot -> SlotDto.builder()
                        .day(Day.valueOf(weekDay))
                        .start(slot.getStart())
                        .end(slot.getEnd())
                        .isAvailable(!slotStartDetails.contains(slot.getStart() * 60))
                        .build()).toList();
    }

}
