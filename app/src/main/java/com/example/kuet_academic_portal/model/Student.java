package com.example.kuet_academic_portal.model;

public class Student {
    private String id;
    private Object roll;  
    private String name;
    private String department;
    private Object term;  
    private Object year;  
    private String section;
    private String email;
    private Object phone;  
    private String role;  

    public Student() {
    }

    public Student(String roll, String name, String department, String term, String year, String section, String email, String phone, String role) {
        this.roll = roll;
        this.name = name;
        this.department = department;
        this.term = term;
        this.year = year;
        this.section = section;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoll() {
        if (roll == null) return "";
        return roll.toString();
    }

    public void setRoll(Object roll) {
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


    public String getTerm() {
        if (term == null) return "";
        return term.toString();
    }

    public void setTerm(Object term) {
        this.term = term;
    }

    public String getYear() {
        if (year == null) return "";
        return year.toString();
    }

    public void setYear(Object year) {
        this.year = year;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        if (phone == null) return "";
        return phone.toString();
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

