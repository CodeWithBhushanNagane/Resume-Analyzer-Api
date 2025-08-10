package com.resume.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.resume.api.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
