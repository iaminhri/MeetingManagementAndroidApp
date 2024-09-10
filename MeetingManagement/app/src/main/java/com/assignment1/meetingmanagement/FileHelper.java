package com.assignment1.meetingmanagement;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static final String FILENAME = "meetingslist.dat";

    public static void writeData(List<Meeting> meetings, Context context){
        try {
            FileOutputStream fileOutput = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fileOutput);
            oos.writeObject(meetings);
            oos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Meeting> readData(Context context){
        List<Meeting> meetingList = null;

        try {
            FileInputStream fileInput = context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fileInput);
            meetingList = (List<Meeting>) ois.readObject();

            ois.close();

        } catch (ClassNotFoundException | IOException e) {
            meetingList = new ArrayList<>();
            e.printStackTrace();
        }

        return meetingList;
    }
}
