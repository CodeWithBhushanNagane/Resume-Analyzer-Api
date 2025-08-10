package com.resume.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resume_experience")
@Data
public class ExperienceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String role;
    private int years;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    @JsonIgnore
    private ResumeEntity resume;

    // Getters & Setters
}
