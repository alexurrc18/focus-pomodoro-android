package com.alexandruc.pomodoro.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.lifecycle.LiveData;

import com.alexandruc.pomodoro.data.entities.Tasks;

import java.util.List;

@Dao
public interface TasksDao {
    @Insert
    void insert(Tasks task);

    @Update
    void update(Tasks task);

    @Delete
    void delete(Tasks task);

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    LiveData<List<Tasks>> getAllTasks();
}
