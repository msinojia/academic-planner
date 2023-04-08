package com.group13.academicplannerbackend.repository;

import com.group13.academicplannerbackend.model.FixedEvent;
import com.group13.academicplannerbackend.model.VariableEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VariableEventRepository extends JpaRepository<VariableEvent, Long> {
    VariableEvent findById(long id);
    List<VariableEvent> findAllByUserEmail(String email);

    @Query("SELECT e FROM VariableEvent e WHERE ((e.schedule.scheduledDateTime BETWEEN :firstDate AND :secondDate)) AND e.user.email = :email")
    List<VariableEvent> findAllByStartDateOrEndDateBetweenDates(@Param("firstDate") LocalDateTime firstDate, @Param("secondDate") LocalDateTime secondDate, @Param("email") String email);

//    @Query(value = "SELECT e.* FROM variable_event e JOIN schedule s ON e.id = s.variable_event_id WHERE ((s.scheduled_date_time BETWEEN :firstDate AND :secondDate) OR (DATE_ADD(s.scheduled_date_time, INTERVAL e.duration SECOND) BETWEEN :firstDate AND :secondDate)) AND e.user_id IN (SELECT u.id FROM user u WHERE u.email = :email)", nativeQuery = true)
//    List<VariableEvent> findAllByStartDateOrEndDateBetweenDates(@Param("firstDate") LocalDate firstDate, @Param("secondDate") LocalDate secondDate, @Param("email") String email);


}
