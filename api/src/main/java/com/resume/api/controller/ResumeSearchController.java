package com.resume.api.controller;

import com.resume.api.model.ResumeEntity;
import com.resume.api.service.ResumeSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resume/search")
public class ResumeSearchController {

    @Autowired
    ResumeSearchService searchService;

    public ResumeSearchController(ResumeSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/skill")
    public List<ResumeEntity> searchBySkill(@RequestParam String skill) {
        return searchService.searchBySkill(skill);
    }

    @GetMapping("/experience")
    public List<ResumeEntity> searchByMinExperience(@RequestParam int minYears) {
        return searchService.searchByMinExperience(minYears);
    }

    @GetMapping("/education")
    public List<ResumeEntity> searchByDegree(@RequestParam String degree) {
        return searchService.searchByDegree(degree);
    }
}
