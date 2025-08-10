package com.resume.api.service;

import com.resume.api.model.ResumeEntity;
import com.resume.api.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeSearchService {

    private final ResumeRepository resumeRepository;

    public ResumeSearchService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public List<ResumeEntity> searchBySkill(String skill) {
        return resumeRepository.findBySkill(skill);
    }

    public List<ResumeEntity> searchByMinExperience(int years) {
        return resumeRepository.findByMinExperience(years);
    }

    public List<ResumeEntity> searchByDegree(String degree) {
        return resumeRepository.findByDegree(degree);
    }
}
