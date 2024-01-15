package me.abhilasbhyrava.repository;

import me.abhilasbhyrava.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer> {
}
