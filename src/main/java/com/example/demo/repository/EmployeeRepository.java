package com.example.demo.repository;

import com.example.demo.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//    Optional<Employee> findByUsername(String username);
    Employee save(Employee employee);
    Employee findById(long id);

    Employee findByUsername(String username);
    Employee findByEmail(String email);

    @Query("SELECT u FROM Employee u JOIN u.roles r WHERE r.name  = :role")
    List<Employee> findAllByRoles(@Param("role") String role);


    @Query("SELECT u FROM Employee u WHERE u.age = :age")
    List<Employee> findAllByAge(@Param("age") String age);
}
