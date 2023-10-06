package com.assignment.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Employee {
    @JsonIgnore
    @Id
    private String id;

    private String name;
    private int age;
    private Date dob;
    private String email;
    private String address, department;
    private String manager;
}
