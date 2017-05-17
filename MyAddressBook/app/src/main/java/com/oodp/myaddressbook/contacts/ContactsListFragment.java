package com.oodp.myaddressbook.contacts;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.oodp.myaddressbook.MainActivity;
import com.oodp.myaddressbook.R;
import com.oodp.myaddressbook.sms.SMSChatActivity;

import java.util.ArrayList;
import java.util.LinkedList;

public class ContactsListFragment extends Fragment {

    SearchView contactSearchView;
    ListView contactsList;
    ArrayList<Contact> contacts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        View view = inflater.inflate(R.layout.fragment_contacts_list, container, false);

        // get the views of the fragment
        contactsList = (ListView)view.findViewById(R.id.contactsList);
        contactSearchView = (SearchView)view.findViewById(R.id.contactsSearch);

        onCreateSearchView();

        // change the title of the activity
        activity.getSupportActionBar()
                .setTitle(R.string.title_contacts_list);

        // add the add contact button listener
        view.findViewById(R.id.addContactButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactAddIntent = new Intent(getActivity(), ContactEditActivity.class);
                contactAddIntent.putExtra(ContactEditActivity.EXTRA_ACTION, ContactEditActivity.ACTION_ADD);
                startActivity(contactAddIntent);
            }
        });

        return view;
    }

    public void onCreateSearchView() {

        // set styles for view
        contactSearchView.onActionViewExpanded();
        contactSearchView.clearFocus();

        // set listener
        contactSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateListView(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateListView(newText.trim());
                return true;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        updateListView(contactSearchView.getQuery().toString().trim());
    }

    /**
     * Updates list view.
     */
    public void updateListView(String search) {
        ContactListViewAdapter adapter = new ContactListViewAdapter(getActivity());
        ContactsDBHelper contactsDBHelper = new ContactsDBHelper(getActivity());
        contacts = contactsDBHelper.getSatisfiedContacts(search);
        String currentGroup = null;

        //adapter.addTitle("Bookmark", true);

        for(Contact contact : contacts) {
            if(currentGroup == null || !contact.getGroup().equals(currentGroup)) {
                ContactListItem groupSeparatorItem = new ContactListItem(ContactListItem.ITEM_TYPE_TITLE, contact.getGroup());
                if(contact.getGroup().equals("")) groupSeparatorItem.setName("No Group");
                currentGroup = contact.getGroup();
                adapter.addListItem(groupSeparatorItem);
            }

            ContactListItem contactListItem = new ContactListItem(ContactListItem.ITEM_TYPE_CONTACT, contact.getName());
            contactListItem.setContact(contact);
            adapter.addListItem(contactListItem);
        }

        // insert items to the ListView
        contactsList.setAdapter(adapter);
        contactsList.setOnItemClickListener(new ContactItemClickListener());
        contactsList.setOnItemLongClickListener(new ContactItemLongClickListener());

        contactsDBHelper.close();
    }

    /**
     * Interface definition for a callback to be invoked when a contact item in the ContactListViewAdapter
     * has been clicked.
     */
    class ContactItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ContactListItem item = (ContactListItem)parent.getItemAtPosition(position);

            if(item.getItemType() == ContactListItem.ITEM_TYPE_CONTACT) {
                Intent i = new Intent(getActivity(), ContactEditActivity.class);
                Log.d("Position", position + "");
                i.putExtra(ContactEditActivity.EXTRA_ACTION, ContactEditActivity.ACTION_MODIFY);
                i.putExtra(ContactEditActivity.EXTRA_ID, item.getContact().getKey());
                startActivity(i);
            }
        }
    }

    /**
     * Interface definition for a callback to be invoked when a contact item in the ContactListViewAdapter
     * has been long-clicked.
     */
    class ContactItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog.Builder actionDialog = new AlertDialog.Builder(getActivity());
            ContactListItem item = (ContactListItem)parent.getItemAtPosition(position);
            Contact contact = item.getContact();
            LinkedList<String> menu = new LinkedList<String>();
            String[] menuArray = null;

            menu.add(getResources().getString(R.string.contact_action_call));
            menu.add(getResources().getString(R.string.contact_action_send_sms));

            if(!contact.getEmail().isEmpty()) menu.add(getResources().getString(R.string.contact_action_send_email));
            menuArray = menu.toArray(new String[0]);

            actionDialog.setTitle(getResources().getString(R.string.contact_action_dialog_title))
                    .setItems(menuArray, new ContactActionItemClickListener(contact));

            actionDialog.create().show();

            return true;
        }

        /**
         * Interface definition for a callback to be invoked when user click an action item in the
         * action list.
         */
        class ContactActionItemClickListener implements DialogInterface.OnClickListener {

            Contact contact;

            public static final int ITEM_CALL = 0;
            public static final int ITEM_SEND_SMS = 1;
            public static final int ITEM_SEND_EMAIL = 2;

            public ContactActionItemClickListener(Contact contact) {
                super();
                this.contact = contact;
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case ITEM_CALL :
                    case ITEM_SEND_SMS :
                        String[] phoneTypeString = getResources().getStringArray(R.array.phone_type);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(getResources().getString((which == ITEM_CALL) ?
                                R.string.contact_which_to_call_title : R.string.contact_which_to_send_sms_title));

                        // make menu for choosing phone numbers
                        LinkedList<String> phoneNumbersList = new LinkedList<String>();
                        for(PhoneNumber phoneNumber : contact) {
                            StringBuilder strPhoneNumber = new StringBuilder();
                            strPhoneNumber.append("(").append(phoneTypeString[phoneNumber.getPhoneType()])
                                    .append(") ").append(phoneNumber.getPhoneNumber());
                            phoneNumbersList.add(strPhoneNumber.toString());
                        }

                        // set item click listener
                        if(which == ITEM_CALL)
                            builder.setItems(phoneNumbersList.toArray(new String[0]), new ContactActionCallItemClickListener(contact));
                        else
                            builder.setItems(phoneNumbersList.toArray(new String[0]), new ContactActionSendSMSItemClickListener(contact));

                        builder.create().show();

                        break;
                }
            }
        }

        /**
         * Interface definition for a callback to be invoked when user click phone number for call.
         */
        class ContactActionCallItemClickListener implements DialogInterface.OnClickListener {

            Contact contact;

            ContactActionCallItemClickListener(Contact contact) {
                super();
                this.contact = contact;
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if you have no permission, print error and do nothing.
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), R.string.alert_request_call_permission, Toast.LENGTH_LONG).show();
                    return;
                }

                // make a call
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contact.getPhoneNumber(which).getPhoneNumberWithOnlyNumber()));
                startActivity(callIntent);
            }
        }

        /**
         * Interface definition for a callback to be invoked when user click phone number for sending message.
         */
        class ContactActionSendSMSItemClickListener implements DialogInterface.OnClickListener {

            Contact contact;

            ContactActionSendSMSItemClickListener(Contact contact) {
                super();
                this.contact = contact;
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getActivity(), SMSChatActivity.class);
                i.putExtra(SMSChatActivity.EXTRA_PHONE_NUMBER, contact.getPhoneNumber(which).getPhoneNumberWithOnlyNumber());
                startActivity(i);
            }
        }
    }
}
