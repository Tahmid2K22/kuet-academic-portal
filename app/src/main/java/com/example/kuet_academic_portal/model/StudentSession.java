package com.example.kuet_academic_portal.model;

public class StudentSession {

    private String department;
    private String email;
    private String name;
    private String phone;
    private String roll;
    private String section;
    private int term;
    private int year;
    private String role;

    public StudentSession() {
    }

    public StudentSession(String department, String email, String name,
                         String phone, String roll, String section,
                         int term, int year, String role) {
        this.department = department;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.roll = roll;
        this.section = section;
        this.term = term;
        this.year = year;
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "StudentSession{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", roll='" + roll + '\'' +
                ", section='" + section + '\'' +
                ", term=" + term +
                ", year=" + year +
                ", role='" + role + '\'' +
                '}';
    }
}

