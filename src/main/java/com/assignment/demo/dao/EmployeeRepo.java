package com.assignment.demo.dao;

import com.assignment.demo.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepo extends JpaRepository<Employee, String> {

    @Query(value = "SELECT CONCAT(name, ' (', email, ') ') FROM employee WHERE email = ?1", nativeQuery = true)
    String getManager(String email);
}
