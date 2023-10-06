package com.assignment.demo.service;

import com.assignment.demo.dao.EmployeeDao;
import com.assignment.demo.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@EnableCaching
public class EmployeeService {
    @Autowired
    @Qualifier("JPA")
    private EmployeeDao employeeDao;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String topic;

    private Map<String, String> validateAdmin(String admin, String action) {
        Map<String, String> map = new HashMap<>();
        map.put("requested_by", admin);
        map.put("requested_at", new Timestamp(System.currentTimeMillis()).toString());
        map.put("action", action);

        if(!employeeDao.validateAdmin(admin)) {
            map.put("status", "invalid admin");
            log.error("Invalid Admin {}", admin);
            kafkaTemplate.send(topic, map.toString());
            return map;
        }

        map.put("status", "success");
        return map;
    }

    public void createEmployee(Employee employee, UUID admin) {
        employee.setId(UUID.randomUUID().toString());
        String[] name = employee.getName().split(" ");
        employee.setEmail(name[0].toLowerCase() + (name.length < 2 ? "" : "."+name[name.length -1].toLowerCase()) + "@jungleegames.com");
        
        Map<String, String> map = validateAdmin(admin.toString(), "create");
        if(map.get("status").equals("invalid admin")) {
            return;
        }

        if(employeeDao.createEmployee(employee)) {
            map.put("id", employee.getId());
            log.info("Created Employee with id {} by {}", employee.getId(), admin);
        } else {
            map.put("status", "failure");
            log.error("Error Creating Employee {} by {}", employee.getId(), admin);
        }
    
        kafkaTemplate.send(topic, map.toString());
    }

    @Cacheable("employee")
    public List<Employee> getAllEmployees(UUID admin) {
        Map<String, String> map = validateAdmin(admin.toString(), "getAll");
        if(map.get("status").equals("invalid admin")) {
            return new ArrayList<>();
        }
        
        List<Employee> employees = employeeDao.getAllEmployees();
        if (!employees.isEmpty()) {
            log.info("Found {} Employees", employees.size());
        } else {
            map.put("status", "failure");
            log.error("No Employees found");
        }

        kafkaTemplate.send(topic, map.toString());
        return employees;
    }

    @Cacheable("Employee")
    public Optional<Employee> getEmployee(String id, UUID admin) {
        Map<String, String> map = validateAdmin(admin.toString(), "get_"+id);
        if(map.get("status").equals("invalid admin")) {
            return Optional.empty();
        }
        
        Optional<Employee> employee = employeeDao.getEmployee(id);
        if (employee.isPresent()) {
            log.info("Found Employee with id {}", id);
        } else {
            map.put("status", "failure");
            log.error("No Employee found with id {}", id);
        }

        kafkaTemplate.send(topic, map.toString());
        return employee;
    }

    @CacheEvict(value = "employee", key = "#id")
    public void deleteEmployee(String id, UUID admin) {
        Map<String, String> map = validateAdmin(admin.toString(), "delete_"+id);
        if(map.get("status").equals("invalid admin")) {
            return;
        }
        
        if(employeeDao.deleteEmployee(id)) {
            log.info("Deleted Employee with id {}", id);
        } else {
            map.put("status", "failure");
            log.error("Error deleting Employee {}", id);
        }

        kafkaTemplate.send(topic, map.toString());
    }

    @CacheEvict(value = "employee", key = "#id")
    public void updateEmployee(String id, Employee newE, UUID admin) {
        Map<String, String> map = validateAdmin(admin.toString(), "update_"+id);
        if(map.get("status").equals("invalid admin")) {
            return;
        }
        
        if(employeeDao.updateEmployee(id, newE)) {
            log.info("Updated Employee with id {}", id);
        } else {
            map.put("status", "failure");
            log.error("Error updating Employee {}", id);
        }

        kafkaTemplate.send(topic, map.toString());
    }

    @CacheEvict(value = "employee", key = "#id")
    public void assignManager(String id, String email, UUID admin) {
        Map<String, String> map = validateAdmin(admin.toString(), "assign_"+id+"_"+email);
        if(map.get("status").equals("invalid admin")) {
            return;
        }
        
        if(employeeDao.updateManager(id, email)) {
            map.put("status", "success");
            log.info("Assigned Manager {} to Employee {}", email, id);
        } else {
            map.put("status", "failure");
            log.error("Error assigning Manager {} to Employee {}", email, id);
        }

        kafkaTemplate.send(topic, map.toString());
    }
}
