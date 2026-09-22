package com.example.employee_app.service;



import com.example.employee_app.dto.AuthResponse;
import com.example.employee_app.dto.LoginRequest;
import com.example.employee_app.model.Employee;
import com.example.employee_app.model.Role;
import com.example.employee_app.repository.EmployeeRepository;
import com.example.employee_app.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }

        Employee employee = employeeRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(employee.getUsername());
        String message = buildWelcomeMessage(employee);

        return new AuthResponse(token, message);
    }

    private String buildWelcomeMessage(Employee employee) {
        if (employee.getRoles().contains(Role.ADMIN)) {
            return "Welcome admin " + employee.getUsername();
        }
        return "Welcome user " + employee.getUsername();
    }
}