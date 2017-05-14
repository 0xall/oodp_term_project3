package com.oodp.myaddressbook.sms;

import java.util.Calendar;

public class SMSListItem {

    public static final String TYPE = "type";
    public static final String READ = "read";
    public static final String DATE = "date";
    public static final String ADDRESS = "address";
    public static final String BODY = "body";

    private int type;
    private boolean read;
    private Calendar date;
    private String phoneNumber;
    private String body;
}
