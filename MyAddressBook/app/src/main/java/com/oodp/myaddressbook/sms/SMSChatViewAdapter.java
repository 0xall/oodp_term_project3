package com.oodp.myaddressbook.sms;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oodp.myaddressbook.R;
import com.oodp.myaddressbook.contacts.ContactsDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class SMSChatViewAdapter extends BaseAdapter {
    private ArrayList<SMSListItem> smsListItemList;
    private Context context;
    private LayoutInflater inflater;
    private String name;

    public SMSChatViewAdapter(Context context, String name) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        smsListItemList = new ArrayList<SMSListItem>();
        this.name = name;
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
        convertView = inflater.inflate(R.layout.sms_chat_item, parent, false);

        // get views in the layout
        LinearLayout chatBox = (LinearLayout) convertView.findViewById(R.id.sms_chat_box);
        TextView nameView = (TextView) convertView.findViewById(R.id.sms_chat_name);
        TextView receivedTimeView = (TextView) convertView.findViewById(R.id.sms_chat_received_time);
        TextView bodyView = (TextView) convertView.findViewById(R.id.sms_chat_body);

        // sets the gravity of the views
        int gravity = Gravity.CENTER_VERTICAL;
        if(item.getType() == SMSListItem.MESSAGE_TYPE_INBOX) gravity |= Gravity.LEFT;
        else if(item.getType() == SMSListItem.MESSAGE_TYPE_SENT) gravity |= Gravity.RIGHT;

        nameView.setGravity(gravity);
        receivedTimeView.setGravity(gravity);
        bodyView.setGravity(gravity);

        // set address
        if(item.getType() == SMSListItem.MESSAGE_TYPE_INBOX) {
            nameView.setText(name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                chatBox.setBackgroundColor(context.getResources().getColor(R.color.smsChatReceive, context.getTheme()));
            }

            else{
                chatBox.setBackgroundColor(context.getResources().getColor(R.color.smsChatReceive));
            }
        }

        else {
            nameView.setText("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                chatBox.setBackgroundColor(context.getResources().getColor(R.color.smsChatSend, context.getTheme()));
            }

            else{
                chatBox.setBackgroundColor(context.getResources().getColor(R.color.smsChatSend));
            }
        }

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
            receivedTimeView.setText(dateFormat.format(item.getDate().getTime()));
        }

        // if passed over 1 day,
        else if(dayPassed > 0) {
            receivedTimeView.setText(dayPassed + " " + context.getResources().getString(R.string.call_log_item_time_day_passed));
        }

        // if passed over 1 hour
        else if(hourPassed > 0) {
            receivedTimeView.setText(hourPassed + " " + context.getResources().getString(R.string.call_log_item_time_hour_passed));
        }

        // if passed over 1 minute
        else if(minutePassed > 0) {
            receivedTimeView.setText(minutePassed + " " + context.getResources().getString(R.string.call_log_item_time_minute_passed));
        }

        // if passed less than 1 minute
        else {
            receivedTimeView.setText(context.getResources().getString(R.string.call_log_item_time_passed_less_than_1min));
        }

        // set sms body
        bodyView.setText(item.getBody());

        convertView.setClickable(false);
        convertView.setEnabled(false);

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
