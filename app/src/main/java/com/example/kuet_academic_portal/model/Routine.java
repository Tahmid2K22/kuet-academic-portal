package com.example.kuet_academic_portal.model;

public class Routine {
    private String id;
    private String department;
    private int year;
    private int term;
    private String day; // Monday, Tuesday, etc.
    private String startTime;
    private String endTime;
    private String courseCode;
    private String teacher;
    private String room;

    public Routine() {
        // Required empty public constructor
    }

    public Routine(String department, int year, int term, String day, String startTime, String endTime, String courseCode, String teacher, String room) {
        this.department = department;
        this.year = year;
        this.term = term;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.courseCode = courseCode;
        this.teacher = teacher;
        this.room = room;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}

