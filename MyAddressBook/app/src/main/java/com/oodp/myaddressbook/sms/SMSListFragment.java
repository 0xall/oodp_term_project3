package com.oodp.myaddressbook.sms;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.oodp.myaddressbook.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

public class SMSListFragment extends Fragment {

    private ListView smsListView;
    private ArrayList<SMSListItem> smsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        View view = inflater.inflate(R.layout.fragment_smslist, container, false);

        activity.getSupportActionBar().setTitle(R.string.title_sms_list);

        // get views
        smsListView = (ListView) view.findViewById(R.id.smsList);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateListView();
    }

    public void updateListView() {

        LinkedList<String> printedNumbers = new LinkedList<String>();
        SMSListViewAdapter adapter = new SMSListViewAdapter(getActivity());

        // checking permissions
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), R.string.alert_request_sms_permission, Toast.LENGTH_LONG).show();
            return;
        }

        Cursor res = getActivity().getContentResolver().query(Uri.parse("content://sms"),
                null, null, null, SMSListItem.SMS_DEFAULT_ORDER);

        res.moveToFirst();
        while(!res.isAfterLast()) {
            String address = res.getString(res.getColumnIndex(SMSListItem.ADDRESS));
            long receivedTime = res.getLong(res.getColumnIndex(SMSListItem.DATE));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(receivedTime);
            String body = res.getString(res.getColumnIndex(SMSListItem.BODY));
            int type = res.getInt(res.getColumnIndex(SMSListItem.TYPE));
            boolean read = ((res.getInt(res.getColumnIndex(SMSListItem.READ)) == 0) ? false : true);

            if(printedNumbers.contains(address)) {
                res.moveToNext();
                continue;
            }

            adapter.addListItem(address, c, body, type, read);
            printedNumbers.add(address);
            res.moveToNext();
        }

        smsListView.setAdapter(adapter);
        smsList = adapter.getArray();
        smsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SMSListItem item = smsList.get(position);
                Intent i = new Intent(getActivity(), SMSChatActivity.class);
                i.putExtra(SMSChatActivity.EXTRA_PHONE_NUMBER, item.getPhoneNumber());
                startActivity(i);
            }
        });
    }
}
