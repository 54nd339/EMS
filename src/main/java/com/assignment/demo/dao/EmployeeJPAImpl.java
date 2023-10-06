package com.assignment.demo.dao;

import com.assignment.demo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("JPA")
public class EmployeeJPAImpl implements EmployeeDao {

    @Autowired
    EmployeeRepo employeeRepo;

    private String getEmail(String str) {
        if (str.indexOf('(') == -1 || str.indexOf(')') == -1) {
            return "N/A";
        }

        return str.substring(str.indexOf('(')+1, str.indexOf(')'));
    }

    @Override
    public boolean validateAdmin(String admin) {
        return employeeRepo.findById(admin).isPresent();
    }

    @Override
    public Optional<Employee> getEmployee(String id) {
        return employeeRepo.findById(id);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    @Override
    public boolean createEmployee(Employee employee) {
        String manager = employee.getManager();
        if(manager.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
            employee.setManager(employeeRepo.getManager(manager));
        } else {
            employee.setManager("N/A");
        }

        employeeRepo.save(employee);
        return true;
    }

    @Override
    public boolean updateEmployee(String id, Employee newEmployee) {
        if(employeeRepo.findById(id).isEmpty()) {
            return false;
        } else {
            String email = getEmail(newEmployee.getManager());
            Employee employee = employeeRepo.findById(id).get();
            employee.setName(newEmployee.getName());
            employee.setAge(newEmployee.getAge());
            employee.setDob(newEmployee.getDob());
            employee.setEmail(newEmployee.getEmail());
            employee.setAddress(newEmployee.getAddress());
            employee.setDepartment(newEmployee.getDepartment());

            if(email.equals("N/A")) {
                employee.setManager("N/A");
            } else {
                employee.setManager(employeeRepo.getManager(email));
            }

            employeeRepo.save(employee);
            return true;
        }
    }

    @Override
    public boolean deleteEmployee(String id) {
        if(employeeRepo.findById(id).isEmpty()) {
            return false;
        } else {
            employeeRepo.deleteById(id);
            return true;
        }
    }

    @Override
    public boolean updateManager(String id, String mail) {
        if(employeeRepo.findById(id).isEmpty()) {
            return false;
        } else {
            Employee employee = employeeRepo.findById(id).get();
            String manager = employeeRepo.getManager(mail);
            employee.setManager(manager == null ? "N/A" : manager);
            employeeRepo.save(employee);
            return true;
        }
    }
}
