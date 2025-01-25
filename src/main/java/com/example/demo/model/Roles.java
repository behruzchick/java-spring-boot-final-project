package com.example.demo.model;

import jakarta.persistence.*;

import java.io.Serializable;
@Entity
@Table(name = "mohirdev_employee_roles")
public class Roles implements Serializable {
    @Id
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
