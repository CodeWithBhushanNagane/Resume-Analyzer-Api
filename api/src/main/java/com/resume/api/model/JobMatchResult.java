package com.resume.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobMatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;       // reference to job posting
    private Long resumeId;    // reference to resume

    private int score;        // 0 - 100
    @Lob
    private String missingSkills;  // JSON string array of missing skills

    @Lob
    private String summary;   // GPT-generated explanation

    private boolean aiGenerated;   // true if GPT assisted
}
