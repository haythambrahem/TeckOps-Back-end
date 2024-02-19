package tn.esprit.se.pispring.entities;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payroll {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long payroll_id;

    private String seniority;

    private String category;
    private Float base_salary;
    private Integer year;
    private String month;
    private Float brut_salary;
    private Float net_salary;
    private Integer work_hours_number;
    private String bank_name;
    private Long account_number;
    @Enumerated(EnumType.STRING)
    private PaymentMehod payment_method;
    @ManyToOne
    User user;


}