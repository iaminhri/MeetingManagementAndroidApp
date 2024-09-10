package com.assignment1.meetingmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomMeetingAdapter extends ArrayAdapter<Meeting> {

    private List<Meeting> newList;
    public CustomMeetingAdapter(Context context, List<Meeting> meetingList){

        super(context, R.layout.meeting_item, meetingList);
        this.newList = meetingList;
    }

    /**
     * Custom view adapter for Meetings.
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Meeting meetings = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meeting_item, parent, false);
        }

        TextView meetingNameTextView = convertView.findViewById(R.id.meetingNameTextView);
        TextView dateTextView = convertView.findViewById(R.id.meetingDateTextView);
        TextView timeTextView = convertView.findViewById(R.id.meetingTimeTextView);
        Button deleteMeetingBtn = convertView.findViewById(R.id.deleteMeetingBtn);

        TextView contactNameText = convertView.findViewById(R.id.contactNameTextView);
        TextView phoneNoText = convertView.findViewById(R.id.phoneNumberTextView);

        // if meeting is not null then sets the values to the textview.
        if(meetings != null){
            meetingNameTextView.setText(meetings.getMeetingName());
            dateTextView.setText(meetings.getDate());
            timeTextView.setText(meetings.getTime());
            contactNameText.setText(meetings.getContactName());
            phoneNoText.setText(meetings.getPhoneNumber());
        }

        // on clicking X button it will delete the instance from the file and the list.
        deleteMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meetings != null) {
                    newList.remove(meetings);
                    FileHelper.writeData(newList, getContext());
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }
}
