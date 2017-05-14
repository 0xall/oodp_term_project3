package com.oodp.myaddressbook.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;


public class ContactsDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "contacts.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";

    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_GROUP = "contact_group";
    public static final String CONTACTS_COLUMN_PHONE_NUMBERS = "phone_numbers";
    public static final String CONTACTS_COLUMN_HOME_NUMBERS = "home_numbers";
    public static final String CONTACTS_COLUMN_WORK_NUMBERS = "work_numbers";
    public static final String CONTACTS_COLUMN_HOME_ADDRESS = "home_address";
    public static final String CONTACTS_COLUMN_OFFICE_ADDRESS = "office_address";
    public static final String CONTACTS_COLUMN_BIRTHDAY = "birthday";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_URL = "url";
    public static final String CONTACTS_COLUMN_MEMO = "memo";

    public ContactsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + CONTACTS_TABLE_NAME + " " +
                        "(" + CONTACTS_COLUMN_ID + " integer primary key, " +
                        CONTACTS_COLUMN_NAME + " text not null, " +
                        CONTACTS_COLUMN_GROUP + " text, " +
                        CONTACTS_COLUMN_PHONE_NUMBERS + " text, " +
                        CONTACTS_COLUMN_HOME_NUMBERS + " text, " +
                        CONTACTS_COLUMN_WORK_NUMBERS + " text, " +
                        CONTACTS_COLUMN_HOME_ADDRESS + " text, " +
                        CONTACTS_COLUMN_OFFICE_ADDRESS + " text, " +
                        CONTACTS_COLUMN_BIRTHDAY + " date, " +
                        CONTACTS_COLUMN_EMAIL + " text, " +
                        CONTACTS_COLUMN_URL + " text, " +
                        CONTACTS_COLUMN_MEMO + " text) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    /**
     * Returns the row value of the contact table.
     * @return the row value of the contact table.
     */
    public ContentValues makeRow(String name, String group, PhoneNumber[] phoneNumbers, PhoneNumber[] homeNumbers,
                                 PhoneNumber[] workNumbers, String homeAddress, String officeAddress,
                                 Calendar birthday, String email, String url, String memo) {
        ContentValues contentValues = new ContentValues();
        StringBuilder strPhoneNumbers = new StringBuilder("");
        StringBuilder strHomeNumbers = new StringBuilder("");
        StringBuilder strWorkNumbers = new StringBuilder("");
        String strBirthday = "";

        if(birthday != null) {
            strBirthday = birthday.get(Calendar.YEAR) + "-" + (birthday.get(Calendar.MONTH) + 1) + "-" +
                    birthday.get(Calendar.DAY_OF_MONTH);
        }

        for(PhoneNumber phoneNumber : phoneNumbers) {
            if(strPhoneNumbers.toString() != "") strPhoneNumbers.append(",");
            strPhoneNumbers.append(phoneNumber.getPhoneNumberWithOnlyNumber());
        }

        for(PhoneNumber phoneNumber : homeNumbers) {
            if(strHomeNumbers.toString() != "") strHomeNumbers.append(",");
            strHomeNumbers.append(phoneNumber.getPhoneNumberWithOnlyNumber());
        }

        for(PhoneNumber phoneNumber : workNumbers) {
            if(strWorkNumbers.toString() != "") strWorkNumbers.append(",");
            strWorkNumbers.append(phoneNumber.getPhoneNumberWithOnlyNumber());
        }

        contentValues.put(CONTACTS_COLUMN_NAME, name.trim());
        contentValues.put(CONTACTS_COLUMN_GROUP, group.trim());
        contentValues.put(CONTACTS_COLUMN_PHONE_NUMBERS, strPhoneNumbers.toString());
        contentValues.put(CONTACTS_COLUMN_HOME_NUMBERS, strHomeNumbers.toString());
        contentValues.put(CONTACTS_COLUMN_WORK_NUMBERS, strWorkNumbers.toString());
        if(birthday != null) contentValues.put(CONTACTS_COLUMN_BIRTHDAY, strBirthday);
        contentValues.put(CONTACTS_COLUMN_HOME_ADDRESS, homeAddress.trim());
        contentValues.put(CONTACTS_COLUMN_OFFICE_ADDRESS, officeAddress.trim());
        contentValues.put(CONTACTS_COLUMN_EMAIL, email.trim());
        contentValues.put(CONTACTS_COLUMN_URL, url.trim());
        contentValues.put(CONTACTS_COLUMN_MEMO, memo);

        return contentValues;
    }

    public ContentValues makeRow(Contact contact) {

        // classifies the numbers
        LinkedList<PhoneNumber> phoneNumbersList = new LinkedList<PhoneNumber>();
        LinkedList<PhoneNumber> homeNumberList = new LinkedList<PhoneNumber>();
        LinkedList<PhoneNumber> workNumberList = new LinkedList<PhoneNumber>();

        for(PhoneNumber phoneNumber : contact) {
            if(phoneNumber.getPhoneType() == PhoneNumber.TYPE_PHONE_NUMBER)
                phoneNumbersList.add(phoneNumber);
            else if(phoneNumber.getPhoneType() == PhoneNumber.TYPE_HOME_NUMBER)
                homeNumberList.add(phoneNumber);
            else if(phoneNumber.getPhoneType() == PhoneNumber.TYPE_WORK_NUMBER)
                workNumberList.add(phoneNumber);
        }

        return makeRow(contact.getName(), contact.getGroup(),
                (PhoneNumber[])phoneNumbersList.toArray(new PhoneNumber[0]),
                (PhoneNumber[])homeNumberList.toArray(new PhoneNumber[0]),
                (PhoneNumber[])workNumberList.toArray(new PhoneNumber[0]),
                contact.getHomeAddress(),
                contact.getOfficeAddress(),
                contact.getBirthday(), contact.getEmail(), contact.getURL(), contact.getMemo());
    }

    /**
     * Inserts a contact into the table.
     * @return true if success to insert or false if not.
     */
    public boolean insertContact(String name, String group, PhoneNumber[] phoneNumbers, PhoneNumber[] homeNumbers,
                                PhoneNumber[] workNumbers, String homeAddress, String officeAddress,
                                 Calendar birthday, String email, String url, String memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = makeRow(name, group, phoneNumbers, homeNumbers, workNumbers,
                homeAddress, officeAddress, birthday, email, url, memo);

        db.insert(CONTACTS_TABLE_NAME, null, contentValues);

        return true;
    }

    public boolean insertContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = makeRow(contact);

        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    /**
     * Returns the number of rows of the contacts table.
     * @return the number of rows of the contacts table.
     */
    public int getNumberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
    }

    /**
     * Updates a contact.
     * @param id the id of the contact.
     * @return true if success to updates, or false if not.
     */
    public boolean updateContact(int id, String name, String group, PhoneNumber[] phoneNumbers, PhoneNumber[] homeNumbers,
                                 PhoneNumber[] workNumbers, String homeAddress, String officeAddress,
                                 Calendar birthday, String email, String url, String memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = makeRow(name, group, phoneNumbers, homeNumbers, workNumbers,
                homeAddress, officeAddress, birthday, email, url, memo);

        // updates a contact that has the id
        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ?", new String[] {
           "" + id
        });

        return true;
    }

    public boolean updateContact(int id, Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = makeRow(contact);

        // updates a contact that has the id
        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ?", new String[] {
                "" + id
        });

        return true;
    }

    public int deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME, "id = ? ", new String[] { "" + id });
    }

    private Contact getContact(Cursor res) {
        if(res.isAfterLast()) return null;
        Contact newContact = new Contact();
        String birthday;
        String[] birthdayNumberArray;
        String rawPhoneNumbers = "";
        String[] phoneNumbersArray;

        newContact.setKey(res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID)));
        newContact.setName(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
        newContact.setGroup(res.getString(res.getColumnIndex(CONTACTS_COLUMN_GROUP)));
        newContact.setHomeAddress(res.getString(res.getColumnIndex(CONTACTS_COLUMN_HOME_ADDRESS)));
        newContact.setOfficeAddress(res.getString(res.getColumnIndex(CONTACTS_COLUMN_OFFICE_ADDRESS)));
        newContact.setEmail(res.getString(res.getColumnIndex(CONTACTS_COLUMN_EMAIL)));
        newContact.setURL(res.getString(res.getColumnIndex(CONTACTS_COLUMN_URL)));
        newContact.setMemo(res.getString(res.getColumnIndex(CONTACTS_COLUMN_MEMO)));

        final String[] columnsName = {
                CONTACTS_COLUMN_PHONE_NUMBERS,
                CONTACTS_COLUMN_HOME_NUMBERS,
                CONTACTS_COLUMN_WORK_NUMBERS };

        for(int i = 0; i < columnsName.length; ++i) {
            rawPhoneNumbers = res.getString(res.getColumnIndex(columnsName[i]));
            phoneNumbersArray = rawPhoneNumbers.split(",");

            for (int j = 0; j < phoneNumbersArray.length; ++j) {
                try {
                    if(!phoneNumbersArray[j].isEmpty())
                        newContact.addPhoneNumber(new PhoneNumber(i, phoneNumbersArray[j]));
                } catch (PhoneNumber.WrongSyntaxException e) {
                }
            }
        }

        // set birthday if it is set.
        birthday = res.getString(res.getColumnIndex(CONTACTS_COLUMN_BIRTHDAY));
        if(birthday != null) {
            birthdayNumberArray = birthday.split("-");
            if (birthdayNumberArray.length == 3) {
                newContact.setBirthday(
                        Integer.parseInt(birthdayNumberArray[0]),
                        Integer.parseInt(birthdayNumberArray[1]),
                        Integer.parseInt(birthdayNumberArray[2]));
            }
        }

        return newContact;
    }

    public Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + CONTACTS_TABLE_NAME + " WHERE id=" + id, null);
        res.moveToFirst();
        return getContact(res);
    }

    public ArrayList getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + CONTACTS_TABLE_NAME + " ORDER BY " + CONTACTS_COLUMN_GROUP + ", " +
                CONTACTS_COLUMN_NAME + " ASC;", null);
        res.moveToFirst();
        while(res.isAfterLast() == false) {
            contacts.add(getContact(res));
            res.moveToNext();
        }

        return contacts;
    }

    public ArrayList getConditionContacts(String condition) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + CONTACTS_TABLE_NAME +
                " WHERE " + condition +
                " ORDER BY " + CONTACTS_COLUMN_GROUP + ", " +
                CONTACTS_COLUMN_NAME + " ASC", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            contacts.add(getContact(res));
            res.moveToNext();
        }

        return contacts;
    }

    public ArrayList getSatisfiedContacts(String name) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + CONTACTS_TABLE_NAME +
                " WHERE LOWER(" + CONTACTS_COLUMN_NAME + ") LIKE \"%" + name.toLowerCase() + "%\"" +
                " ORDER BY " + CONTACTS_COLUMN_GROUP + ", " +
                CONTACTS_COLUMN_NAME + " ASC", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            contacts.add(getContact(res));
            res.moveToNext();
        }

        return contacts;
    }
}
