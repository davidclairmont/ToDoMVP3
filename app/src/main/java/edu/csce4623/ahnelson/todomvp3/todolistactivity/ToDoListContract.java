package edu.csce4623.ahnelson.todomvp3.todolistactivity;

import android.content.Context;

import java.util.List;

import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;

/**
 * ToDoListContract
 * Two inner interfaces, a View and a Presenter for the ToDoListActivity
 */
public interface ToDoListContract {

    interface View{
        /**
         * setPresenter - sets the presenter associated with a View
         * @param presenter - the ToDoListContract.presenter instance
         */
        void setPresenter(ToDoListContract.Presenter presenter);

        /**
         * showToDoItems - takes a list of toDoItems and populates a ListView
         * @param toDoItemList - List of ToDoItems
         */
        void showToDoItems(List<ToDoItem> toDoItemList);

        /**
         * showAddEditToDoItem - Creates an intent object to launch add or edit to do item activity
         * @param item - Item to be added/modified
         * @param requestCode - Integer code referencing whether a ToDoItem is being added or edited
         */
        void showAddEditToDoItem(ToDoItem item, int requestCode);

        // used to receive context from the view
        Context getContext();
    }

    interface Presenter{
        /**
         * loadToDoItems - Loads all ToDoItems from the ToDoItemsRepository
         */
        void loadToDoItems();

        /**
         * start -- All procedures that need to be started
         * Ideally, should be coupled with a stop if any running tasks need to be destroyed.
         */
        void start();

        /**
         * addNewToDoItem -- Create a new ToDoItem with stub values
         * Calls showAddEditToDoItem with created item and adding item integer
         */
        void addNewToDoItem();

        /**
         * showExistingToDoItem -- Edit an existing toDoItem
         * Calls showAddEditToDoItem with existing item and editing item integer
         * @param item - Item to be edited
         */
        void showExistingToDoItem(ToDoItem item);

        /**
         * updateToDoItem -- Item to be updated in the dataRepository
         * @param item -- ToDoItem to be updated in the ToDoItemRepository
         */
        void updateToDoItem(ToDoItem item);

        /**
         * result -- Passthrough from View
         * Takes the requestCode, resultCode, and the returned ToDoItem from a call to showAddEditToDoItem
         * on an OK result, and either creates or updates item in the repository
         * @param requestCode -- Integer code identifying whether it was an update or edit call
         * @param resultCode -- Integer code identifying the result from the Intent
         * @param item -- ToDoItem returned from the AddEditToDoItemActivity
         */
        void result(int requestCode, int resultCode, ToDoItem item);

        // function used to create an alarm for ToDoItem
        void createAlarm(Context context, ToDoItem item);

        // function used to delete an alarm for ToDoItem
        void deleteAlarm(Context context, ToDoItem item);

        // function used to update an alarm for ToDoItem
        void updateAlarm(Context context, ToDoItem item);
    }

}
