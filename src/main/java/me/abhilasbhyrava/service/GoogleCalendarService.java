package me.abhilasbhyrava.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import me.abhilasbhyrava.model.Attendee;
import me.abhilasbhyrava.model.exception.BaseException;
import me.abhilasbhyrava.model.request.AvailabilityRequest;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Calendly Clone";

    private Calendar calendar;

    public GoogleCalendarService(String accessToken){

        HttpRequestInitializer requestInitializer = request -> request.getHeaders()
                .setAuthorization("Bearer " + accessToken);

        try{
            this.calendar = new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    null
            )
                    .setHttpRequestInitializer(requestInitializer)

                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String checkCalendlyCloneCalendarPresent() {
        try {
            CalendarList calendarList = this.calendar.calendarList().list().execute();
            Optional<CalendarListEntry> calendlyCloneCalendar =
                    calendarList.getItems().stream().filter(cal -> cal.getSummary().equals(APPLICATION_NAME)).findFirst();

            String calendarId;

            if (calendlyCloneCalendar.isEmpty()) {
//                ConferenceProperties conferenceProperties = new ConferenceProperties();
//                CreateConferenceRequest conferenceRequest = new CreateConferenceRequest();
//                ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey();
//                conferenceRequest.setConferenceSolutionKey(conferenceSolutionKey);
//                conferenceSolutionKey.setType("hangoutsMeet");
//                conferenceProperties.setAllowedConferenceSolutionTypes(List.of("hangoutsMeet"));
//                ConferenceData conferenceData = new ConferenceData();
////                conferenceData.setConferenceSolution();
//                ConferenceSolution conferenceSolution = new ConferenceSolution();
//                conferenceRequest.

                com.google.api.services.calendar.model.Calendar cal = new com.google.api.services.calendar.model.Calendar();
                cal
                        .setSummary("Calendly Clone")
                        .setDescription("For Calendly Clone app");
//                        .setConferenceProperties(conferenceProperties);

                com.google.api.services.calendar.model.Calendar c = this.calendar.calendars().insert(cal).execute();
                calendarId = c.getId();
            } else {
                calendarId = calendlyCloneCalendar.get().getId();
            }

            //        AclRule aclRule = new AclRule();
            //        AclRule.Scope scope = new AclRule.Scope();
            //        scope.setType("user").setValue("piabhi18@gmail.com");
            //        aclRule.setScope(scope).setRole("writer");

            //        this.calendar.acl().insert(calendarId, aclRule).execute();

            return calendarId;
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        throw new BaseException(400, "Cannot connect to Google Calendar service.");
    }

    public void addEventOrganizerToAttendee(String calendarId, Attendee attendee, me.abhilasbhyrava.model.Event event) {

        try {

            EventAttendee eventAttendee = new EventAttendee()
                    .setDisplayName(attendee.getUser().getName())
                    .setEmail(attendee.getUser().getEmail());

            ConferenceData conferenceData = new ConferenceData();
            CreateConferenceRequest conferenceRequest = new CreateConferenceRequest();
            ConferenceSolutionKey solutionKey = new ConferenceSolutionKey();
            solutionKey.setType("hangoutsMeet");
            conferenceRequest.setConferenceSolutionKey(solutionKey);
            conferenceRequest.setRequestId("abcd-abcd-abcd");
            conferenceData.setCreateRequest(conferenceRequest);


            Event googleEvent = new Event()
                    .setSummary(event.getEventName())
                    .setDescription(event.getEventDescription())
                    .setAttendees(List.of(eventAttendee))
                    .setConferenceData(conferenceData);

            googleEvent.setStart(getEventDateTime(event.getStart()));
            googleEvent.setEnd(getEventDateTime(event.getEnd()));

            this.calendar.events().insert(calendarId, googleEvent)
                    .setConferenceDataVersion(1)
                    .setSendNotifications(true)
                    .execute();

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public EventDateTime getEventDateTime(String dateStr){

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[X]");

        DateTime dateTime = new DateTime(zonedDateTime.format(formatter));

        return new EventDateTime()
                .setDateTime(dateTime)
                .setTimeZone("Asia/Kolkata");
    }

    public List<TimePeriod> getFreebusySlots(String calendarId, String dateStr) {

        try{
            FreeBusyRequest freeBusyRequest = new FreeBusyRequest();

            System.out.println(dateStr + " in method - 1");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            ZonedDateTime dateTime = ZonedDateTime.parse(dateStr);
            dateTime = dateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
            System.out.println(dateTime.format(formatter) + " in method - 2");
            freeBusyRequest.setTimeMin(new DateTime(dateTime.format(formatter) + 'Z'));
            dateTime = dateTime.plusDays(1);
            freeBusyRequest.setTimeMax(new DateTime(dateTime.format(formatter) + 'Z'));
            freeBusyRequest.setTimeZone("Asia/Kolkata");
            FreeBusyRequestItem requestItem = new FreeBusyRequestItem();
            requestItem.setId(calendarId);
            freeBusyRequest.setItems(List.of(requestItem));

            FreeBusyResponse freeBusyResponse = this.calendar.freebusy().query(freeBusyRequest).execute();
            FreeBusyCalendar freeBusyCalendar = freeBusyResponse.getCalendars().get(calendarId);

            return freeBusyCalendar.getBusy();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

}
