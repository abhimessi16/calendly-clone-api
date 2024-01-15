package me.abhilasbhyrava.controller;

import lombok.RequiredArgsConstructor;
import me.abhilasbhyrava.model.dto.SlotDto;
import me.abhilasbhyrava.model.dto.UserDto;
import me.abhilasbhyrava.model.request.AvailabilityRequest;
import me.abhilasbhyrava.service.GoogleCalendarService;
import me.abhilasbhyrava.service.OrganizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final OrganizerService organizerService;

    @GetMapping()
    public String home(){
        return "home";
    }

    @GetMapping("/{username}/availability")
    public List<SlotDto> getSlots(@PathVariable("username") String email, @RequestParam String dateStr){
        String date = "2024-01-10T18:30:00.000Z";
        System.out.println(date + " " + dateStr);
        List<SlotDto> slotDtoList = organizerService.getSlotsAvailability(email, dateStr);
        return slotDtoList;
    }

}
