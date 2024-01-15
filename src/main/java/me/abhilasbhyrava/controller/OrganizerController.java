package me.abhilasbhyrava.controller;

import lombok.RequiredArgsConstructor;
import me.abhilasbhyrava.model.dto.OrganizerDto;
import me.abhilasbhyrava.model.Slot;
import me.abhilasbhyrava.model.dto.SlotDto;
import me.abhilasbhyrava.model.request.AvailabilityRequest;
import me.abhilasbhyrava.service.OrganizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizer")
@RequiredArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;

    @PostMapping("/add")
    public ResponseEntity<OrganizerDto> addOrganizer(@RequestBody OrganizerDto organizerDto){

        System.out.println(organizerDto);
        organizerService.add(organizerDto);
        return ResponseEntity.ok(organizerDto);
    }

    @GetMapping("/{username}")
    public ResponseEntity<OrganizerDto> getOrganizer(@PathVariable("username") String email){

        OrganizerDto organizerDto = organizerService.getOrganizer(email);
        return ResponseEntity.ok(organizerDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrganizerDto>> getAllOrganizer(){

        List<OrganizerDto> organizers = organizerService.getAllOrganizers();
        return ResponseEntity.ok(organizers);
    }

    @PutMapping("/{username}")
    public ResponseEntity<OrganizerDto> updateOrganizer(@PathVariable("username") String email, @RequestBody List<Slot> slots){

        OrganizerDto organizerDto = organizerService.updateOrganizer(email, slots);
        return ResponseEntity.ok(organizerDto);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> updateOrganizer(@PathVariable("username") String email){

        organizerService.deleteOrganizer(email);
        return ResponseEntity.ok("Organizer with email " + email + " deleted!");
    }

    @GetMapping("/{username}/availability")
    public ResponseEntity<List<SlotDto>> getSlotsAndAvailability(@PathVariable("username") String email,
                                                                 @RequestParam String dateStr){

        return ResponseEntity.ok(organizerService.getSlotsAvailability(email, dateStr));
    }

}
