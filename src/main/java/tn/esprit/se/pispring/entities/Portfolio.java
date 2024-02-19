package tn.esprit.se.pispring.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long potfolio_id;
    private String potfolio_name;
    private String potfolio_manager;
    private String potfolio_description;
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> users;
    @ManyToOne
    Consultant consultant;
    @OneToOne
    private CustomerTracking customertracking;



}