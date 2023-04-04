package com.group13.academicplannerbackend.repository;

import com.group13.academicplannerbackend.model.VariableEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariableEventRepository extends JpaRepository<VariableEvent, Long> {
    VariableEvent findById(long id);
    List<VariableEvent> findAllByUserEmail(String email);
}
