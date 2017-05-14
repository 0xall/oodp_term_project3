package com.oodp.myaddressbook.calllog;

import android.content.Context;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oodp.myaddressbook.R;
import com.oodp.myaddressbook.contacts.Contact;
import com.oodp.myaddressbook.contacts.ContactsDBHelper;
import com.oodp.myaddressbook.contacts.PhoneNumber;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CallLogListViewAdapter extends BaseAdapter {

    private ArrayList<CallLogListItem> callLogItemList;
    private Context context;
    private LayoutInflater inflater;

    public CallLogListViewAdapter(Context context) {
        callLogItemList = new ArrayList<CallLogListItem>();
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return callLogItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return callLogItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CallLogListItem item = (CallLogListItem)getItem(position);

        convertView = inflater.inflate(R.layout.call_log_view_item, parent, false);

        // get views in the layout
        ImageView iconImageView = (ImageView)convertView.findViewById(R.id.call_log_icon);
        TextView addressText = (TextView)convertView.findViewById(R.id.call_log_address);
        TextView dateText = (TextView)convertView.findViewById(R.id.call_log_received_time);

        // set the icon of the item
        // if case the user called, show nothing.
        switch(item.getCallType()) {
            case CallLog.Calls.OUTGOING_TYPE :
                iconImageView.setImageResource(R.drawable.ic_phone_call_24dp);
                break;

            case CallLog.Calls.MISSED_TYPE :
                iconImageView.setImageResource(R.drawable.ic_phone_missed_24dp);
                break;
        }

        // set the address text
        // if the phone number is registered in the contacts database, show the name
        // if not, show only phone number.
        ContactsDBHelper dbHelper = new ContactsDBHelper(context.getApplicationContext());
        ArrayList<Contact> contacts =
                dbHelper.getConditionContacts(ContactsDBHelper.CONTACTS_COLUMN_PHONE_NUMBERS + "= '" + item.getPhoneNumber() + "'");

        try {
            if (contacts.size() == 0)
                addressText.setText((new PhoneNumber(item.getPhoneNumber())).getPhoneNumber());
            else addressText.setText(contacts.get(0).getName());
        } catch(PhoneNumber.WrongSyntaxException e) {
            addressText.setText(contacts.get(0).getName());
        }

        dbHelper.close();

        // get the differences between current time and received time.
        // the view would show time following by passed time.
        long currentTimeDiff = (new Date()).getTime() -  item.getDate().getTimeInMillis();
        long dayPassed = TimeUnit.MILLISECONDS.toDays(currentTimeDiff);
        long hourPassed = TimeUnit.MILLISECONDS.toHours(currentTimeDiff) % 24;
        long minutePassed = TimeUnit.MILLISECONDS.toMinutes(currentTimeDiff) % 60;

        // if passed over 7 days, show entire date
        if(dayPassed > 7) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
            dateText.setText(dateFormat.format(item.getDate().getTime()));
        }

        // if passed over 1 day,
        else if(dayPassed > 0) {
            dateText.setText(dayPassed + " " + context.getResources().getString(R.string.call_log_item_time_day_passed));
        }

        // if passed over 1 hour
        else if(hourPassed > 0) {
            dateText.setText(hourPassed + " " + context.getResources().getString(R.string.call_log_item_time_hour_passed));
        }

        // if passed over 1 minute
        else if(minutePassed > 0) {
            dateText.setText(minutePassed + " " + context.getResources().getString(R.string.call_log_item_time_minute_passed));
        }

        // if passed less than 1 minute
        else {
            dateText.setText(context.getResources().getString(R.string.call_log_item_time_passed_less_than_1min));
        }



        return convertView;
    }

    public void addListItem(CallLogListItem item) {
        callLogItemList.add(item);
    }

    public void addListItem(int callType, String phoneNumber, int duration, long time) {
        callLogItemList.add(new CallLogListItem(callType, phoneNumber, duration, time));
    }

    public void addListItem(int callType, String phoneNumber, int duration, Calendar time) {
        callLogItemList.add(new CallLogListItem(callType, phoneNumber, duration, time));
    }

    public ArrayList<CallLogListItem> getArray() {
        return callLogItemList;
    }
}
