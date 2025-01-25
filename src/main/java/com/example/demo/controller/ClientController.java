package com.example.demo.controller;

import com.example.demo.model.Archive;
import com.example.demo.model.Employee;
import com.example.demo.model.User;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ClientController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;

    public ClientController(
            UserService userService,
            AuthenticationManager authenticationManager,
            TokenProvider tokenProvider, EmployeeService employeeService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.employeeService = employeeService;
        this.passwordEncoder = passwordEncoder;
    }


    @RequestMapping(value = "/client/addClient" , method = RequestMethod.POST)
    public ResponseEntity<?> addClient(@RequestBody User user, HttpServletRequest httpServletRequest) {

        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
        }
        token = token.substring("Bearer".length());

        String username;

        try{
            username = tokenProvider.getUserNameFromToken(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Token is invalid");
        }

        Employee employee = employeeService.findByUsername(username);
        if (employee == null) {
            return ResponseEntity.badRequest().body("Employee not found");
        }
        user.setRegistered_by(username.toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        employee.getAdded_clients().add(user);
        userService.saveUser(user);
        return ResponseEntity.ok().body("Employee added successfully" + user);
    }

    @GetMapping(value = "/client/getAllClients")
    private ResponseEntity<?> getAllClients() {
        List<User> user = userService.getAllUsers();
        return ResponseEntity.ok().body(user);
    }


    @RequestMapping(value = "/client/getClient/{id}" , method = RequestMethod.GET)
    public ResponseEntity<?> getClient(@PathVariable long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok().body(user);
    }
    @RequestMapping(value = "/client/updateClient/{id}" , method = RequestMethod.PATCH)
    public ResponseEntity<?> updateClient(@PathVariable long id, @RequestBody User user) {
        User currentUser = userService.findById(id);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setEdited_at(new Date());
        currentUser.setPassportData(user.getPassportData());

        User updated_user = userService.updateUser(currentUser);

        return ResponseEntity.ok().body(updated_user);
    }

    @RequestMapping(value = "/client/addArchive/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> addArchive(@PathVariable long id) {
        User current_user = userService.findById(id);
        if (current_user == null) {
            return ResponseEntity.notFound().build();
        }
        current_user.setArchived(true);
        User urchived_user = userService.addUserToArchive(current_user);
        return ResponseEntity.ok().body(urchived_user);
    }

}
