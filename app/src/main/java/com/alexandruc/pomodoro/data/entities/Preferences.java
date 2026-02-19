package com.alexandruc.pomodoro.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "preferences")

public class Preferences {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name="tag")
    private String tag;

    @ColumnInfo(name="value")
    private String value;

    public Preferences(String tag, String value){
        this.tag = tag; this.value = value;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public String getValue() {
        return value;
    }
}
