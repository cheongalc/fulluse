package com.fulluse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Al Cheong on 6/10/2017.
 */

public class EventFragment extends Fragment {
    private RelativeLayout rootLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_events, container, false);
        rootLayout = (RelativeLayout) rootView;
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            RootFragmentInterface rfi = (RootFragmentInterface) getActivity();
            rfi.onEventFragmentCreated(rootLayout);
        } catch (ClassCastException e) {
            Log.e("ERROR", String.valueOf(getActivity()) + " must implement RootFragmentInterface");
        }

    }
}
