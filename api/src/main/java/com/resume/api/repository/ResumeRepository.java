package com.resume.api.repository;

import com.resume.api.model.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {

    // Search by skill in aiAnalysis JSONB
    @Query(value = "SELECT * FROM resumes r " +
            "WHERE LOWER(r.ai_analysis::text) LIKE LOWER(CONCAT('%', :skill, '%'))",
            nativeQuery = true)
    List<ResumeEntity> findBySkill(@Param("skill") String skill);

    // Search by minimum years of experience (assuming it's stored in aiAnalysis JSON)
    @Query(value = "SELECT * FROM resumes r " +
            "WHERE (r.ai_analysis->'experience')::text LIKE CONCAT('%', :minYears, '%')",
            nativeQuery = true)
    List<ResumeEntity> findByMinExperience(int minYears);

    // Search by degree in education array
    @Query(value = "SELECT * FROM resumes r " +
            "WHERE LOWER(r.ai_analysis::text) LIKE LOWER(CONCAT('%', :degree, '%'))",
            nativeQuery = true)
    List<ResumeEntity> findByDegree(String degree);
}
