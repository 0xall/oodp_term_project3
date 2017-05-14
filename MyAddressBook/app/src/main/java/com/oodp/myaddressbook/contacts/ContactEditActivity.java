package com.oodp.myaddressbook.contacts;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.oodp.myaddressbook.R;
import com.oodp.myaddressbook.view.CircularImageView;

import java.util.Calendar;

public class ContactEditActivity extends AppCompatActivity {

    public static final String EXTRA_ACTION = "ACTION";
    public static final int ACTION_ADD = 0;
    public static final int ACTION_MODIFY = 1;

    public static final String EXTRA_ID = "ID";

    private LinearLayout phoneListLayout;
    private EditText nameView;
    private AutoCompleteTextView contactsGroupView;
    private int birthdayYear, birthdayMonth, birthdayDay;
    private EditText birthdayView;
    private ImageButton birthdayChangeButton;
    private EditText homeAddressView, officeAddressView;
    private EditText emailView, urlView, memoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        // set all views variables
        nameView = (EditText) findViewById(R.id.contact_edit_name);
        phoneListLayout = (LinearLayout)findViewById(R.id.contact_edit_phone_list);
        contactsGroupView = (AutoCompleteTextView)findViewById(R.id.contact_edit_group);
        birthdayView = (EditText)findViewById(R.id.contact_edit_birthday_text);
        birthdayChangeButton = (ImageButton)findViewById(R.id.contact_edit_birthday_change_button);
        homeAddressView = (EditText)findViewById(R.id.contact_edit_home_address);
        officeAddressView = (EditText)findViewById(R.id.contact_edit_office_address);
        emailView = (EditText)findViewById(R.id.contact_edit_email);
        urlView = (EditText)findViewById(R.id.contact_edit_url);
        memoView = (EditText)findViewById(R.id.contact_edit_memo);

