package com.fulluse;

import android.widget.RelativeLayout;

/**
 * Created by Al Cheong on 6/11/2017.
 */

public interface NormalEventFragmentInterface {
    void onIntroFragmentCreated(RelativeLayout rootLayout);
    void onTitleFragmentCreated(RelativeLayout layout);
    void onStartDateFragmentCreated(RelativeLayout layout);
    void onEndDateFragmentCreated(RelativeLayout layout);
}
