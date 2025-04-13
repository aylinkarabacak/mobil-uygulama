package com.example.todolistapp;

import com.example.todolistapp.Model.ToDoModel;
import com.example.todolistapp.Utils.DataBaseHelper;

public class CompleteTaskAction implements TaskAction {
    private DataBaseHelper db;

    public CompleteTaskAction(DataBaseHelper db) {
        this.db = db;
    }

    @Override
    public void execute(ToDoModel task) {
        db.updateStatus(task.getId(), 1);
    }
}
