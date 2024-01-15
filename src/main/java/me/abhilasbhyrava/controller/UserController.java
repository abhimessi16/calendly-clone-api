package me.abhilasbhyrava.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import me.abhilasbhyrava.config.FrontendRequestAttributes;
import me.abhilasbhyrava.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/check")
public class UserController {

    @Autowired
    private FrontendRequestAttributes requestAttributes;

    @GetMapping("/attendee")
    public String checkAttendee(){
        requestAttributes.setHeaderValue("attendee");
        return "ok";
    }

    @GetMapping("/organizer")
    public String checkOrganizer(){
        requestAttributes.setHeaderValue("organizer");
        return "ok";
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> user(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = new UserDto();
        userDto.setName("");
        userDto.setEmail("");
        if(authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            userDto.setName(oAuth2User.getAttribute("name"));
            userDto.setEmail(oAuth2User.getAttribute("email"));
        }

        return ResponseEntity.status(userDto.getName() != null ?
                200 : 400).body(userDto);
    }

}
