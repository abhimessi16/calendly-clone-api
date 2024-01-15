package me.abhilasbhyrava.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "calendar_id")
    private String calendarId;
    @OneToOne
    private User user;
    @OneToMany
    @Cascade({CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Slot> slots;
}
