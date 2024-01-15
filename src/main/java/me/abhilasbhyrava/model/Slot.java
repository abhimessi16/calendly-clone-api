package me.abhilasbhyrava.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int start;
    private int end;
    @Enumerated(EnumType.STRING)
    private Day day;
    @OneToOne
    @Cascade({CascadeType.PERSIST, CascadeType.REMOVE})
    private Event event;

}
