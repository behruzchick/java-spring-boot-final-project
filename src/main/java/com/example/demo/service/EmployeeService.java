package com.example.demo.service;

import com.example.demo.model.Employee;
import com.example.demo.model.SaleAd;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.SaledRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private final PasswordEncoder passwordEncoder;
    private final SaledRepository saledRepository;

    private final EmployeeRepository employeeRepository;
    public EmployeeService(PasswordEncoder passwordEncoder, SaledRepository saledRepository, EmployeeRepository employeeRepository) {
        this.passwordEncoder = passwordEncoder;
        this.saledRepository = saledRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(long id) {
        return employeeRepository.findById(id);
    }
    public Employee deleteById(long id) {
        Employee employee = employeeRepository.findById(id);
        employeeRepository.delete(employee);
        return employee;
    }

    public Employee changeEmployee(Employee employee) {
        Employee saved_employee = employeeRepository.save(employee);
        return saved_employee;
    }

    public Employee save(Employee employee) {
        String bcrypted_password = passwordEncoder.encode(employee.getPassword());
        employee.setPassword(bcrypted_password);
        return employeeRepository.save(employee);
    }

    public Employee findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    public SaleAd createSaleAd(SaleAd saleAd) {
        return saledRepository.save(saleAd);
    }
    public SaleAd updateSaleAd(SaleAd saleAd) {
        return saledRepository.save(saleAd);
    }

    public SaleAd stopSaleAd(SaleAd saleAd) {
        return saledRepository.save(saleAd);
    }

    public List<SaleAd> findAllSaleAd() {
        return saledRepository.findAll();
    }
    public SaleAd findSaleAdById(long id) {
        return saledRepository.findById(id);
    }

    public List<Employee> findBestEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .sorted(Comparator.comparingInt(empl -> empl.getAdded_clients().size()))
                .collect(Collectors.toList());
    }

    public Employee findBestEmployee(){
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .max(Comparator.comparingInt(empl -> empl.getAdded_clients().size()))
                .orElse(null);
    }

    public List<Employee> filterEmployeeByRole(String role){

        List<Employee> employeesList = employeeRepository.findAllByRoles(role);

        return employeesList;
    }

    public List<Employee> filterEmployeeByAge(String age){
        List<Employee> employeeList = employeeRepository.findAllByAge(age);
        return employeeList;
    }

    public List<SaleAd> filterSaleAdByType(String ad_type){
        List<SaleAd> saleAds = saledRepository.findAllPricesByAd_type(ad_type);
        return saleAds;
    }

    public String  filterSaleAdByPriceAddedBySale(){
        List<SaleAd> saleAds = saledRepository.findAll();
        return saleAds
                .stream().filter(sale -> sale.getAdded_by() != null) // Игнорируем записи с null в added_by
                .collect(Collectors.groupingBy(SaleAd::getAdded_by, Collectors.counting())) // Группируем по added_by и подсчитываем количество
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue()) // Находим пользователя с максимальным количеством объявлений
                .map(Map.Entry::getKey) // Берём имя пользователя
                .orElse(null);
    }

    public List<SaleAd> getMonthlyIncludedAds(){
        List<SaleAd> saleAds = saledRepository.findAll();
        Instant instant = Instant.now();
        Long secondMonth = 30L * 24 * 60 * 60;

        return saleAds.stream().filter(filter_ad -> {
            Instant registeredAt = filter_ad.getCreated_at().toInstant();
            long differenceSeconds = instant.getEpochSecond() - registeredAt.getEpochSecond();
            return differenceSeconds <= secondMonth;
        }).collect(Collectors.toList());
    }

    public List<SaleAd> getMonthlyStoppedAds(){
        List<SaleAd> saleAds = saledRepository.findAll();
        Instant instant = Instant.now();
        Long secondMonth = 30L * 24 * 60 * 60;

        return saleAds.stream()
                .filter(ad -> ad.isStopped())
                .filter(ad -> {
                    Instant registeredAt = ad.getCreated_at().toInstant();
                    long differenceSeconds = instant.getEpochSecond() - registeredAt.getEpochSecond();
                    return differenceSeconds <= secondMonth;
                })
                .collect(Collectors.toList());
    }

//    public List<Employee> employeesSalary(){
//        List<Employee> employees = employeeRepository.findAll();
//        return employees.stream()
//                .mapToDouble(Employee::getSalary)
//                .sum();
//    }


}
