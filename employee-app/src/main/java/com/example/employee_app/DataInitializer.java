package com.example.employee_app;

import com.example.employee_app.model.Employee;
import com.example.employee_app.model.Role;
import com.example.employee_app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!employeeRepository.existsByUsername("admin")) {
            Employee admin = new Employee();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Default Admin");
            admin.setPosition("System Administrator");
            admin.setDepartment("IT");
            admin.setSalary(0.0);

            Set<Role> roles = new HashSet<>();
            roles.add(Role.USER);
            roles.add(Role.ADMIN);
            admin.setRoles(roles);

            employeeRepository.save(admin);
            System.out.println("admin created -> username: admin | password: admin123");
        }
    }
}
