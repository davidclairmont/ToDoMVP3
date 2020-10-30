package edu.csce4623.ahnelson.todomvp3.data;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//Room Database implementation
//Don't touch unless you know what you are doing.
@Database(entities = {ToDoItem.class}, version = 1, exportSchema = false)
public abstract class ToDoItemDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "todo_db";
    private static ToDoItemDatabase INSTANCE;

    public static ToDoItemDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context,ToDoItemDatabase.class,DATABASE_NAME).build();
        }
        return INSTANCE;
    }

    public abstract ToDoItemDao getToDoItemDao();

}
