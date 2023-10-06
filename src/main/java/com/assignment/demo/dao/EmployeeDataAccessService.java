package com.assignment.demo.dao;

import com.assignment.demo.model.Employee;
import com.assignment.demo.model.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository("JDBC")
public class EmployeeDataAccessService implements EmployeeDao {

    JdbcTemplate jdbcTemplate;

    private final String SQL_FIND_PERSON = "select * from employee where id = ?";
	private final String SQL_DELETE_PERSON = "delete from employee where id = ?";
	private final String SQL_UPDATE_PERSON = "update employee set name = ?, age = ?, dob = ?, email = ?, address = ?," +
            "department = ?, manager = (select concat(name, ' (', email, ') ') from employee where email = ?) where id = ?";
	private final String SQL_GET_ALL = "select * from employee";
	private final String SQL_INSERT_PERSON = "insert into employee(id, name, age, dob, email, address, department, manager)" +
            "values(?,?,?,?,?,?,?,(select concat(name, ' (', email, ') ') from employee where email = ?))";
    private final String SQL_UPDATE_MANAGER = "update employee set manager = (select concat(name, ' (', email, ') ') from employee where email = ?) where id = ?";

    private String getEmail(String str) {
        if (str.indexOf('(') == -1 || str.indexOf(')') == -1) {
            return "N/A";
        }
        
        return str.substring(str.indexOf('(')+1, str.indexOf(')'));
    }

    @Autowired
    public EmployeeDataAccessService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public boolean validateAdmin(String admin) {
        return getEmployee(admin).isPresent();
    }

    @Override
    public boolean createEmployee(Employee employee) {
        return jdbcTemplate.update(SQL_INSERT_PERSON, employee.getId(), employee.getName(), employee.getAge(), employee.getDob(),
                employee.getEmail(), employee.getAddress(), employee.getDepartment(), employee.getManager()) > 0;
    }

    @Override
    public boolean updateEmployee(String id, Employee employee) {
        return jdbcTemplate.update(SQL_UPDATE_PERSON, employee.getName(), employee.getAge(), employee.getDob(), employee.getEmail(),
                employee.getAddress(), employee.getDepartment(), getEmail(employee.getManager()), id) > 0;
    }

    @Override
    public boolean deleteEmployee(String id) {
        return jdbcTemplate.update(SQL_DELETE_PERSON, id) > 0;
    }

    @Override
    public boolean updateManager(String id, String mail) {
        return jdbcTemplate.update(SQL_UPDATE_MANAGER, mail, id) > 0;
    }

    @Override
    public Optional<Employee> getEmployee(String id) {
        try {
            Employee employee = jdbcTemplate.queryForObject(SQL_FIND_PERSON, new EmployeeMapper(), id);
            return Optional.ofNullable(employee);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        return jdbcTemplate.query(SQL_GET_ALL, new EmployeeMapper());
    }
}
