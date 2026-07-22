package com.jwalit.inventory_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String drivingLicenseNumber;
    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    private Role role;
}
