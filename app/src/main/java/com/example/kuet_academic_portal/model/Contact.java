package com.example.kuet_academic_portal.model;

public class Contact {
    private String id;
    private String name;
    private String designation;
    private String email;
    private String phone;
    private String department;

    public Contact() {
        // Required empty public constructor
    }

    public Contact(String name, String designation, String email, String phone, String department) {
        this.name = name;
        this.designation = designation;
        this.email = email;
        this.phone = phone;
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

