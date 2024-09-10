package com.assignment1.meetingmanagement;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /**
     * Schedule Meetings
     *      include date and time
     * Multiple Meetings for same day
     * Ability to see all meetings done in a particular day
     *      All meetings today / tomorrow.
     *      All meetings for any day.
     *
     * Add contacts to meetings
     *
     * Persistence of meetings / contacts across multiple executions
     *
     * Ability to manage locations / contacts outside of the view for a specific meeting
     * Ability to completely clear all meetings
     *
     * The ability to clear all meetings for a single day
     *
     * The ability to push all of today's meetings to another day.
     *
     *
     *
     *
     *
     */

    private ListView listView;
    private TextView meetingNameTextView, dateTextView, timeTextView;
    private Button addMeeting, dateBtn, timeBtn;
    MeetingScheduler scheduler = new MeetingScheduler();

    List<Meeting> meetingsList;

    CustomMeetingAdapter meetingAdapter;

    String[] item = {"View All Meetings", "View Todays Meeting", "View Tomorrows Meeting"};

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUIViews();

        meetingsList = FileHelper.readData(this); // reads from existing file

        meetingAdapter = new CustomMeetingAdapter(getApplicationContext(), meetingsList); //updates the listview with existing values.
        listView.setAdapter(meetingAdapter);

        /**
         * Shows actions that can be selected
             * Action 1: View All Meetings
             * Action 2: View Todays Meetings
             * Action 3: Vew Tomorrows Meetings
         */
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItems);

        // updates result of the modified contacts. when contacts name updated it immediately refreshes the UI
        ActivityResultLauncher<Intent> detailsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        meetingsList = FileHelper.readData(this); // on result ok refreshes the UI to update content.
                        meetingAdapter.clear();
                        meetingAdapter.addAll(meetingsList);
                        meetingAdapter.notifyDataSetChanged();
                    }
                }
        );

        // new intent created here, the intent's purpose is to add new contact name and phone number.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                detailsIntent.putExtra("keyword", position);
                detailsLauncher.launch(detailsIntent);

            }
        });

        // opens calender
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender();
            }
        });

        // opens clock
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openClock();
            }
        });

        // adds meeting
        addMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMeeting();
            }
        });

        // shows actions on meetings.
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String currentAction = adapterView.getItemAtPosition(position).toString();

                switch(currentAction){
                    case "View All Meetings":
                        meetingsList = fetchAllMeetings(); // fetches all the meetings from the list
                        break;
                    case "View Todays Meeting":
                        meetingsList = fetchTodayMeetings(); // fetches only today's meetings
                        break;
                    case "View Tomorrows Meeting":
                        meetingsList = fetchTomorrowsMeetings(); // fetches tomorrow's meetings.
                        break;
                    default:
                        break;
                }

                // clears adapter and updates the list and notifies the change to the adapter.
                meetingAdapter.clear();
                meetingAdapter.addAll(meetingsList);
                meetingAdapter.notifyDataSetChanged();
            }
        });
    }

    // Search option, can be searched by date.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Contact Here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String date) {
                meetingAdapter.getFilter().filter(date);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // on option items clicked from the nav bar
    // for id 0 clears everything
    // for id 1 clear todays meetings
    // for id 2 pushes the meetings.
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(id == R.id.clear){
            clearAllMeetings();
        }
        else if(id == R.id.clearAllMeetingsForToday){
            clearAllMeetingsForToday();
        }
        else if( id == R.id.pushMeetings){
            pushMeetings();
        }
        return true;
    }


    // clears all meetings from the file and the list view.
    private void clearAllMeetings() {
        meetingsList.clear();
        meetingAdapter.notifyDataSetChanged();
        FileHelper.writeData(meetingsList, getApplicationContext());
    }

    // clears all meetings for today
    private void clearAllMeetingsForToday() {
        List<Meeting> exceptTodayMeetings = fetchAllMeetings();

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.M.d", Locale.getDefault());
        String todayDate = dateFormat.format(today);

        Iterator<Meeting> iterator = exceptTodayMeetings.iterator();
        while (iterator.hasNext()) {
            Meeting meeting = iterator.next();
            if (meeting.getDate().equals(todayDate)) {
                iterator.remove();
            }
        }

        meetingsList = exceptTodayMeetings;

        meetingAdapter.clear();
        meetingAdapter.addAll(meetingsList);
        meetingAdapter.notifyDataSetChanged();
        FileHelper.writeData(meetingsList, getApplicationContext());
    }

    // pushes the meetings if next day is weekday then to next weekday, could be the  weekday after the weekend's if the next day is weekend
    // pushes the meetings to the next following weekend day or to the next week's weekend.
    private void pushMeetings() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.M.d", Locale.getDefault());
        String todayDate = dateFormat.format(today);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // gets the day of the current week
        boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY); // checks if it's a weekend.

        if (isWeekend) { // if weekend
            if (dayOfWeek == Calendar.SATURDAY) { // If Saturday, push to Sunday
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            } else { // If Sunday, push to next Saturday
                calendar.add(Calendar.DAY_OF_WEEK, 6);
            }
        }
        if(!isWeekend) { // if weekday
            int nextDayOfWeek = dayOfWeek + 1;
            if (nextDayOfWeek >= Calendar.SATURDAY) { // Next day is a weekend day
                calendar.add(Calendar.DAY_OF_WEEK, 3); // Update it to Monday
            } else {
                calendar.add(Calendar.DAY_OF_WEEK, 1); // Move to next day
            }
        }

        Date newDate = calendar.getTime();
        String newDateStr = dateFormat.format(newDate);

        Log.d("Meeting Update", "Meeting pushed from: + " + todayDate + "to: " + newDateStr);

        List<Meeting> updatedMeetingsList = new ArrayList<>(meetingsList);

        for (Meeting meeting : updatedMeetingsList) {
            if (meeting.getDate().equals(todayDate)) {
                meeting.setDate(newDateStr);
                Log.d("Meeting Update", "Meeting pushed from: + " + meeting.getDate() + "to: " + newDateStr);
            }
        }

        meetingAdapter.clear();
        meetingAdapter.addAll(updatedMeetingsList);
        meetingAdapter.notifyDataSetChanged();

        FileHelper.writeData(updatedMeetingsList, getApplicationContext());

        String message = isWeekend ? "Today's meetings pushed to next Weekend day" : "Today's meetings pushed to tomorrow";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private List<Meeting> fetchAllMeetings(){
        meetingsList = FileHelper.readData(this);
        return meetingsList;
    }

    // fetches today's meetings
    private List<Meeting> fetchTodayMeetings(){
        List<Meeting> alLMeetings = FileHelper.readData(this);

        List<Meeting> todayMeetings = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.M.d", Locale.getDefault());
        String todayDate = dateFormat.format(today);

        for(Meeting meeting : alLMeetings){
            if(meeting.getDate().equals(todayDate)){
                todayMeetings.add(meeting);
            }
            else{
                Log.d("Meeting Details", "not matching: Today Meeting Date - " + meeting.getDate() + " vs Today Local Date:" + todayDate);
            }
        }

        return todayMeetings;
    }

    // fetches tomorrow's meetings
    private List<Meeting> fetchTomorrowsMeetings(){
        List<Meeting> alLMeetings = FileHelper.readData(this);

        List<Meeting> tomorrowMeetings = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.M.d", Locale.getDefault());
        String tomorrowDate  = dateFormat.format(tomorrow);

        for(Meeting meeting : alLMeetings){
            if(meeting.getDate().equals(tomorrowDate )){
                tomorrowMeetings.add(meeting);
                Log.d("Meeting Details", "matching: Tomorrow Meeting Date - " + meeting.getDate() + " vs Tomorrow Local Date:" + tomorrowDate);
            }
            else{
                Log.d("Meeting Details", "not matching: Tomorrow Meeting Date - " + meeting.getDate() + " vs Tomorrow Local Date:" + tomorrowDate);
            }
        }

        return tomorrowMeetings;
    }

    // adds meetings to the list.
    private void addMeeting(){
        String meetingName = meetingNameTextView.getText().toString();
        String date = dateTextView.getText().toString();
        String time = timeTextView.getText().toString();

        if (!meetingName.isEmpty() && !date.isEmpty() && !time.isEmpty()) {

            meetingsList.add(scheduler.addMeeting(meetingName, date, time, "None", "None"));

            meetingNameTextView.setText("");
            dateTextView.setText("");
            timeTextView.setText("");

            meetingAdapter.notifyDataSetChanged();
            FileHelper.writeData(meetingsList, getApplicationContext());
        } else {
            Toast.makeText(getApplicationContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCalender(){
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String datePicked = year + "." + (month+1) + "." + dayOfMonth;
                dateTextView.setText(datePicked);
            }
        }, 2024, 3, 29);

        dialog.show();
    }

    private void openClock(){
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hours, int minutes) {
                String timePicked = hours + ":" + minutes;
                timeTextView.setText(timePicked);
            }
        }, 15, 00, true);
        dialog.show();
    }

    // Initialization of the R.id.tags.
    private void setupUIViews(){
        listView = findViewById(R.id.listViewMeeting);

        meetingNameTextView = findViewById(R.id.editTextMeetingName);
        dateTextView = findViewById(R.id.editTextMeetingDate);
        timeTextView = findViewById(R.id.editTextMeetingTime);

        dateBtn = findViewById(R.id.dateBtn);
        timeBtn = findViewById(R.id.timeBtn);
        addMeeting = findViewById(R.id.addMeetingBtn);

        autoCompleteTextView = findViewById(R.id.autoCompleteText);
    }

}