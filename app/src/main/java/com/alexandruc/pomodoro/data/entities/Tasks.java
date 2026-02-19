package com.alexandruc.pomodoro.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Tasks {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name="description")
    private String description;
    @ColumnInfo(name="completed")
    private boolean completed;

    public Tasks(String description, boolean completed){
        this.description = description;
        this.completed = completed;
    }

    public int getId(){
        return id;
    }

    public String getDescription(){
        return description;
    }

    public boolean isCompleted(){
        return completed;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
