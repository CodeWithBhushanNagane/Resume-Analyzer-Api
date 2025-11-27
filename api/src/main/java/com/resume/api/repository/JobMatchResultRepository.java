package com.resume.api.repository;

import com.resume.api.model.JobMatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobMatchResultRepository extends JpaRepository<JobMatchResult, Long> {
    List<JobMatchResult> findByJobId(Long jobId);
    List<JobMatchResult> findByResumeId(Long resumeId);
}
