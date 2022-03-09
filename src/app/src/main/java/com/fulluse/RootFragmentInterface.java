package com.fulluse;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public interface RootFragmentInterface {
    void onMainFragmentCreated(RelativeLayout layout);
    void onEventFragmentCreated(RelativeLayout layout);
    void onDashboardFragmentCreated(RelativeLayout layout);
}