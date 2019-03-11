package com.ar.dev.todo;

public class Model {
    private String taskID;
    private String taskTitle;
    private String taskDesc;

    public Model() {
    }

    public Model(String taskID, String taskTitle, String taskDesc) {
        this.taskID = taskID;
        this.taskTitle = taskTitle;
        this.taskDesc = taskDesc;
    }

    public String getTaskID() {
        return taskID;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskDesc() {
        return taskDesc;
    }
}
