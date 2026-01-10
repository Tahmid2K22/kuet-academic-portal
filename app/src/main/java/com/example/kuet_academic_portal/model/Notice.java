package com.example.kuet_academic_portal.model;

public class Notice {
    private String title;
    private String description;
    private String date;
    private int term;
    private int year;

    public Notice() {
    }

    public Notice(String title, String description, String date, int term, int year) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.term = term;
        this.year = year;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}

