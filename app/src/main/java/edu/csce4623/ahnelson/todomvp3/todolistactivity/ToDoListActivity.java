package edu.csce4623.ahnelson.todomvp3.todolistactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import static com.google.common.base.Preconditions.checkNotNull;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import edu.csce4623.ahnelson.todomvp3.R;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItemRepository;
import util.AppExecutors;

/**
 * ToDoListActivity - Main Activity for the Application
 */
public class ToDoListActivity extends AppCompatActivity {

    //local instance of the toDoListPresenter, passed through into the toDoListFragment
    private ToDoListPresenter mToDoListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set view to use the main activity layout - a content frame that holds a single fragment
        setContentView(R.layout.activity_main);

        //ToDoListFragment -- Main view for the ToDoListActivity
        ToDoListFragment toDoListFragment =
                (ToDoListFragment) getSupportFragmentManager().findFragmentById(R.id.toDoListFragmentFrame);
        if (toDoListFragment == null) {
            // Create the fragment
            toDoListFragment = ToDoListFragment.newInstance();
            // Check that it is not null
            checkNotNull(toDoListFragment);
            // Populate the fragment into the activity
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.toDoListFragmentFrame, toDoListFragment);
            transaction.commit();
        }
        //Get an instance of the ToDoListPresenter
        //Parameters - ToDoListRepository - Instance of the toDoListRepository
        //toDoListFragment - the View to be communicated to by the presenter
        // ToDoListRepository needs a thread pool to execute database/network calls in other threads
        // ToDoListRepository needs the application context to be able to make calls to the ContentProvider
        mToDoListPresenter = new ToDoListPresenter(ToDoItemRepository.getInstance(new AppExecutors(),getApplicationContext()),toDoListFragment);

    }
}