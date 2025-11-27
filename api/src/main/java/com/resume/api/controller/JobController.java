package com.resume.api.controller;

import com.resume.api.model.JobMatchResult;
import com.resume.api.model.ResumeEntity;
import com.resume.api.repository.JobMatchResultRepository;
import com.resume.api.repository.ResumeRepository;
import com.resume.api.service.JobMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    JobMatchService jobMatchService;

    @Autowired
    ResumeRepository resumeRepository;

    /**
     * POST /api/job/match
     * Accepts raw job description text, returns list of candidates with match %.
     */
//    @PostMapping("/match")
//    public ResponseEntity<?> matchJob(@RequestBody String jobDescription) {
//        return ResponseEntity.ok(jobMatchService.matchJob(jobDescription));
//    }

    @PostMapping("/match/{jobId}/{resumeId}")
    public ResponseEntity<JobMatchResult> matchJob(
            @PathVariable Long jobId,
            @PathVariable Long resumeId,
            @RequestBody String jobJson) {

        ResumeEntity resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        JobMatchResult result = jobMatchService.matchAndSave(jobId, jobJson, resume);
        return ResponseEntity.ok(result);
    }

}