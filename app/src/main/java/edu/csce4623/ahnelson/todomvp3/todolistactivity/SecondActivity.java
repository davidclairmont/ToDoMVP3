package edu.csce4623.ahnelson.todomvp3.todolistactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.TimeZone;

import edu.csce4623.ahnelson.todomvp3.R;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;

// class for the SecondActivity, used to create a new ToDoItem or update a current one.
// displays information accordingly
public class SecondActivity extends AppCompatActivity {
    private ToDoItem item;
    private TextView title;
    private TextView content;
    private CheckBox check;
    private TextView date;
    private TextView time;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_to_do_item);

        title = findViewById(R.id.etItemTitle);
        content = findViewById(R.id.etItemContent);
        check = findViewById(R.id.checkBox);
        date = findViewById(R.id.textView3);
        time = findViewById(R.id.textView4);
        Button save = findViewById(R.id.btnSaveToDoItem);
        ImageButton delete = findViewById(R.id.imageButton);
        final Calendar calendar = Calendar.getInstance();

        // gets the intent from which this activity was called along with the needed ToDoItem
        final Intent i = getIntent();
        item = (ToDoItem) i.getSerializableExtra("toDoItem");

        title.setText(item.getTitle());
        content.setText(item.getContent());
        check.setChecked(item.getCompleted());

        // check if a date was given for the item. If so, it is displayed
        long fullDate = item.getDueDate();
        if(fullDate != 0){
            calendar.setTimeInMillis(fullDate);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int year = calendar.get(Calendar.YEAR);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            String picked_date = (month+1) + "/" + day + "/" + year;
            Log.d("SecondActivity",String.valueOf(calendar.getTime()));
            date.setText(picked_date);
            time.setText(hour + ":" + minute);
        }

        // Date on click listener
        date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(
                        SecondActivity.this,
                        android.R.style.Theme_Holo_Light_DarkActionBar,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                String picked_date = (month+1) + "/" + day + "/" + year;
                Log.d("SecondActivity",picked_date);
                date.setText(picked_date);
            }
        };

        // Time on click listener
        time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        SecondActivity.this,
                        mTimeSetListener,
                        hour, minute,
                        android.text.format.DateFormat.is24HourFormat(SecondActivity.this));
                timePickerDialog.show();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                time.setText(hour + ":" + minute);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
            }
        };

        // On click listener for the delete button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code for creating the alert
                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Delete Task");
                builder.setMessage("Are you sure you want to delete this task?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            // on click for the confirm button
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // create new intent to return to ToDoListActivity with the
                                // purpose of deleting the task
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("ToDoItem", item);
                                returnIntent.putExtra("DELETE", true);
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                        });
                // do nothing for cancel button
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // On click listener for the save button
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // set item's values
                item.setTitle("" + title.getText());
                item.setContent("" + content.getText());
                if(check.isChecked()){item.setCompleted(check.isChecked());}
                else{item.setCompleted(false);}

                // get date and time to store
                Log.d("SecondActivity", String.valueOf(calendar.getTime()));
                item.setDueDate(calendar.getTimeInMillis());

                // create new intent to return to the main activity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("ToDoItem", item);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    // if the back button is pressed, the app returns to the main activity without doing anything
    @Override
    public void onBackPressed(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ToDoItem", item);
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
