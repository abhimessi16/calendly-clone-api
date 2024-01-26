# Calendly Clone App

A web app that will help you to connect with a person and to let another person connect with you.
Any user can register as an Organizer or as an Attendee.
Organizers will connect with an individual attendee. The Organizer will share the times during which he/she will be available. These time slots will be visible to the attendees to book the time of the any organizer.
Attendees can browse through a list of Organizers and choose to book their time based on the availability of slots.
Whenever an Attendee books the time of an Organizer, the Attendee will receive a Google Meet invite from the Organizer.

# Calendly Clone API
•	Using Spring Boot, MySQL, Google Calendar API
# Database schemas
 ![image](https://github.com/abhimessi16/calendly-clone-api/assets/91337858/821bafeb-0286-44a5-a968-31c89317032a)
 ![image](https://github.com/abhimessi16/calendly-clone-api/assets/91337858/d5398e5d-94af-4060-ace6-f8861e9b755a)
 ![image](https://github.com/abhimessi16/calendly-clone-api/assets/91337858/026a561c-e3c8-4396-af29-d9f4b0fe74ce)
 ![image](https://github.com/abhimessi16/calendly-clone-api/assets/91337858/9be2f2ed-00fc-46ec-843a-9000fec5fc07)
 ![image](https://github.com/abhimessi16/calendly-clone-api/assets/91337858/2ce2e765-b64c-4d38-829d-eab1c41c2044)
 ![image](https://github.com/abhimessi16/calendly-clone-api/assets/91337858/5deee297-8f81-49f6-b0ce-ddfb43bcff49)
 ![image](https://github.com/abhimessi16/calendly-clone-api/assets/91337858/2c66f8a6-7a75-4913-8e40-2d40dc4b73cb)
 ![image](https://github.com/abhimessi16/calendly-clone-api/assets/91337858/d03878ac-4aa6-4f59-b94f-31b557ba2344)
 ![image](https://github.com/abhimessi16/calendly-clone-api/assets/91337858/50092112-9da7-4fad-9fbc-92dd7bc036e0)
## Server flow
1.	The server will store the tokens received while logging in using OAuth2.
2.	Uses these tokens to add events to user’s Calendar on their behalf.
3.	Creates a new Calendar called ‘Calendly Clone’ in the user’s Google Calendar. This is done, so as to not use the primary Calendar of the user for the purpose of booking Calendly Clone events. This will keep the user’s events which are related to Calendly Clone separate.
4.	Retrieves the freebusy details of the user using the Google Calendar API, so as to book slots only when the user is available.
5.	Books slots in the user’s Google Calendar, uses the Google Calendar API.
6.	Stores events in the database also, so that the Google Calendar API is only called when creating events. (currently we use the API to get freebusy details also)

## Endpoints
•	POST – /api/v1/organizer/add

•	POST – /api/v1/attendee/add
 
•	GET – /api/v1/organizer/{username}/availability
 
•	POST – /api/v1/attendee/{username_attendee}/add-event/{username_organizer}
 
