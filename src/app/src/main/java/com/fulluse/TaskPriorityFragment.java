package com.fulluse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Al Cheong on 6/6/2017.
 */

public class TaskPriorityFragment extends Fragment {

    private RelativeLayout rootLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_task_priority, container, false);
        rootLayout = (RelativeLayout) rootView;
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            TaskFragmentInterface tfi = (TaskFragmentInterface) getActivity();
            tfi.onPriorityFragmentCreated(rootLayout);
        } catch (ClassCastException e) {
            Log.e("ERROR", String.valueOf(getActivity()) + " must implement TaskFragmentInterface");
        }
    }
}
