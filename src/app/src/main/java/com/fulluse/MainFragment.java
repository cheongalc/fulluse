package com.fulluse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MainFragment extends Fragment {

    public RelativeLayout mRelativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        //Setting date
        Calendar c = Calendar.getInstance();
        int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day_of_month = c.get(Calendar.DAY_OF_MONTH);
        String weekRange = Integer.toString(day_of_month) + "/" + Integer.toString(month) + "/" + Integer.toString(year);

        TextView date = (TextView) viewGroup.findViewById(R.id.tv_date);
        date.setText(weekRange);

        mRelativeLayout = (RelativeLayout) viewGroup.findViewById(R.id.rl_tabFragment1);

        return viewGroup;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            RootFragmentInterface vi = (RootFragmentInterface) getActivity();
            vi.onMainFragmentCreated(mRelativeLayout);
        } catch (ClassCastException e) {
            Log.e("ERROR", String.valueOf(getActivity()) + " must implement RootFragmentInterface");
        }
    }
}
