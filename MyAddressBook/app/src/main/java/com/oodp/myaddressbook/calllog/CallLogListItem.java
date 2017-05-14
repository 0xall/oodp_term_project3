package com.oodp.myaddressbook.calllog;

import java.util.Calendar;
import java.util.Date;

public class CallLogListItem {

    private int callType;
    private String phoneNumber;
    private int duration;
    private Calendar date;

    /**
     * Constructs an call log list item with empty value.
     */
    CallLogListItem() {
        callType = -1;
        phoneNumber = "";
        duration = 0;
        date = Calendar.getInstance();
    }

    /**
     * Constructs an call log list.
     * @param callType the type of call. Constants are defined in CallLog.Calls class.
     * @param phoneNumber the phone number of sender or receiver.
     * @param duration the calling duration.
     * @param date the started time.
     */
    CallLogListItem(int callType, String phoneNumber, int duration, Calendar date) {
        this.callType = callType;
        this.phoneNumber = phoneNumber;
        this.duration = duration;
        this.date = date;
    }

    /**
     * Constructs an call log list.
     * @param callType the type of call. Constants are defined in CallLog.Calls class.
     * @param phoneNumber the phone number of sender or receiver.
     * @param duration the calling duration.
     * @param date the started time.
     */
    CallLogListItem(int callType, String phoneNumber, int duration, long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        this.callType = callType;
        this.phoneNumber = phoneNumber;
        this.duration = duration;
        this.date = c;
    }

    void setCallType(int callType) {
        this.callType = callType;
    }

    int getCallType() {
        return callType;
    }

    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    void setDuration(int duration) {
        this.duration = duration;
    }

    int getDuration() {
        return duration;
    }

    void setDate(long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        this.date = c;
    }

    void setDate(Calendar date) {
        this.date = date;
    }

    Calendar getDate() {
        return date;
    }
}