        onCreateSupportActionBar();
        onCreateImage();
        onCreateGroupEditBox();
        onCreateBirthdayEditBox();
        createPhoneEditor();
        onCreateSettingDefaultValue();
    }

    /**
     * Sets value for modifying if the action is modifying.
     */
    protected void onCreateSettingDefaultValue() {
        if(getActionType() == ACTION_MODIFY) {
            ContactsDBHelper dbHelper = new ContactsDBHelper(this);
            Contact contact = dbHelper.getContact(getContactID());

            nameView.setText(contact.getName());
            contactsGroupView.setText(contact.getGroup());

            // sets phone number
            int index = 0;
            for(PhoneNumber phoneNumber : contact) {
                LinearLayout editorLayout = getPhoneEditor(index++);
                Spinner typeView = (Spinner)editorLayout.findViewById(R.id.contact_edit_phone_type);
                EditText phoneNumberView = (EditText)editorLayout.findViewById(R.id.contact_edit_phone_number);
                // set an editor
                typeView.setSelection(phoneNumber.getPhoneType());
                phoneNumberView.setText(phoneNumber.getPhoneNumberWithOnlyNumber());

                // creates new phone editor for next
                createPhoneEditor();
            }

            homeAddressView.setText(contact.getHomeAddress());
            officeAddressView.setText(contact.getOfficeAddress());

            Calendar birthday = contact.getBirthday();
            if(birthday != null)
                birthdayView.setText(
                        birthday.get(Calendar.YEAR) + "/" +
                        (birthday.get(Calendar.MONTH) + 1) + "/" +
                        birthday.get(Calendar.DAY_OF_MONTH));

            emailView.setText(contact.getEmail());
            urlView.setText(contact.getURL());
            memoView.setText(contact.getMemo());
            dbHelper.close();
        }
    }

    /**
     * Initializes the support action bar of the activity.
     */
    protected void onCreateSupportActionBar() {
        final int actionType = getActionType();
        final ActionBar supportActionBar = getSupportActionBar();

        // set the title of the activity
        switch(actionType) {
            case ContactEditActivity.ACTION_ADD :
                supportActionBar.setTitle(R.string.title_add_contact);
                break;

            case ContactEditActivity.ACTION_MODIFY :
                supportActionBar.setTitle(R.string.title_modify_contact);
        }

        // enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Initializes the circular image view.
     */
    protected void onCreateImage() {
        // get the display manager for sizing
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        // set the size of the circular image view
        CircularImageView iv = (CircularImageView) findViewById(R.id.contact_edit_image);
        int size = (int)getResources().getDimension(R.dimen.contact_edit_max_image_view_size);
        if(size > outMetrics.heightPixels / 3) size = outMetrics.heightPixels / 3;
        iv.getLayoutParams().width = size;
        iv.getLayoutParams().height = size;
    }

    /**
     * Initializes the group edit box.
     */
    private void onCreateGroupEditBox() {
        String[] groups = getResources().getStringArray(R.array.contact_group);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, groups);
        contactsGroupView.setAdapter(adapter);

        // if focus on this view, show group drop down.
        contactsGroupView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) contactsGroupView.showDropDown();
            }
        });

        // if key down action button, focus to the phone editor.
        contactsGroupView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                getPhoneEditor(0).requestFocus();
                return true;
            }
        });
    }

    /**
     * Initializes the birthday edit box.
     */
    private void onCreateBirthdayEditBox() {
        Calendar c = Calendar.getInstance();

        // set the default birthday variables to today
        // if user click birthday changing button, it shows today.
        birthdayYear = c.get(Calendar.YEAR);
        birthdayMonth = c.get(Calendar.MONTH) + 1;
        birthdayDay = c.get(Calendar.DAY_OF_MONTH);

        // user cannot change the text.
        birthdayView.setEnabled(false);

        // set click listener of birthday changing button
        birthdayChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                birthdayYear = year;
                                birthdayMonth = month + 1;
                                birthdayDay = dayOfMonth;
                                birthdayView.setText(birthdayYear + "/" + birthdayMonth + "/" + birthdayDay);
                            }
                        }, birthdayYear, birthdayMonth - 1, birthdayDay);

                datePickerDialog.show();
            }
        });
    }

    /**
     * Adds a phone number editor in the list.
     * @return the linear layout for editor.
     */
    protected LinearLayout createPhoneEditor() {
        Spinner phoneTypeSpinner;
        LinearLayout phoneEditorLayout;
        int i;

        // get type list
        String[] phoneType = getResources().getStringArray(R.array.phone_type);
        boolean[] phoneTypeUsed = new boolean[phoneType.length];

        for(i = 0; i < phoneTypeUsed.length; ++i)
            phoneTypeUsed[i] = false;

        // check used type in the phone list layout
        final int childCount = phoneListLayout.getChildCount();
        for(i = 0; i < childCount; ++i) {
            phoneEditorLayout = getPhoneEditor(i);
            phoneTypeSpinner = (Spinner)phoneEditorLayout.findViewById(R.id.contact_edit_phone_type);
            phoneTypeUsed[phoneTypeSpinner.getSelectedItemPosition()] = true;
        }

        // make new layout for adding phone number
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        phoneEditorLayout = (LinearLayout)inflater.inflate(R.layout.phone_number_editor, null);

        // get spinner view from layout
        phoneTypeSpinner = (Spinner)phoneEditorLayout.findViewById(R.id.contact_edit_phone_type);
        ArrayAdapter phoneTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.phone_type, android.R.layout.simple_spinner_item);
        phoneTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneTypeSpinner.setAdapter(phoneTypeAdapter);

        // add listener
        EditText phoneEditor = (EditText)phoneEditorLayout.findViewById(R.id.contact_edit_phone_number);
        ImageButton removeButton = (ImageButton)phoneEditorLayout.findViewById(R.id.contact_edit_remove);
        phoneEditor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int index = getPhoneEditorIndex((View)v.getParent());
                final int phoneEditorCount = getPhoneEditorCount();
                processPhoneNumberEditor(v);
                if(index < phoneEditorCount - 2)
                    return getPhoneEditor(index + 1).findViewById(R.id.contact_edit_phone_number).requestFocus();
                else return homeAddressView.requestFocus();
            }
        });

        phoneEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false) processPhoneNumberEditor((TextView)v);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = getPhoneEditorIndex((View)v.getParent());
                if(index == -1) return;
                if(index != getPhoneEditorCount() - 1) {
                    removePhoneEditor(index);
                    getPhoneEditor(index).findViewById(R.id.contact_edit_phone_number).requestFocus();
                }
            }
        });



        // set default selection of the phone type
        for(i = 0; i < phoneTypeUsed.length; ++i) {
            if(phoneTypeUsed[i] == false) {
                phoneTypeSpinner.setSelection(i);
                break;
            }
        }

        // add to the activity
        phoneListLayout.addView(phoneEditorLayout);
        return phoneEditorLayout;
    }

    /**
     * Returns phone number editor.
     * @param index the index to return.
     * @return the phone number editor layout.
     */
    protected LinearLayout getPhoneEditor(int index) {
        return (LinearLayout)phoneListLayout.getChildAt(index);
    }

    /**
     * Returns the last index phone number editor.
     * @return the last index phone number editor layout.
     */
    protected LinearLayout getLastPhoneEditor() {
        return getPhoneEditor(getPhoneEditorCount() - 1);
    }

    protected int getPhoneEditorIndex(View v) {
        final int phoneEditorCount = getPhoneEditorCount();
        int index;
        for (index = 0; index < phoneEditorCount; ++index) {
            if (getPhoneEditor(index) == v) break;
        }

        if(index == phoneEditorCount) return -1;
        else return index;
    }

    /**
     * Returns the number of phone number editor.
     * @return the number of phone number editor.
     */
    protected int getPhoneEditorCount() {
        return phoneListLayout.getChildCount();
    }

    /**
     * Removes a phone number editor.
     * @param index the index to remove.
     */
    protected void removePhoneEditor(int index) {
        phoneListLayout.removeViewAt(index);
    }

    /**
     * Removes the last phone number editor.
     */
    protected void removeLastPhoneEditor() {
        removePhoneEditor(getPhoneEditorCount() - 1);
    }

    /**
     * Returns the reason that the activity is created. (add or modify)
     * The types are provided as the class static constant.
     * @return the action type by integer value.
     */
    protected int getActionType() {
        return getIntent().getExtras().getInt(ContactEditActivity.EXTRA_ACTION);
    }

    protected int getContactID() {
        if(getActionType() == ContactEditActivity.ACTION_MODIFY)
            return getIntent().getExtras().getInt(ContactEditActivity.EXTRA_ID);
        return -1;
    }

    protected boolean processPhoneNumberEditor(TextView v) {
        final LinearLayout lastPhoneEditor = getLastPhoneEditor();
        final String phoneNumber = v.getText().toString();

        // if the last phone editor has some text, add new phone editor
        if(lastPhoneEditor == v.getParent()) {
            if(phoneNumber.equals("")) {

            }

            else {
                createPhoneEditor();
            }
        }

        // if some phone editor has no phone number, remove or focus to next editor
        else {
            int index = 0;
            final int phoneEditorCount = getPhoneEditorCount();

            // search the index of the editor
            index = getPhoneEditorIndex((View)v.getParent());

            // unpredicted exception
            if (index == -1) return false;

            if(phoneNumber.isEmpty()) {
                removePhoneEditor(index);
            }

            else {
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        if(getActionType() == ACTION_ADD) {
            menuInflater.inflate(R.menu.menu_only_apply, menu);
        }

        else if(getActionType() == ACTION_MODIFY) {
            menuInflater.inflate(R.menu.menu_apply_delete, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        switch(itemId) {
            case android.R.id.home :
                onBackPressed();
                return true;

            case R.id.menu_apply :
                if(InsertToDatabase()) onBackPressed();
                return true;

            case R.id.menu_delete :
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(R.string.alert_dialog_reask);
                alertDialogBuilder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFromDatabase();
                        onBackPressed();
                    }
                });

                alertDialogBuilder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setTitle("Remove the contact");
                alertDialog.show();

                return true;

            default :
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Inserts the contact to the database.
     * @return true if success to insert into the database, or false if not.
     */
    public boolean InsertToDatabase() {
        Contact newContact = constructContact();
        if(newContact == null) return false;
        ContactsDBHelper dbHelper = new ContactsDBHelper(this);

        if(getActionType() == ACTION_ADD) {
            dbHelper.insertContact(newContact);
        }

        else if(getActionType() == ACTION_MODIFY) {
            dbHelper.updateContact(getContactID(), newContact);
        }

        dbHelper.close();
        return true;
    }

    public boolean deleteFromDatabase() {
        ContactsDBHelper dbHelper = new ContactsDBHelper(this);
        dbHelper.deleteContact(getContactID());
        dbHelper.close();
        return true;
    }

    /**
     * Constructs a contact instance from views.
     * @return the contact created from views.
     */
    public Contact constructContact() {
        Contact newContact = new Contact();
        int i;
        final int phoneNumbersCount = getPhoneEditorCount();

        // check whether it can be inserted or not
        if(!IsInsertableIntoDatabase(true)) return null;

        // sets value for contact from views
        newContact.setName(nameView.getText().toString().trim());
        newContact.setGroup(contactsGroupView.getText().toString().trim());

        // set phone numbers
        for(i = 0; i < phoneNumbersCount; ++i) {
            LinearLayout phoneNumberEditor = getPhoneEditor(i);
            int phoneNumberType = ((Spinner)phoneNumberEditor.findViewById(R.id.contact_edit_phone_type)).
                    getSelectedItemPosition();
            try {
                PhoneNumber phoneNumber = new PhoneNumber(phoneNumberType,
                        ((EditText) phoneNumberEditor.findViewById(R.id.contact_edit_phone_number)).getText().toString());
                if(phoneNumber.isEmpty()) continue;
                newContact.addPhoneNumber(phoneNumber);
            } catch(PhoneNumber.WrongSyntaxException e) { return null; }
        }

        newContact.setHomeAddress(homeAddressView.getText().toString().trim());
        newContact.setOfficeAddress(officeAddressView.getText().toString().trim());

        // set birthday
        String strBirthday = birthdayView.getText().toString().trim();
        String[] birthdayTokens = strBirthday.split("/");

        if(birthdayTokens.length == 3) {
            newContact.setBirthday(
                    Integer.parseInt(birthdayTokens[0]),    // year
                    Integer.parseInt(birthdayTokens[1]),    // month
                    Integer.parseInt(birthdayTokens[2])     // day of month
            );
        }

        newContact.setEmail(emailView.getText().toString().trim());
        newContact.setURL(urlView.getText().toString().trim());
        newContact.setMemo(memoView.getText().toString());

        return newContact;
    }

    public boolean IsInsertableIntoDatabase(boolean showReason) {
        // if name is not set, return false
        if(nameView.getText().toString().trim().equals("")) {
            if(showReason)
                Toast.makeText(this, "You should input name.", Toast.LENGTH_LONG).show();
            return false;
        }

        // if phone number is not set, return false
        else if(((TextView)(getPhoneEditor(0).findViewById(R.id.contact_edit_phone_number))).
                getText().toString().trim().equals("")) {
            if(showReason)
                Toast.makeText(this, "You should input phone number at least one.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
