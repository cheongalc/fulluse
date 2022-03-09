package com.fulluse;

import android.widget.RelativeLayout;

/**
 * Created by Al Cheong on 6/8/2017.
 */

public interface MorningReviewFragmentInterface {
    void onIntroFragmentCreated(RelativeLayout layout);
    void onSTTFragmentCreated(RelativeLayout layout);
    void onLTTFragmentCreated(RelativeLayout layout);
    void onEventsFragmentCreated(RelativeLayout layout);
    void onTreeAnalysisFragmentCreated(RelativeLayout layout);
}
