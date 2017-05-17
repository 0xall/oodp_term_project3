package com.oodp.myaddressbook;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.oodp.myaddressbook.calllog.CallLogFragment;
import com.oodp.myaddressbook.contacts.ContactEditActivity;
import com.oodp.myaddressbook.contacts.ContactsListFragment;
import com.oodp.myaddressbook.sms.SMSListFragment;

public class MainActivity extends AppCompatActivity {

    public static final int FRAGMENT_INDEX_CONTACTS_LIST = 0;
    public static final int FRAGMENT_INDEX_CALL_LOG = 1;
    public static final int FRAGMENT_INDEX_SMS = 2;

    private int fragmentIndex;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_contactsList:
                    displayFragment(MainActivity.FRAGMENT_INDEX_CONTACTS_LIST);
                    return true;

                case R.id.navigation_callLog:
                    displayFragment(MainActivity.FRAGMENT_INDEX_CALL_LOG);
                    return true;

                case R.id.navigation_sms:
                    displayFragment(MainActivity.FRAGMENT_INDEX_SMS);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if the activity is created by some action like rotating the device
        // and not by starting this application,load some data from bundle.
        if(savedInstanceState != null) {
            displayFragment(savedInstanceState.getInt("FRAGMENT_INDEX"));
        }

        else {
            displayFragment(FRAGMENT_INDEX_CONTACTS_LIST);
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("FRAGMENT_INDEX", fragmentIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * sets the value for the fragment.
     * This function is only used internally.
     * @param fragmentIndex the index of the fragment to set.
     */
    private void _setFragmentValue(int fragmentIndex) {
        this.fragmentIndex = fragmentIndex;
    }

    /**
     * sets the value for the fragment and change the fragment.
     * @param fragmentIndex the index of the fragment to change.
     */
    public void displayFragment(int fragmentIndex) {
        _setFragmentValue(fragmentIndex);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment;

        switch(fragmentIndex) {
            case FRAGMENT_INDEX_CONTACTS_LIST :
                fragment = new ContactsListFragment();
                break;

            case FRAGMENT_INDEX_CALL_LOG :
                fragment = new CallLogFragment();
                break;

            case FRAGMENT_INDEX_SMS :
                fragment = new SMSListFragment();
                break;

            default :
                fragment = new ContactsListFragment();
        }


        fragmentTransaction.replace(R.id.mainContentFragment, fragment);
        fragmentTransaction.commit();

    }
}
