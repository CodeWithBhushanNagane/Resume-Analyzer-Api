package com.resume.api.dto;

import java.util.List;

public class ResumeAnalysis {
    public String name;
    public String email;
    public String phone;
    public List<String> skills;
    public List<Experience> experience;
    public List<Education> education;

    public static class Experience {
        public String company;
        public String role;
        public int years;
    }

    public static class Education {
        public String degree;
        public String university;
        public int year;
    }
}
