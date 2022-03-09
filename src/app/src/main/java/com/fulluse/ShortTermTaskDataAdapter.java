package com.fulluse;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Al Cheong on 3/22/2017.
 */

public class ShortTermTaskDataAdapter extends ArrayAdapter<ShortTermTaskData> {
    private Context context;
    private List<ShortTermTaskData> todoData;
    private int CHARACTER_LIMIT = 20;

    public ShortTermTaskDataAdapter(Context context, int resource, ArrayList<ShortTermTaskData> objects) {
        super(context, resource, objects);

        this.context = context;
        this.todoData = objects;
    }

    public String getSTTDWithTruncatedName(String truncatedName) {
        for (int i = 0; i < this.todoData.size(); i++) {
            ShortTermTaskData tD = this.todoData.get(i);
            if (tD.getTruncatedName().equals(truncatedName)) return tD.getTaskName();
        }
        return null;
    }

//  Can possibly use these methods when data optimization occurs. updateUI() only in onStart and onCreate
//  Use notifyDataSetChanged() and more to represent changes. This way I can animate.

//    public void removeSTTDWithName(String taskName) {
//        for (int i = 0; i < this.todoData.size(); i++) {
//            ShortTermTaskData tD = this.todoData.get(i);
//            if (tD.getTaskName().equals(taskName)) {
//                this.todoData.remove(i);
//                notifyDataSetChanged();
//                break;
//            }
//        }
//    }
//    public void setTaskData(ArrayList<ShortTermTaskData> arrayList) {
//        this.todoData = arrayList;
//        notifyDataSetChanged();
//    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ShortTermTaskData tD = todoData.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_short_term_task, null);

        TextView todoName = (TextView) view.findViewById(R.id.tv_taskName);
        todoName.setText(tD.getTaskName());
        TextView todoPriority = (TextView) view.findViewById(R.id.tv_taskPriority);
        todoPriority.setText(tD.getTaskPriority());
        TextView todoDueDate = (TextView) view.findViewById(R.id.tv_taskDueDate);
        todoDueDate.setText(tD.getTaskDueDate());

        if (tD.getIsBold()) {
            todoName.setTypeface(Typeface.DEFAULT_BOLD);
            todoPriority.setTypeface(Typeface.DEFAULT_BOLD);
            todoDueDate.setTypeface(Typeface.DEFAULT_BOLD);
        }

        if (tD.getTaskName().length() > CHARACTER_LIMIT) {
            String newName = tD.getTaskName().substring(0, Math.min(tD.getTaskName().length(), CHARACTER_LIMIT));
            newName = newName + "...";
            tD.setTruncatedName(newName);
            todoName.setText(newName);
        }
        if (tD.getIsOverDue()) {
            todoName.setTypeface(Typeface.DEFAULT_BOLD);
            todoName.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.overDueTask));
            todoPriority.setTypeface(Typeface.DEFAULT_BOLD);
            todoPriority.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.overDueTask));
            todoDueDate.setTypeface(Typeface.DEFAULT_BOLD);
            todoDueDate.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.overDueTask));
        }

        String[] taskTags = tD.getTags();
        int lim = (taskTags.length < 2) ? taskTags.length : 2;

        LinearLayout ll_tags = (LinearLayout) view.findViewById(R.id.ll_tags);
        for (int i = 0; i < lim; i++) {
            View tag = inflater.inflate(R.layout.item_tag_min, null);
            TextView tv_tagName = (TextView) tag.findViewById(R.id.tv_tagName);
            tv_tagName.setText(taskTags[i]);
            if (!taskTags[i].equals("")) {
                if (tag.getParent() != null) ((ViewGroup) tag.getParent()).removeView(tag);
                ll_tags.addView(tag);
            }
        }

        return view;
    }


}
