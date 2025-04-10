package com.assign.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_details") 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {

    @Id
    // If you're using auto-increment, uncomment the following line:
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;
}