package com.example.kuet_academic_portal.model;

public class Assignment {
    private String id;
    private String title;
    private String description;
    private String course;
    private String department;
    private Object term;
    private Object year;
    private String dueDate;

    public Assignment() {
    }

    public Assignment(String title, String description, String course, String department, Object term, Object year, String dueDate) {
        this.title = title;
        this.description = description;
        this.course = course;
        this.department = department;
        this.term = term;
        this.year = year;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}

