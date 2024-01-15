package me.abhilasbhyrava.repository;

import me.abhilasbhyrava.model.Attendee;
import me.abhilasbhyrava.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendeeRepository extends JpaRepository<Attendee, Integer> {
    Optional<Attendee> findByUserId(int userId);
}
