package edu.csce4623.ahnelson.todomvp3.data;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ToDoItemDao {
    /**
     * Insert a todoitem into the table
     * @return row ID for newly inserted data
     */
    @Insert
    long insert(ToDoItem item);    /**
     * select all todoitems
     * @return A {@link Cursor} of all todoitems in the table
     */
    @Query("SELECT * FROM ToDoItem")
    Cursor findAll();      /**
     * Delete a todoitem by ID
     * @return A number of todoitems deleted
     */
    @Query("DELETE FROM ToDoItem WHERE id = :id ")
    int delete(long id);    /**
     * Update the todoitem
     * @return A number of todoitems updated
     */
    @Update
    int update(ToDoItem todoitem);
}