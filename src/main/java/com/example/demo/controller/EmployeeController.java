package com.example.demo.controller;

import com.example.demo.dtos.LoginUserDto;
import com.example.demo.model.Employee;
import com.example.demo.model.SaleAd;
import com.example.demo.model.User;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.EmployeeService;
import com.example.demo.service.UserService;
import com.example.demo.validation.Validation;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final Validation validation;
    private final UserService userService;

    public EmployeeController(
            EmployeeService employeeService, AuthenticationManagerBuilder authenticationManagerBuilder, AuthenticationManager authenticationManager,
            TokenProvider tokenProvider, Validation validation, UserService userService
    ) {
        this.employeeService = employeeService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.validation = validation;
        this.userService = userService;
    }

    @RequestMapping(value = "/employee/addEmployee" , method = RequestMethod.POST)
    public ResponseEntity<?> signup(@RequestBody Employee employee) {
        if (employee.getUsername().isEmpty() || employee.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Please enter username/password");
        }
        if (employee.getAddress().isEmpty() || employee.getPhone().isEmpty() || employee.getEmail().isEmpty() || employee.getLastName().isEmpty() || employee.getFirstName().isEmpty()) {
            return ResponseEntity.badRequest().body("Please enter rows!");
        }
        Employee saved = employeeService.save(employee);
        return ResponseEntity.ok(saved);
    }

    @RequestMapping(value = "/blabla" , method = RequestMethod.GET)
    public ResponseEntity blabla() {
        return ResponseEntity.ok("blabla");
    }

    @RequestMapping(value = "/employee/getAllEmployees" , method = RequestMethod.GET)
    public ResponseEntity<Collection<Employee>> getAllEmployees() {
        Collection<Employee> employees = employeeService.findAll();
        return ResponseEntity.ok(employees);
    }


    @RequestMapping(value = "/employee/findEmployee/{id}" , method = RequestMethod.GET)
    public ResponseEntity<Employee> findEmployeeById(@PathVariable long id) {
        Employee employee = employeeService.findById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employee);
    }

    @RequestMapping(value = "/employee/deleteEmployee/{id}" , method = RequestMethod.DELETE)
    public ResponseEntity deleteEmployee(@PathVariable long id, HttpServletRequest httpServletRequest) {

        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or invalid");
        }

        token = token.substring("Bearer ".length());

        String username;

        try {
            username = tokenProvider.getUserNameFromToken(token);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Employee current_employee = employeeService.findByUsername(username);

        if (current_employee == null) {
            return ResponseEntity.notFound().build();
        }
        if (current_employee.getId().equals(id)) {
            return ResponseEntity.badRequest().body("You cannot delete yourself employee");
        }
        employeeService.deleteById(id);
        return ResponseEntity.ok("User with id: " + id + "successfully deleted");
    }

    @PostMapping("/employee/signIn")
    public ResponseEntity<JWTToken> signin(@RequestBody LoginUserDto loginUserDto) {
        System.out.println("loginUserDto = " + loginUserDto.getUsername() + " " + loginUserDto.getPassword());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUserDto.getUsername(),
                loginUserDto.getPassword()
        );
        System.out.println("authenticationToken: " + authenticationToken);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication , loginUserDto.isRememberMe());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        return ResponseEntity.ok(new JWTToken(jwt));
    }

    @RequestMapping(value = "/employee/editEmployee/{id}" , method = RequestMethod.PUT)
    public ResponseEntity<?> editEmployee(@PathVariable long id , @RequestBody Employee employee) {
        Employee current_employee = employeeService.findById(id);

        if (current_employee == null) {
        return ResponseEntity.notFound().build();
        }
        String validationError = validation.validationEmployee(employee, current_employee);

        if (validationError != null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(validationError));
        }

        current_employee.setUsername(employee.getUsername());
        current_employee.setEmail(employee.getEmail());
        current_employee.setPassportData(employee.getPassportData());
        current_employee.setPhone(employee.getPhone());
        current_employee.setRoles(employee.getRoles());
        current_employee.setFirstName(employee.getFirstName());
        current_employee.setLastName(employee.getLastName());
        current_employee.setAddress(employee.getAddress());
        current_employee.setSalary(employee.getSalary());
        current_employee.setEdited_at(new Date());

        Employee saved = employeeService.changeEmployee(current_employee);
        return ResponseEntity.ok(saved);
    }

    @RequestMapping(value = "/ad/createAd" , method = RequestMethod.POST)
    public ResponseEntity<?> createAd(@RequestBody SaleAd saleAd , HttpServletRequest httpServletRequest) {

        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is missing or invalid");
        }
        token = token.substring("Bearer ".length());
        String username;
        try {
            username = tokenProvider.getUserNameFromToken(token);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Employee current_employee = employeeService.findByUsername(username);
        if (current_employee == null) {
            return ResponseEntity.notFound().build();
        }
        saleAd.setAdded_by(current_employee.getUsername().toUpperCase());

        employeeService.createSaleAd(saleAd);
        return ResponseEntity.ok(saleAd);

    }


    @RequestMapping(value = "/ad/updateAd/{id}" , method = RequestMethod.PUT)
    public ResponseEntity<?> updateAd(@PathVariable long id , @RequestBody SaleAd saleAd) {
        SaleAd current_ad = employeeService.findSaleAdById(id);
        if (current_ad == null) {
            return ResponseEntity.notFound().build();
        }

        current_ad.setAdded_by(saleAd.getAdded_by());
        current_ad.setAd_type(saleAd.getAd_type());
        current_ad.setTitle(saleAd.getTitle());
        current_ad.setSales_price(saleAd.getSales_price());
        employeeService.updateSaleAd(current_ad);

        return ResponseEntity.ok(current_ad);
    }

    @PutMapping(value = "/ad/stopAd/{id}")
    public ResponseEntity<?> stopAd(@PathVariable long id) {
        SaleAd current_ad = employeeService.findSaleAdById(id);
        if (current_ad == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SaleAd not found");
        }
        current_ad.setStopped(true);

        employeeService.stopSaleAd(current_ad);
        return ResponseEntity.ok("Ad is stopped!: " + current_ad);
    }

    @RequestMapping(value = "/ad/getAllAds" , method = RequestMethod.GET)
    public ResponseEntity<?> getAllAds() {
        List<SaleAd> adds = employeeService.findAllSaleAd();
        return ResponseEntity.ok(adds);
    }

    @RequestMapping(value = "/ad/getAd/{id}" , method = RequestMethod.GET)
    public ResponseEntity<?> getAd(@PathVariable long id) {
        SaleAd current_ad = employeeService.findSaleAdById(id);
        if (current_ad == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(current_ad);
    }


    @RequestMapping(value = "/statistics/daily_registered_clients" , method = RequestMethod.GET)
    public ResponseEntity<?> getDailyRegisteredClients() {
        List<User> daily_users = userService.filterUserByDailyLogin();
        return ResponseEntity.ok(daily_users);
    }

    @RequestMapping(value = "/statistics/monthly_registered_clients" , method = RequestMethod.GET)
    public ResponseEntity<?> getMonthlyRegisteredClients() {
        List<User> monthly_users = userService.filterUserByMonthLogin();
        return ResponseEntity.ok(monthly_users);
    }
    @RequestMapping(value = "/statistics/best_employees" , method = RequestMethod.GET)
    public ResponseEntity<?> getBestEmployees() {
        List<Employee> bestEmployees = employeeService.findBestEmployees().reversed();
        return ResponseEntity.ok(bestEmployees);
    }
    @RequestMapping(value = "/statistics/best_employee" , method = RequestMethod.GET)
    public ResponseEntity<?> getBestEmployee() {
        Employee bestEmployees = employeeService.findBestEmployee();
        if (bestEmployees == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Employee not found");
        }
        return ResponseEntity.ok(bestEmployees);
    }
    @RequestMapping(value = "/statistics/getEmployeesByRole/{role}" , method = RequestMethod.GET)
    public ResponseEntity<?> getEmployesByRole(@PathVariable String role) {
        System.out.println(role);
        List<Employee> bestEmployees = employeeService.filterEmployeeByRole(role);
        if (bestEmployees.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employees not found");
        }
        return ResponseEntity.ok(bestEmployees);
    }

    @RequestMapping(value = "/statistics/getEmployeesByAge/{age}" , method = RequestMethod.GET)
    public ResponseEntity<?> getEmployeesByAge(@PathVariable String age) {
        List<Employee> employees = employeeService.filterEmployeeByAge(age);
        if (employees.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employees not found");
        }
        return ResponseEntity.ok(employees);
    }

    @RequestMapping(value = "/statistics/getAdvertisementPriceByType" , method = RequestMethod.GET)
    public ResponseEntity<?> getAdvertisementPriceByType(@RequestParam String ad_type) {
        List<SaleAd> saleAds = employeeService.filterSaleAdByType(ad_type);
        if (saleAds.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employees not found");
        }
        return ResponseEntity.ok(saleAds);
    }

    @RequestMapping(value = "/statistics/getAdvertisementMostAddedByEmployee" , method = RequestMethod.GET)
    public ResponseEntity<?> getAdvertisementMostAddedByEmployee() {
        String saleAds = employeeService.filterSaleAdByPriceAddedBySale();
        if (saleAds.length() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employees not found");
        }
        return ResponseEntity.ok(saleAds);
    }

    @RequestMapping(value = "/statistics/getMonthlyIncludedAdvertisement" , method = RequestMethod.GET)
    public ResponseEntity<?> getMonthlyIncludedAdvertisement() {
        List<SaleAd> saleAds = employeeService.getMonthlyIncludedAds();
        if (saleAds.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employees not found");
        }
        return ResponseEntity.ok(saleAds);
    }

    @RequestMapping(value = "/statistics/getMonthlyStoppedAdvertisement" , method = RequestMethod.GET)
    public ResponseEntity<?> getMonthlyStoppedAdvertisement() {
        List<SaleAd> saleAds = employeeService.getMonthlyStoppedAds();
        if (saleAds.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employees not found");
        }
        return ResponseEntity.ok(saleAds);
    }




    public class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    static class JWTToken {
        private String token;

        public JWTToken(String token) {
            this.token = token;
        }

        @JsonProperty("jwt_token")
        public String getToken() {
            return token;
        }
    }
}
