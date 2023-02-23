package com.group13.academicplannerbackend.repository;

import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.model.UserMeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMetaRepository extends JpaRepository<UserMeta, Long> {
}
