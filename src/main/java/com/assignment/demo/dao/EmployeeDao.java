package com.assignment.demo.dao;

import com.assignment.demo.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeDao {
    boolean validateAdmin(String admin);

    Optional<Employee> getEmployee(String id);

    List<Employee> getAllEmployees();
    
    boolean createEmployee(Employee employee);
    
    boolean updateEmployee(String id, Employee employee);
    
    boolean deleteEmployee(String id);

    boolean updateManager(String id, String mail);
}
