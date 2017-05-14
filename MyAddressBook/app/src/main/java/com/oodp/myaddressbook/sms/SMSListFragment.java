package com.oodp.myaddressbook.sms;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oodp.myaddressbook.R;

public class SMSListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        View view = inflater.inflate(R.layout.fragment_smslist, container, false);

        activity.getSupportActionBar().setTitle(R.string.title_sms_list);

        return view;
    }
}
