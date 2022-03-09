package com.fulluse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class DashboardFragment extends Fragment {

    private RelativeLayout mRelativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard, container, false);
        mRelativeLayout = (RelativeLayout) viewGroup.findViewById(R.id.rl_tabFragment2);
        return viewGroup;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            RootFragmentInterface vi = (RootFragmentInterface) getActivity();
            vi.onDashboardFragmentCreated(mRelativeLayout);
        } catch (ClassCastException e) {
            Log.e("ERROR", String.valueOf(getActivity()) + " must implement RootFragmentInterface");
        }
    }
}
