package com.resume.api.controller;

import com.resume.api.service.JobMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    JobMatchService jobMatchService;

    /**
     * POST /api/job/match
     * Accepts raw job description text, returns list of candidates with match %.
     */
    @PostMapping("/match")
    public ResponseEntity<?> matchJob(@RequestBody String jobDescription) {
        return ResponseEntity.ok(jobMatchService.matchJob(jobDescription));
    }
}