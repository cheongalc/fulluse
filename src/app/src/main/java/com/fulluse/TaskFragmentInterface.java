package com.fulluse;

import android.widget.RelativeLayout;

/**
 * Created by Al Cheong on 6/6/2017.
 */

public interface TaskFragmentInterface {
    void onTitleFragmentCreated(RelativeLayout layout);
    void onPriorityFragmentCreated(RelativeLayout layout);
    void onDueDateFragmentCreated(RelativeLayout layout);
    void onEndDateFragmentCreated(RelativeLayout layout);
}
