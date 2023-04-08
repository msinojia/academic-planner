package com.group13.academicplannerbackend.model;

<<<<<<< Updated upstream
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
=======
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
>>>>>>> Stashed changes
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class UserMeta implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private boolean verified;
    private String profileStatus;
}
