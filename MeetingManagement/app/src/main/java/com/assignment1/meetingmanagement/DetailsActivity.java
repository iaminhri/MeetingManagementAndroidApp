package com.assignment1.meetingmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    TextView contactNameView, phoneNoView;

    Button addContactBtn;

    List<Meeting> meetingList;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);

        setupUI();

        meetingList = FileHelper.readData(this);

        Intent intent = getIntent();

        // position gotten from MainActivity.class
        position = intent.getIntExtra("keyword", -1);

        // takes the current clicked position and updates the contact and phone number in the list.
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactName = contactNameView.getText().toString();
                String phoneNo = phoneNoView.getText().toString();

                if(position != -1 && position < meetingList.size()){
                    meetingList.get(position).setContactName(contactName);
                    meetingList.get(position).setPhoneNumber(phoneNo);

                    FileHelper.writeData(meetingList, DetailsActivity.this);
                    Toast.makeText(DetailsActivity.this, "Contact added successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(DetailsActivity.this, "Invalid position or meetingList", Toast.LENGTH_SHORT).show();
                }

                setResult(RESULT_OK);
                finish();
            }
        });
    }

    // initializing R.id.tags
    public void setupUI(){
        contactNameView = findViewById(R.id.editContactNameText);
        phoneNoView = findViewById(R.id.editPhoneNoText);

        addContactBtn = findViewById(R.id.addContactBtn);
    }
}