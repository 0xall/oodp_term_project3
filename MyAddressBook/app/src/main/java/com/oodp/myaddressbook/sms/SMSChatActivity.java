package com.oodp.myaddressbook.sms;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.oodp.myaddressbook.R;
import com.oodp.myaddressbook.contacts.ContactsDBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

public class SMSChatActivity extends AppCompatActivity {

    public static final String EXTRA_PHONE_NUMBER = "PHONE";

    private ListView smsChatView;
    private String name;
    private String phoneNumber;
    private Button smsSendButton;
    private EditText smsSendText;
    private ArrayList<SMSListItem> smsList;
    private ContentObserver smsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smschat);

        // initializes member variables
        smsList = new ArrayList<SMSListItem>();

        // get views from the layout
        smsChatView = (ListView) findViewById(R.id.smsChatList);
        smsSendButton = (Button) findViewById(R.id.sms_send_button);
        smsSendText = (EditText) findViewById(R.id.sms_send_text);

        // gets the name of the contact
        // if not registered number, the name is just the phone number.
        ContactsDBHelper dbHelper = new ContactsDBHelper(this);

        phoneNumber = getIntent().getExtras().getString(EXTRA_PHONE_NUMBER);
        name = dbHelper.getNameByPhoneNumber(phoneNumber);
        if(name == null) name = phoneNumber;

        dbHelper.close();

        // sets the title
        getSupportActionBar().setTitle(getResources().getString(R.string.title_sms_chat) + " - " + name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set send button listener
        smsSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(smsSendText.getText().toString().isEmpty()) {
                    // send failed because of empty text.
                    Toast.makeText(getApplicationContext(), R.string.sms_send_failed_empty_text, Toast.LENGTH_LONG).show();
                }
                else {
                    // send message and show
                    SmsManager.getDefault().sendTextMessage(phoneNumber, null, smsSendText.getText().toString(), null, null);
                    smsSendText.setText("");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // adds content observer that observes sms messages change and
        // updates the list view.
        smsObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);

                updateListView();
                smsChatView.setSelection(smsChatView.getCount() - 1);
            }
        };

        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, smsObserver);

        // updates list view
        updateListView();
        smsChatView.setSelection(smsChatView.getCount() - 1);
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            // unregister when the activity is paused or destroyed
            // don't update and receive sms when the activity isn't on the screen.
            getContentResolver().unregisterContentObserver(smsObserver);
        } catch(IllegalArgumentException e) {}  // if not registered, do nothing
    }

    public void updateListView() {
        SMSChatViewAdapter adapter = new SMSChatViewAdapter(this, name);

        // checking permissions
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), R.string.alert_request_sms_permission, Toast.LENGTH_LONG).show();
            return;
        }

        // query sms message such that address
        String[] conditionArgument = { phoneNumber };

        Cursor res = getApplicationContext().getContentResolver().query(Uri.parse("content://sms"),
                null, "address =  ?", conditionArgument, SMSListItem.SMS_DEFAULT_ORDER);

        res.moveToLast();
        while(!res.isBeforeFirst()) {
            String address = res.getString(res.getColumnIndex(SMSListItem.ADDRESS));
            long receivedTime = res.getLong(res.getColumnIndex(SMSListItem.DATE));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(receivedTime);
            String body = res.getString(res.getColumnIndex(SMSListItem.BODY));
            int type = res.getInt(res.getColumnIndex(SMSListItem.TYPE));
            boolean read = ((res.getInt(res.getColumnIndex(SMSListItem.READ)) == 0) ? false : true);


            adapter.addListItem(address, c, body, type, read);
            res.moveToPrevious();
        }

        smsChatView.setAdapter(adapter);
        smsList = adapter.getArray();
    }
}
