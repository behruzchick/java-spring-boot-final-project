package com.example.demo.validation;

import com.example.demo.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class Validation {
    public String validationEmployee(Employee employee , Employee current_employee) {
        if (current_employee.getUsername().equals(employee.getUsername())) {
            return "Username is already taken";
        }
        if (current_employee.getEmail().equals(employee.getEmail())) {
            return "Email is already taken";
        }
        if (current_employee.getPassportData().equals(employee.getPassportData())) {
            return "Passport data is already taken";
        }
        if (current_employee.getPhone().equals(employee.getPhone())) {
            return "Phone is already taken";
        }
        return null;
    }
}
