package com.fulluse;

/**
 * Created by Al Cheong on 3/31/2017.
 */

public class LongTermTaskData {
    private String taskName;
    private String truncatedName;
    private String taskPriority;
    private String taskEndDate;

    public LongTermTaskData (String taskName, String taskPriority, String taskEndDate) {
        this.taskName = taskName;
        this.truncatedName = taskName;
        this.taskPriority = taskPriority;
        this.taskEndDate = taskEndDate;
    }

    public String getTaskName() {return taskName;}
    public String getTaskPriority() {return taskPriority;}
    public String getTaskEndDate() {return taskEndDate;}
    public String getTruncatedName() {return truncatedName;}
    public void setTruncatedName(String truncatedName) {this.truncatedName = truncatedName;}

}
