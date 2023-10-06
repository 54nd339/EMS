package com.assignment.demo.api;

import com.assignment.demo.model.Employee;
import com.assignment.demo.service.EmployeeService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(path = "/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @PostMapping(value = "/add-employee")
    public void addEmployee(@NonNull @RequestBody Employee employee, @RequestParam("admin") UUID admin) {
        employeeService.createEmployee(employee, admin);
    }

    @GetMapping(value = "/all-employees")
    public List<Employee> getAllEmployee(@RequestParam("admin") UUID admin) {
        return employeeService.getAllEmployees(admin);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<?> getEmployee(@PathVariable("id") UUID id, @RequestParam("admin") UUID admin) {
        Employee employee = employeeService.getEmployee(id.toString(), admin)
                .orElse(null);
        if (employee != null)
            return ResponseEntity.ok(employee);
        else
            return ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "{id}")
    public void deleteEmployee(@PathVariable("id") UUID id, @RequestParam("admin") UUID admin) {
        employeeService.deleteEmployee(id.toString(), admin);
    }

    @PutMapping(path = "{id}")
    public void updateEmployee(@NonNull @RequestBody Employee newE, @PathVariable("id") UUID id, @RequestParam("admin") UUID admin) {
        employeeService.updateEmployee(id.toString(), newE, admin);
    }

    @PutMapping(path = "/assign/{id}")
    public void assignManger(@NotNull @RequestParam(value = "email") String email, @PathVariable("id") UUID id, @RequestParam("admin") UUID admin) {
        employeeService.assignManager(id.toString(), email, admin);
    }
}
