package com.example.recipeapp.model;


import java.io.Serializable;

public class Comment implements Serializable {

    private String author;
    private String comment;
    private String date;

    public Comment() {
    }

    public Comment(String author, String comment, String date) {
        this.author = author;
        this.comment = comment;
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "author='" + author + '\'' +
                ", comment='" + comment + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
