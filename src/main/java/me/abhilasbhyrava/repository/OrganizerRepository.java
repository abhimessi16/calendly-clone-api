package me.abhilasbhyrava.repository;

import me.abhilasbhyrava.model.Organizer;
import me.abhilasbhyrava.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizerRepository extends JpaRepository<Organizer, Integer> {
    Optional<Organizer> findByUserId(int userId);
}
