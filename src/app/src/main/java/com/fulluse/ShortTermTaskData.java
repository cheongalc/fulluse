package com.fulluse;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Al Cheong on 3/22/2017.
 */

public class ShortTermTaskData {
    private String todoName;
    private String truncatedName;
    private String todoPriority;
    private String todoDueDate;
    private String[] tags = new String[]{};
    private boolean isBold;
    private boolean isOverDue;

    public ShortTermTaskData(String todoName, String todoPriority, String todoDueDate, String encodedTagStr, boolean isBold, boolean isOverDue) {
        this.todoName = todoName;
        this.truncatedName = todoName;
        this.todoPriority = todoPriority;
        this.todoDueDate = todoDueDate;
        this.isBold = isBold;
        this.isOverDue = isOverDue;

        if (!Arrays.equals(encodedTagStr.split(":"), new String[]{})) {
            this.tags = encodedTagStr.split(":");
        }

        Log.d("MAINACTIVITY", "encoded tag: " + Arrays.toString(encodedTagStr.split(":")));
        Log.d("MAINACTIVITY", "an empty array: " + Arrays.toString(new String[]{}));
        Log.d("MAINACTIVITY", "this.tags = " + Arrays.toString(this.tags));
    }

    public String getTaskName() {return todoName;}
    public String getTaskPriority() {return todoPriority;}
    public String getTaskDueDate() {return todoDueDate;}
    public String[] getTags() {return tags;}
    public String getTruncatedName() {return truncatedName;}
    public boolean getIsBold() {return isBold;}
    public boolean getIsOverDue() {return isOverDue;}
    public void setIsBold() {
        this.isBold = true;
    }
    public void setIsOverDue() {this.isOverDue = true;}
    public void setTruncatedName(String truncatedName) {this.truncatedName = truncatedName;}
}
