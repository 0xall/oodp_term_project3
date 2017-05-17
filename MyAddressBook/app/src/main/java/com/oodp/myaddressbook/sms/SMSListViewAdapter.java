package com.oodp.myaddressbook.sms;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oodp.myaddressbook.R;
import com.oodp.myaddressbook.contacts.Contact;
import com.oodp.myaddressbook.contacts.ContactsDBHelper;
import com.oodp.myaddressbook.contacts.PhoneNumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SMSListViewAdapter extends BaseAdapter {

    private ArrayList<SMSListItem> smsListItemList;
    private Context context;
    private LayoutInflater inflater;

    public SMSListViewAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        smsListItemList = new ArrayList<SMSListItem>();
    }

    @Override
    public int getCount() {
        return smsListItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return smsListItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SMSListItem item = (SMSListItem) getItem(position);
        convertView = inflater.inflate(R.layout.sms_view_item, parent, false);

        // get views in the layout
        TextView smsAddressView = (TextView) convertView.findViewById(R.id.sms_address);
        TextView smsReceivedTimeView = (TextView) convertView.findViewById(R.id.sms_received_time);
        TextView smsBodyView = (TextView) convertView.findViewById(R.id.sms_body);

        // set address
        ContactsDBHelper dbHelper = new ContactsDBHelper(context.getApplicationContext());

        String name = dbHelper.getNameByPhoneNumber(item.getPhoneNumber());
        if(name == null) smsAddressView.setText(item.getPhoneNumber());
        else smsAddressView.setText(name);

        dbHelper.close();

        // set received time
        // get the differences between current time and received time.
        // the view would show time following by passed time.
        long currentTimeDiff = (new Date()).getTime() -  item.getDate().getTimeInMillis();
        long dayPassed = TimeUnit.MILLISECONDS.toDays(currentTimeDiff);
        long hourPassed = TimeUnit.MILLISECONDS.toHours(currentTimeDiff) % 24;
        long minutePassed = TimeUnit.MILLISECONDS.toMinutes(currentTimeDiff) % 60;

        // if passed over 7 days, show entire date
        if(dayPassed > 7) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
            smsReceivedTimeView.setText(dateFormat.format(item.getDate().getTime()));
        }

        // if passed over 1 day,
        else if(dayPassed > 0) {
            smsReceivedTimeView.setText(dayPassed + " " + context.getResources().getString(R.string.call_log_item_time_day_passed));
        }

        // if passed over 1 hour
        else if(hourPassed > 0) {
            smsReceivedTimeView.setText(hourPassed + " " + context.getResources().getString(R.string.call_log_item_time_hour_passed));
        }

        // if passed over 1 minute
        else if(minutePassed > 0) {
            smsReceivedTimeView.setText(minutePassed + " " + context.getResources().getString(R.string.call_log_item_time_minute_passed));
        }

        // if passed less than 1 minute
        else {
            smsReceivedTimeView.setText(context.getResources().getString(R.string.call_log_item_time_passed_less_than_1min));
        }

        // set sms body
        smsBodyView.setText(item.getBody());

        return convertView;
    }

    public void addListItem(String address, Calendar receivedTime, String body, int type, boolean read) {
        SMSListItem item = new SMSListItem(type, read, receivedTime, address, body);
        smsListItemList.add(item);
    }

    public ArrayList<SMSListItem> getArray() {
        return smsListItemList;
    }
}
