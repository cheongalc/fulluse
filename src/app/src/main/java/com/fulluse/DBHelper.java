package com.fulluse;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Al Cheong on 3/22/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "mydb.db";
    private static final int VERSION = 2;

    public static final String TABLE_SHORT_TERM_TASK = "shortTermTasks";
    public static final String SHORT_TERM_TASK_ID = "_id";
    public static final String SHORT_TERM_TASK_NAME = "shortTermTaskName";
    public static final String SHORT_TERM_TASK_PRIORITY = "shortTermTaskPriority";
    public static final String SHORT_TERM_TASK_DUEDAY = "shortTermTaskDueDay";
    public static final String SHORT_TERM_TASK_DUEMTH = "shortTermTaskDueMth";
    public static final String SHORT_TERM_TASK_DUEYR = "shortTermTaskDueYr";
    public static final String SHORT_TERM_TASK_TAGSTR = "shortTermTaskTagstr";

    public static final String TABLE_LONG_TERM_TASK = "longTermTasks";
    public static final String LONG_TERM_TASK_ID = "_id";
    public static final String LONG_TERM_TASK_NAME = "longTermTaskName";
    public static final String LONG_TERM_TASK_PRIORITY = "longTermTaskPriority";
    public static final String LONG_TERM_TASK_ENDDAY = "longTermTaskEndDay";
    public static final String LONG_TERM_TASK_ENDMTH = "longTermTaskEndMth";
    public static final String LONG_TERM_TASK_ENDYR = "longTermTaskEndYr";

    public static final String TABLE_EVENTS = "events";
    public static final String EVENT_ID = "_id";
    public static final String EVENT_START_DAY = "eventStartDay";
    public static final String EVENT_START_MTH = "eventStartMth";
    public static final String EVENT_START_YR = "eventStartYr";
    public static final String EVENT_END_DAY = "eventEndDay";
    public static final String EVENT_END_MTH = "eventEndMth";
    public static final String EVENT_END_YR = "eventEndYr";
    public static final String EVENT_NAME = "eventName";
    public static final String EVENT_START_TIME = "eventStartTime";
    public static final String EVENT_END_TIME = "eventEndTime";

    private static final String LOG_TAG = "DBHELPER";

    private SQLiteDatabase DB;

    public DBHelper (Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryTodoTable = "CREATE TABLE IF NOT EXISTS " + TABLE_SHORT_TERM_TASK +
                " (" +
                SHORT_TERM_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SHORT_TERM_TASK_NAME + " TEXT NOT NULL, " +
                SHORT_TERM_TASK_PRIORITY + " INTEGER NOT NULL, " +
                SHORT_TERM_TASK_DUEDAY + " INTEGER NOT NULL, " +
                SHORT_TERM_TASK_DUEMTH + " INTEGER NOT NULL, " +
                SHORT_TERM_TASK_DUEYR + " INTEGER NOT NULL, " +
                SHORT_TERM_TASK_TAGSTR + " TEXT NOT NULL " +
                ")";
        sqLiteDatabase.execSQL(queryTodoTable);

        String queryLongTermTaskTable = "CREATE TABLE IF NOT EXISTS " + TABLE_LONG_TERM_TASK +
                " (" +
                LONG_TERM_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LONG_TERM_TASK_NAME + " TEXT NOT NULL, " +
                LONG_TERM_TASK_PRIORITY + " INTEGER NOT NULL, " +
                LONG_TERM_TASK_ENDDAY + " INTEGER NOT NULL, " +
                LONG_TERM_TASK_ENDMTH + " INTEGER NOT NULL, " +
                LONG_TERM_TASK_ENDYR + " INTEGER NOT NULL " +
                ")";
        sqLiteDatabase.execSQL(queryLongTermTaskTable);

        String queryFreeTimeDataTable = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS +
                " (" +
                EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVENT_NAME + " TEXT NOT NULL, " +
                EVENT_START_DAY + " INTEGER NOT NULL, " +
                EVENT_START_MTH + " INTEGER NOT NULL, " +
                EVENT_START_YR + " INTEGER NOT NULL, " +
                EVENT_END_DAY + " INTEGER NOT NULL, " +
                EVENT_END_MTH + " INTEGER NOT NULL, " +
                EVENT_END_YR + " INTEGER NOT NULL, " +
                EVENT_START_TIME + " TEXT NOT NULL, " +
                EVENT_END_TIME + " TEXT NOT NULL " +
                ")";
        sqLiteDatabase.execSQL(queryFreeTimeDataTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        updateColumns(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    private void updateColumns(SQLiteDatabase sqLiteDatabase) {
        String[] queries = {
                "ALTER TABLE " + TABLE_SHORT_TERM_TASK + " ADD COLUMN " + SHORT_TERM_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT",
                "ALTER TABLE " + TABLE_SHORT_TERM_TASK + " ADD COLUMN " + SHORT_TERM_TASK_NAME + " TEXT NOT NULL",
                "ALTER TABLE " + TABLE_SHORT_TERM_TASK + " ADD COLUMN " + SHORT_TERM_TASK_PRIORITY + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_SHORT_TERM_TASK + " ADD COLUMN " + SHORT_TERM_TASK_DUEDAY + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_SHORT_TERM_TASK + " ADD COLUMN " + SHORT_TERM_TASK_DUEMTH + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_SHORT_TERM_TASK + " ADD COLUMN " + SHORT_TERM_TASK_DUEYR + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_SHORT_TERM_TASK + " ADD COLUMN " + SHORT_TERM_TASK_TAGSTR + " TEXT NOT NULL DEFAULT ''",

                "ALTER TABLE " + TABLE_LONG_TERM_TASK + " ADD COLUMN " + LONG_TERM_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT",
                "ALTER TABLE " + TABLE_LONG_TERM_TASK + " ADD COLUMN " + LONG_TERM_TASK_NAME + " TEXT NOT NULL",
                "ALTER TABLE " + TABLE_LONG_TERM_TASK + " ADD COLUMN " + LONG_TERM_TASK_PRIORITY + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_LONG_TERM_TASK + " ADD COLUMN " + LONG_TERM_TASK_ENDDAY + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_LONG_TERM_TASK + " ADD COLUMN " + LONG_TERM_TASK_ENDMTH + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_LONG_TERM_TASK + " ADD COLUMN " + LONG_TERM_TASK_ENDYR + " INTEGER NOT NULL",

                "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT",
                "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EVENT_NAME + " TEXT NOT NULL",
                "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EVENT_START_DAY + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EVENT_START_MTH + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EVENT_START_YR + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EVENT_END_DAY + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EVENT_END_MTH + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EVENT_END_YR + " INTEGER NOT NULL",
                "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EVENT_START_TIME + " TEXT NOT NULL",
                "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + EVENT_END_TIME + " TEXT NOT NULL",
        };

        for (String query : queries) {
            try {
                sqLiteDatabase.execSQL(query);
            } catch (SQLiteException e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    public SQLiteDatabase openDB() {
        DB = getWritableDatabase();
        return DB;
    }

    public void closeDB() {
        if (DB != null && DB.isOpen()) {
            DB.close();
        }
    }

    public long insertShortTermTask(String taskName, int taskPriority, int dueDay, int dueMth, int dueYr, String encodedTagStr) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(SHORT_TERM_TASK_NAME, taskName);
        contentValues.put(SHORT_TERM_TASK_PRIORITY, taskPriority);
        contentValues.put(SHORT_TERM_TASK_DUEDAY, dueDay);
        contentValues.put(SHORT_TERM_TASK_DUEMTH, dueMth);
        contentValues.put(SHORT_TERM_TASK_DUEYR, dueYr);
        contentValues.put(SHORT_TERM_TASK_TAGSTR, encodedTagStr);

        return DB.insertWithOnConflict(TABLE_SHORT_TERM_TASK, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public long insertLongTermTask(String taskName, int taskPriority, int endDay, int endMth, int endYr) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LONG_TERM_TASK_NAME, taskName);
        contentValues.put(LONG_TERM_TASK_PRIORITY, taskPriority);
        contentValues.put(LONG_TERM_TASK_ENDDAY, endDay);
        contentValues.put(LONG_TERM_TASK_ENDMTH, endMth);
        contentValues.put(LONG_TERM_TASK_ENDYR, endYr);

        return DB.insertWithOnConflict(TABLE_LONG_TERM_TASK, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public long insertEvent(String eventName,
                            int eventStartDay,
                            int eventStartMth,
                            int eventStartYr,
                            int eventEndDay,
                            int eventEndMth,
                            int eventEndYr,
                            String eventStartTime,
                            String eventEndTime) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(EVENT_NAME, eventName);
        contentValues.put(EVENT_START_DAY, eventStartDay);
        contentValues.put(EVENT_START_MTH, eventStartMth);
        contentValues.put(EVENT_START_YR, eventStartYr);
        contentValues.put(EVENT_END_DAY, eventEndDay);
        contentValues.put(EVENT_END_MTH, eventEndMth);
        contentValues.put(EVENT_END_YR, eventEndYr);
        contentValues.put(EVENT_START_TIME, eventStartTime);
        contentValues.put(EVENT_END_TIME, eventEndTime);

        return DB.insertWithOnConflict(TABLE_EVENTS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public long editSTT(int originalID, String newName, int newPriority, int newDay, int newMth, int newYr, String encodedTagStr) {
        ContentValues contentValues = new ContentValues();
        if (newName != null) contentValues.put(SHORT_TERM_TASK_NAME, newName);
        if (newPriority > -1 && newPriority < 4) {
            contentValues.put(SHORT_TERM_TASK_PRIORITY, newPriority);
        }
        if (newDay > -1) contentValues.put(SHORT_TERM_TASK_DUEDAY, newDay);
        if (newMth > -1) contentValues.put(SHORT_TERM_TASK_DUEMTH, newMth);
        if (newYr > -1) contentValues.put(SHORT_TERM_TASK_DUEYR, newYr);
        if (!encodedTagStr.equals("`")) contentValues.put(SHORT_TERM_TASK_TAGSTR, encodedTagStr);

        String whereClause = SHORT_TERM_TASK_ID + " =?";
        String[] whereArgs = new String[]{String.valueOf(originalID)};

        return DB.update(TABLE_SHORT_TERM_TASK, contentValues, whereClause, whereArgs);
    }

    public long editLTT(int originalID, String newName, int newPriority, int newDay, int newMth, int newYr) {
        ContentValues contentValues = new ContentValues();

        if (newName != null) contentValues.put(LONG_TERM_TASK_NAME, newName);
        if (newPriority > -1 && newPriority < 4) {
            contentValues.put(LONG_TERM_TASK_PRIORITY, newPriority);
        }
        if (newDay > -1) contentValues.put(LONG_TERM_TASK_ENDDAY, newDay);
        if (newMth > -1) contentValues.put(LONG_TERM_TASK_ENDMTH, newMth);
        if (newYr > -1) contentValues.put(LONG_TERM_TASK_ENDYR, newYr);

        String whereClause = LONG_TERM_TASK_ID + " =?";
        String[] whereArgs = new String[]{String.valueOf(originalID)};

        return DB.update(TABLE_LONG_TERM_TASK, contentValues, whereClause, whereArgs);
    }

    public long editEvent(int originalID, String newName,
                          int startYear, int startMonth, int startDay, String startTime,
                          int endYear, int endMonth, int endDay, String endTime) {
        ContentValues contentValues = new ContentValues();

        if (newName != null) contentValues.put(EVENT_NAME, newName);
        contentValues.put(EVENT_START_YR, startYear);
        contentValues.put(EVENT_START_MTH, startMonth);
        contentValues.put(EVENT_START_DAY, startDay);
        contentValues.put(EVENT_START_TIME, startTime);
        contentValues.put(EVENT_END_YR, endYear);
        contentValues.put(EVENT_END_MTH, endMonth);
        contentValues.put(EVENT_END_DAY, endDay);
        contentValues.put(EVENT_END_TIME, endTime);

        String whereClause = EVENT_ID + " =?";
        String[] whereArgs = {String.valueOf(originalID)};

        return DB.update(TABLE_EVENTS, contentValues, whereClause, whereArgs);
    }

    public long deleteFromSTT(String name) {
        String whereClause = SHORT_TERM_TASK_NAME + " = ?";
        return DB.delete(TABLE_SHORT_TERM_TASK, whereClause, new String[]{name});
    }

    public long deleteFromLTT(String name) {
        String whereClause = LONG_TERM_TASK_NAME + " = ?";
        return DB.delete(TABLE_LONG_TERM_TASK, whereClause, new String[]{name});
    }

    public long deleteEvent(String name) {
        String whereClause = EVENT_NAME + " = ?";
        return DB.delete(TABLE_EVENTS, whereClause, new String[]{name});
    }


}
