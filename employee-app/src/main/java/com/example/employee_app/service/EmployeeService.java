package com.example.employee_app.service;

import com.example.employee_app.dto.EmployeeResponse;
import com.example.employee_app.dto.EmployeeUpdateRequest;
import com.example.employee_app.dto.RegisterEmployeeRequest;
import com.example.employee_app.model.Employee;
import com.example.employee_app.model.Role;
import com.example.employee_app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public EmployeeResponse addEmployee(RegisterEmployeeRequest request) {
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        Employee employee = new Employee();
        employee.setUsername(request.getUsername());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setFullName(request.getFullName());
        employee.setPosition(request.getPosition());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER); // ajouter role USER par defaut
        employee.setRoles(roles);

        employeeRepository.save(employee);
        return toResponse(employee);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setFullName(request.getFullName());
        employee.setPosition(request.getPosition());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());

        employeeRepository.save(employee);
        return toResponse(employee);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public String promoteToAdmin(String username) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (employee.getRoles().contains(Role.ADMIN)) {
            return employee.getUsername() + " is already an admin";
        }

        employee.getRoles().add(Role.ADMIN);
        employeeRepository.save(employee);

        return employee.getUsername() + " is now an admin";
    }

    private EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setUsername(employee.getUsername());
        response.setFullName(employee.getFullName());
        response.setPosition(employee.getPosition());
        response.setDepartment(employee.getDepartment());
        response.setSalary(employee.getSalary());
        response.setRoles(employee.getRoles());
        return response;
    }
}