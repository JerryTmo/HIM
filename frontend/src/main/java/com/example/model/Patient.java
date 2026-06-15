package com.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 患者信息模型
 */
public class Patient {
    private String id;
    private String name;
    private String gender;
    private int age;
    private String phone;
    private String idCard;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private LocalDate registrationDate;
    private List<String> medicalHistory;
    private String status;

    public Patient() {
        this.medicalHistory = new ArrayList<>();
        this.registrationDate = LocalDate.now();
        this.status = "正常";
    }

    public Patient(String id, String name, String gender, int age, String phone) {
        this();
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public List<String> getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(List<String> medicalHistory) { this.medicalHistory = medicalHistory; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
