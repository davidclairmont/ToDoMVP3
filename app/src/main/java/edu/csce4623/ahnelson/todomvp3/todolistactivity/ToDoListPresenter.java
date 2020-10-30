package edu.csce4623.ahnelson.todomvp3.todolistactivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.List;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItemRepository;
import edu.csce4623.ahnelson.todomvp3.data.ToDoListDataSource;

/**
 * ToDoListPresenter -- Implements the Presenter interface from ToDoListContract Presenter
 */
public class ToDoListPresenter implements ToDoListContract.Presenter {

    //Data repository instance
    //Currently has a memory leak -- need to refactor context passing
    private static ToDoItemRepository mToDoItemRepository;
    //View instance
    private final ToDoListContract.View mToDoItemView;

    // Integer request codes for creating or updating through the result method
    private static final int CREATE_TODO_REQUEST = 0;
    private static final int UPDATE_TODO_REQUEST = 1;
    private static final int DELETE_TODO_REQUEST = 2;

    /**
     * ToDoListPresenter constructor
     * @param toDoItemRepository - Data repository instance
     * @param toDoItemView - ToDoListContract.View instance
     */
    public ToDoListPresenter(@NonNull ToDoItemRepository toDoItemRepository, @NonNull ToDoListContract.View toDoItemView){
        mToDoItemRepository = toDoItemRepository;
        mToDoItemView = toDoItemView;
        //Make sure to pass the presenter into the view!
        mToDoItemView.setPresenter(this);
    }

    @Override
    public void start(){
        //Load all toDoItems
        loadToDoItems();
    }


    @Override
    public void addNewToDoItem() {
        //Create stub ToDoItem with temporary data
        ToDoItem item = new ToDoItem();
        item.setTitle("Title");
        item.setContent("Content");
        item.setCompleted(false);
        item.setDueDate(0);
        item.setId(-1);
        //Show AddEditToDoItemActivity with a create request and temporary item
        mToDoItemView.showAddEditToDoItem(item,CREATE_TODO_REQUEST);
    }

    @Override
    public void showExistingToDoItem(ToDoItem item) {
        //Show AddEditToDoItemActivity with a edit request, passing through an item
       Log.d("ToDoListPresenter", "Show Existing ToDoItem");
       mToDoItemView.showAddEditToDoItem(item,UPDATE_TODO_REQUEST);
    }

    @Override
    public void result(int requestCode, int resultCode, ToDoItem item) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CREATE_TODO_REQUEST){
                createToDoItem(item);
            }else if(requestCode == UPDATE_TODO_REQUEST){
                updateToDoItem(item);
            }else if(requestCode == DELETE_TODO_REQUEST){
                deleteToDoItem(item);
            }

            else{
                Log.e("ToDoPresenter", "No such request!");
            }
        }
    }

    /**
     * Delete ToDoItem in repository from ToDoItem and reload data
     *
     **/
    private void deleteToDoItem(ToDoItem item){
        Log.d("ToDoListPresenter","Delete Item");
        mToDoItemRepository.deleteToDoItem(item);
        deleteAlarm(mToDoItemView.getContext(), item);
    }

    // function to delete the current alarm associated with the toDoItem
    public void deleteAlarm(Context context, ToDoItem item){
        Log.d("ToDoListPresenter","Delete Alarm");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        Log.d("ToDoListPresenter","Alarm Deleted!");
    }


    /**
     * Create ToDoItem in repository from ToDoItem and reload data
     * @param item - item to be placed in the data repository
     */
    private void createToDoItem(ToDoItem item){
        Log.d("ToDoListPresenter","Create Item");
        mToDoItemRepository.createToDoItem(item);
        createAlarm(mToDoItemView.getContext(), item);
    }

    public void createAlarm(Context context, ToDoItem item){
        // check if the time selected by user is past the current time
        long dueDate = item.getDueDate();
        long currentTime = System.currentTimeMillis();

        // calendar for dueDate
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(dueDate);
        calendar1.add(Calendar.DATE, 1);

        // calendar for currentTime
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(currentTime);
        calendar2.add(Calendar.DATE, 1);

        if(calendar1.getTime().after(calendar2.getTime())){
            // create an alarm and notification
            int id = item.getId();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmNotificationIntent = new Intent(context,ReminderBroadcast.class);
            alarmNotificationIntent.putExtra("title",item.getTitle());
            alarmNotificationIntent.putExtra("content",item.getContent());
            alarmNotificationIntent.putExtra("id",id);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context,id,alarmNotificationIntent,0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,item.getDueDate(),alarmIntent);
            Log.d("ToDoListPresenter","Alarm Set!");
        }else{
            Log.d("ToDoListPresenter","The time entered for the alarm is at or before the current time.");
        }

    }

    /**
     * Update ToDoItem in repository from ToDoItem and reload data
     * @param item -- ToDoItem to be updated in the ToDoItemRepository
     */
    @Override
    public void updateToDoItem(ToDoItem item){
        Log.d("ToDoListPresenter", "Update Item");
        mToDoItemRepository.saveToDoItem(item);
        updateAlarm(mToDoItemView.getContext(), item);
    }

    // function to update Alarm. deletes the current alarm and assigns a new one with the new time
    public void updateAlarm(Context context, ToDoItem item){
        Log.d("ToDoListPresenter", "Update Alarm");
        deleteAlarm(context, item);
        createAlarm(context, item);
        Log.d("ToDoListPresenter", "Alarm has been updated!");
    }

    /**
     * loadToDoItems -- loads all items from ToDoItemRepository
     * Two callbacks -- success/failure
     */
    @Override
    public void loadToDoItems(){
        Log.d("ToDoListPresenter","Loading ToDoItems");
        mToDoItemRepository.getToDoItems(new ToDoListDataSource.LoadToDoItemsCallback() {
            @Override
            public void onToDoItemsLoaded(List<ToDoItem> toDoItems) {
                Log.d("PRESENTER","Loaded");
                ////////////////////////////////////
                // Remove the following lines
                // Just for demonstration
                ////////////////////////////////////
                if (toDoItems.size() == 0){
                    ToDoItem temp = new ToDoItem();
                    temp.setId(-1);
                    temp.setDueDate(-1);
                    temp.setCompleted(false);
                    temp.setTitle("Temporary To-Do Item");
                    temp.setContent("Temporary Content");
                    toDoItems.add(temp);
                }
                mToDoItemView.showToDoItems(toDoItems);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("PRESENTER","Not Loaded");
            }
        });
    }


}
