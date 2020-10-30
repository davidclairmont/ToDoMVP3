package edu.csce4623.ahnelson.todomvp3.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import util.AppExecutors;

/**
 * ToDoItemRepository class - implements the ToDoDataSource interface
 */
public class ToDoItemRepository implements ToDoListDataSource {

    //Memory leak here by including the context - Fix this at some point
    private static volatile ToDoItemRepository INSTANCE;

    //Thread pool for execution on other threads
    private AppExecutors mAppExecutors;
    //Context for calling ToDoProvider
    private Context mContext;

    /**
     * private constructor - prevent direct instantiation
     * @param appExecutors - thread pool
     * @param context
     */
    private ToDoItemRepository(@NonNull AppExecutors appExecutors, @NonNull Context context){
        mAppExecutors = appExecutors;
        mContext = context;
    }

    /**
     * public constructor - prevent creation of instance if one already exists
     * @param appExecutors
     * @param context
     * @return
     */
    public static ToDoItemRepository getInstance(@NonNull AppExecutors appExecutors, @NonNull Context context){
        if(INSTANCE == null){
            synchronized (ToDoItemRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new ToDoItemRepository(appExecutors, context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * getToDoItems runs query in a separate thread, and on success loads data from cursor into a list
     * @param callback
     */
    @Override
    public void getToDoItems(@NonNull final LoadToDoItemsCallback callback) {
        Log.d("REPOSITORY","Loading...");
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                String[] projection = {
                        ToDoItem.TODOITEM_ID,
                        ToDoItem.TODOITEM_TITLE,
                        ToDoItem.TODOITEM_CONTENT,
                        ToDoItem.TODOITEM_DUEDATE,
                        ToDoItem.TODOITEM_COMPLETED};
                final Cursor c = mContext.getContentResolver().query(Uri.parse("content://" + ToDoProvider.AUTHORITY + "/" + ToDoProvider.TODOITEM_TABLE_NAME), projection, null, null, null);
                final List<ToDoItem> toDoItems = new ArrayList<ToDoItem>(0);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(c == null){
                            callback.onDataNotAvailable();
                        } else{
                            while(c.moveToNext()) {
                                ToDoItem item = new ToDoItem();
                                item.setId(c.getInt(c.getColumnIndex(ToDoItem.TODOITEM_ID)));
                                item.setTitle(c.getString(c.getColumnIndex(ToDoItem.TODOITEM_TITLE)));
                                item.setContent(c.getString(c.getColumnIndex(ToDoItem.TODOITEM_CONTENT)));
                                item.setDueDate(c.getLong(c.getColumnIndex(ToDoItem.TODOITEM_DUEDATE)));
                                item.setCompleted(c.getInt(c.getColumnIndex(ToDoItem.TODOITEM_COMPLETED)) > 0);
                                toDoItems.add(item);
                            }
                            c.close();
                            callback.onToDoItemsLoaded(toDoItems);
                        }
                    }
                });

            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Not implemented yet
     * @param toDoItemId
     * @param callback
     */
    @Override
    public void getToDoItem(@NonNull String toDoItemId, @NonNull GetToDoItemCallback callback) {
        Log.d("REPOSITORY","GetToDoItem");
    }

    /**
     * saveToDoItem runs contentProvider update in separate thread
     * @param toDoItem
     */
    @Override
    public void saveToDoItem(@NonNull final ToDoItem toDoItem) {
        Log.d("REPOSITORY","SaveToDoItem");
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                ContentValues myCV = new ContentValues();
                myCV.put(ToDoItem.TODOITEM_ID,toDoItem.getId());
                myCV.put(ToDoItem.TODOITEM_TITLE,toDoItem.getTitle());
                myCV.put(ToDoItem.TODOITEM_CONTENT,toDoItem.getContent());
                myCV.put(ToDoItem.TODOITEM_DUEDATE,toDoItem.getDueDate());
                myCV.put(ToDoItem.TODOITEM_COMPLETED,toDoItem.getCompleted());
                final int numUpdated = mContext.getContentResolver().update(Uri.parse("content://" + ToDoProvider.AUTHORITY + "/" + ToDoProvider.TODOITEM_TABLE_NAME), myCV,null,null);
                Log.d("REPOSITORY","Update ToDo updated " + String.valueOf(numUpdated) + " rows");
            }
        };
        mAppExecutors.diskIO().execute(runnable);

    }

    /**
     * createToDoItem runs contentProvider insert in separate thread
     * @param toDoItem
     */
    @Override
    public void createToDoItem(@NonNull final ToDoItem toDoItem) {
        Log.d("REPOSITORY","CreateToDoItem");
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                ContentValues myCV = new ContentValues();
                myCV.put(ToDoItem.TODOITEM_TITLE,toDoItem.getTitle());
                myCV.put(ToDoItem.TODOITEM_CONTENT,toDoItem.getContent());
                myCV.put(ToDoItem.TODOITEM_DUEDATE,toDoItem.getDueDate());
                myCV.put(ToDoItem.TODOITEM_COMPLETED,toDoItem.getCompleted());
                final Uri uri = mContext.getContentResolver().insert(Uri.parse("content://" + ToDoProvider.AUTHORITY + "/" + ToDoProvider.TODOITEM_TABLE_NAME), myCV);
                Log.d("REPOSITORY","Create ToDo finished with URI" + uri.toString());
            }
        };
        mAppExecutors.diskIO().execute(runnable);

    }

    /**
     * deleteToDoItem runs contentProvider delete in separate thread
     * @param toDoItem
     */
    public void deleteToDoItem(@NonNull final ToDoItem toDoItem) {
        Log.d("REPOSITORY","DeleteToDoItem");
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                Uri uri = Uri.parse("content://" + ToDoProvider.AUTHORITY + "/" + ToDoProvider.TODOITEM_TABLE_NAME + "/" + toDoItem.getId());
                int isDeleted = mContext.getContentResolver().delete(uri, null, null);
                if(isDeleted == 0){
                    Log.d("REPOSITORY","Not Deleted!");
                }else {
                    Log.d("REPOSITORY","Deleted!");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);

    }
}
