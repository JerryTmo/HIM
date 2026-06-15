package com.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 医生信息模型
 */
public class Doctor {
    private String id;
    private String name;
    private String department;
    private String title;
    private String phone;
    private String specialty;
    private String description;
    private List<String> availableTimes;
    private String status;

    public Doctor() {
        this.availableTimes = new ArrayList<>();
        this.status = "在职";
    }

    public Doctor(String id, String name, String department, String title) {
        this();
        this.id = id;
        this.name = name;
        this.department = department;
        this.title = title;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getAvailableTimes() { return availableTimes; }
    public void setAvailableTimes(List<String> availableTimes) { this.availableTimes = availableTimes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
