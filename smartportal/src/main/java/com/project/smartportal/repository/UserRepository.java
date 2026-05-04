package com.project.smartportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.smartportal.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
