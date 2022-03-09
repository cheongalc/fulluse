package com.fulluse;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Al Cheong on 3/31/2017.
 */

public class LongTermTaskDataAdapter extends ArrayAdapter<LongTermTaskData> {
    private Context context;
    private List<LongTermTaskData> taskData;
    private int CHARACTER_LIMIT = 20;

    public LongTermTaskDataAdapter(Context context, int resource, ArrayList<LongTermTaskData> objects) {
        super(context, resource, objects);

        this.context = context;
        this.taskData = objects;
    }

    public String getLTTDWithTruncatedName(String truncatedName) {
        for (int i = 0; i < this.taskData.size(); i++) {
            LongTermTaskData tD = this.taskData.get(i);
            if (tD.getTruncatedName().equals(truncatedName)) return tD.getTaskName();
        }
        return null;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        LongTermTaskData tD = taskData.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_long_term_task, null);

        TextView todoName = (TextView) view.findViewById(R.id.tv_taskName);
        todoName.setText(tD.getTaskName());
        if (tD.getTaskName().length() > CHARACTER_LIMIT) {
            String newName = tD.getTaskName().substring(0, Math.min(tD.getTaskName().length(), CHARACTER_LIMIT));
            newName = newName + "...";
            tD.setTruncatedName(newName);
            todoName.setText(newName);
        }
        Log.d("LTT", "displayedName: " + String.valueOf(todoName.getText()));


        TextView todoPriority = (TextView) view.findViewById(R.id.tv_taskPriority);
        todoPriority.setText(tD.getTaskPriority());

        TextView todoDueDate = (TextView) view.findViewById(R.id.tv_taskEndDate);
        todoDueDate.setText(tD.getTaskEndDate());

        return view;
    }
}
