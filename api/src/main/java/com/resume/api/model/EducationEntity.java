package com.resume.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resume_education")
@Data
public class EducationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String degree;
    private String university;
    private int year;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    @JsonIgnore
    private ResumeEntity resume;

    // Getters & Setters
}
