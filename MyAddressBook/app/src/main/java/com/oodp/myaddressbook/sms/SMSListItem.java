package com.oodp.myaddressbook.sms;

import android.database.Cursor;

import java.util.Calendar;

public class SMSListItem {

    public static final String TYPE = "type";
    public static final String READ = "read";
    public static final String DATE = "date";
    public static final String ADDRESS = "address";
    public static final String BODY = "body";
    public static final String SMS_DEFAULT_ORDER = "date DESC";

    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;

    private int type;
    private boolean read;
    private Calendar date;
    private String phoneNumber;
    private String body;

    /**
     * Constructs an SMSListItem with empty value.
     */
    SMSListItem() {
        type = 0;
        read = false;
        date = Calendar.getInstance();
        phoneNumber = "";
        body = "";
    }

    /**
     * Constructs an SMSListItem with full value.
     * @param type the type of the item. The constants are defined in the class as MESSAGE_TYPE_...
     * @param read whether the message is read.
     * @param date the received time.
     * @param phoneNumber the number of sender or receiver.
     * @param body message content.
     */
    SMSListItem(int type, boolean read, Calendar date, String phoneNumber, String body) {
        this.type = type;
        this.read = read;
        this.date = date;
        this.phoneNumber = phoneNumber;
        this.body = body;
    }

    /**
     * Constructs an SMSListItem from SQL query.
     * @param c the cursor of the SQL query result.
     */
    SMSListItem(Cursor c) {
        long dateTime;

        // get date from SQL.
        this.type = c.getInt(c.getColumnIndex(TYPE));
        this.read = ((c.getInt(c.getColumnIndex(READ)) == 1)? true : false);
        this.phoneNumber = c.getString(c.getColumnIndex(ADDRESS));
        this.body = c.getString(c.getColumnIndex(BODY));

        dateTime = c.getLong(c.getColumnIndex(DATE));
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(dateTime);
    }

    /**
     * Sets the type of the SMS.
     * @param type the type of the SMS.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Sets whether the SMS is read or not.
     * @param read boolean value representing the SMS is read.
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Sets the phone number of the SMS.
     * @param phoneNumber the phone number of the SMS.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the contents of the SMS.
     * @param body the contents of the SMS.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Sets the received time.
     * @param date the received time.
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     * Returns the type of the SMS.
     * @return the type of the SMS.
     */
    public int getType() {
        return type;
    }

    /**
     * Returns whether the SMS is read or not.
     * @return boolean value representing the SMS is read.
     */
    public boolean getRead() {
        return read;
    }

    /**
     * Returns the phone number of the SMS.
     * @return the phone number of the SMS.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns the body of the SMS.
     * @return the body of the SMS.
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns the received time of the SMS.
     * @return the received time.
     */
    public Calendar getDate() {
        return date;
    }
}
