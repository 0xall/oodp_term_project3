package com.oodp.myaddressbook.calllog;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
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


public class CallLogFragment extends Fragment {

    private ListView callLogListView;
    private ArrayList<CallLogListItem> callLogList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_call_log, container, false);

        callLogListView = (ListView)view.findViewById(R.id.callLogList);
        callLogList = new ArrayList<CallLogListItem>();

        activity.getSupportActionBar()
                .setTitle(R.string.title_call_logs_list);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateListView();
    }

    public void updateListView() {

         // checking permissions
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), R.string.alert_request_call_log_permission, Toast.LENGTH_LONG).show();
            return;
        }

        // get call logs
        Cursor res = getActivity().getBaseContext().getContentResolver().query(
                CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER
        );

        CallLogListViewAdapter adapter = new CallLogListViewAdapter(getActivity());
        callLogList = adapter.getArray();

        res.moveToFirst();
        while(!res.isAfterLast()) {
            int callType = res.getInt(res.getColumnIndex(CallLog.Calls.TYPE));
            String phoneNumber = res.getString(res.getColumnIndex(CallLog.Calls.NUMBER));
            String duration = res.getString(res.getColumnIndex(CallLog.Calls.DURATION));
            long date = res.getLong(res.getColumnIndex(CallLog.Calls.DATE));
            adapter.addListItem(callType, phoneNumber, 0, date);

            res.moveToNext();
        }

        callLogListView.setAdapter(adapter);
        callLogListView.setOnItemClickListener(new OnCallLogItemClickListener());
    }

    /**
     * Interface definition for a callback to be invoked when a call log item has been clicked.
     */
    class OnCallLogItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:" + callLogList.get(position).getPhoneNumber()));
            startActivity(i);
        }
    }
}
