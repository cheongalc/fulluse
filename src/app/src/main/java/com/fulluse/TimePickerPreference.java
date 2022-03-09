package com.fulluse;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by Al Cheong on 8/2/2017.
 */

public class TimePickerPreference extends DialogPreference {

    private int lastHour = 12;
    private int lastMinute = 0;
    private TimePicker picker = null;

    public static int getHour(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]);
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[1]));
    }

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("SET");
        setNegativeButtonText("CANCEL");
    }

    /*
        Creates the content view for the dialog (if a custom content view is required).
        By default, it inflates the dialog layout resource if it is set.

        Returns: the content view for the dialog
     */

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return picker;
    }

    /*
        Binds views in the content View of the dialog to data.

        Make sure to call through to the superclass implementation.

        If you override this method you must call through to the superclass implementation.

        Returns View: The content View of the dialog, if it is custom.
     */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setHour(lastHour);
            picker.setMinute(lastMinute);
        } else {
            picker.setCurrentHour(lastHour);
            picker.setCurrentMinute(lastMinute);
        }
        picker.setIs24HourView(true);
    }

    /*
        Called when the dialog is dismissed and should be used to save data to the SharedPreferences.

        Returns boolean: Whether the positive button was clicked (true),
                         or the negative button was clicked
                         or the dialog was canceled (false).
     */

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                lastHour = picker.getHour();
                lastMinute = picker.getMinute();
            } else {
                lastHour = picker.getCurrentHour();
                lastMinute = picker.getCurrentMinute();
            }

            String time = String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);

            if (callChangeListener(time)) { // Call this method after the user changes the preference, but before the internal state is set. This allows the client to ignore the user value.
                persistString(time); // Attempts to persist a String if this Preference is persistent.
            }
        }
    }

    /*
        Called when a Preference is being inflated and the default value attribute needs to be read.

        Since different Preference types have different value types,
        the subclass should get and return the default value which will be its value type.

        For example, if the value type is String, the body of the method would proxy to getString(int).

        Param TypedArray: The set of attributes.
        Param int: The index of the default value attribute.
     */

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /*
        Implement this to set the initial value of the Preference.

        If restorePersistedValue is true, you should restore the Preference value from the SharedPreferences.
        If restorePersistedValue is false, you should set the Preference value to defaultValue that is given
        (and possibly store to SharedPreferences if shouldPersist() is true).

        In case of using PreferenceDataStore, the restorePersistedValue is always true.
        But the default value (if provided) is set.

        This may not always be called. One example is if it should not persist but there is no default value given.

        Parameters
        restorePersistedValue	boolean: True to restore the persisted value; false to use the given defaultValue.
        defaultValue	        Object: The default value for this Preference. Only use this if restorePersistedValue is false.
     */

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time = null;

        if (restorePersistedValue) {
            if (defaultValue == null) {
                time = getPersistedString("09:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        lastHour = getHour(time);
        lastMinute = getMinute(time);
    }
}
