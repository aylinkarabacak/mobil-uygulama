package com.example.todolistapp;

import com.example.todolistapp.Model.ToDoModel;

public interface TaskAction {
    void execute(ToDoModel task);
}
