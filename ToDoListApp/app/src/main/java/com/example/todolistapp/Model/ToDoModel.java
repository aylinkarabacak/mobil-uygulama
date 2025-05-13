package com.example.todolistapp.Model;

public class ToDoModel {

    private int id;
    private String task;
    private String date;
    private String time;  // Saat bilgisini tutmak için yeni değişken
    private String imageUri;
    private int status;  // Durum bilgisini tutmak için ekledik

    // Constructor
    public ToDoModel() {
    }

    // Getter ve Setter metodları
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    // Saat setter metodu eklendi
    public void setTime(String time) {
        this.time = time;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getStatus() {
        return status;  // Status getter metodu
    }

    public void setStatus(int status) {
        this.status = status;  // Status setter metodu
    }
}
