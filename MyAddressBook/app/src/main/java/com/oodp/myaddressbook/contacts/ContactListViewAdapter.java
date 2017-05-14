package com.oodp.myaddressbook.contacts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oodp.myaddressbook.R;

import java.util.ArrayList;

public class ContactListViewAdapter extends BaseAdapter {

    private static final int ITEM_VIEW_TYPE_MAX = 2;
    private ArrayList<ContactListItem> contactItemList;
    private Context context;
    private LayoutInflater inflater;

    /**
     * Constructs a new adapter for the contact list.
     */
    public ContactListViewAdapter(Context context) {
        contactItemList = new ArrayList<ContactListItem>();
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contactItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactItemList.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //final Context context = parent.getContext();
        ContactListItem item = contactItemList.get(position);
        final int itemType = item.getItemType();

        if(item == null) return null;

        switch (itemType) {
            case ContactListItem.ITEM_TYPE_BOOKMARK :
            case ContactListItem.ITEM_TYPE_TITLE :  // the item is title
                // get view
                convertView = inflater.inflate(R.layout.contact_list_view_group, parent, false);

                // set the title
                TextView groupName = (TextView) convertView.findViewById(R.id.contactGroupName);
                groupName.setText(item.getName());

                // if the title is bookmark, set the font color to yellow
                if(itemType == ContactListItem.ITEM_TYPE_BOOKMARK)
                    groupName.setTextColor(Color.argb(255, 242, 203, 97));

                // the title is neither clickable, focusable, nor enabled.
                convertView.setClickable(false);
                convertView.setFocusable(false);
                convertView.setEnabled(false);

                break;

            case ContactListItem.ITEM_TYPE_CONTACT :    // the item is contact
                // get view
                convertView = inflater.inflate(R.layout.contact_list_view_item, parent, false);

                // get the image of the contact
                ImageView contactImage = (ImageView) convertView.findViewById(R.id.contactImage);
                TextView contactName = (TextView) convertView.findViewById(R.id.contactName);

                // set image and text. If image does not exist, it does not change. (default image)
                if (item.getContactImage() != null)
                    contactImage.setImageDrawable(item.getContactImage());

                contactName.setText(item.getName());
        }

        return convertView;
    }

    /**
     * Adds a new list item for contact list.
     * @param item the item of the contact list.
     */
    public void addListItem(ContactListItem item) {
        contactItemList.add(item);
    }

    /**
     * Adds a new title for contact list.
     * @param title the title in the contact list.
     * @param isBookmark whether the title is bookmark or not.
     */
    public void addTitle(String title, boolean isBookmark) {
        if(isBookmark) addListItem(new ContactListItem(ContactListItem.ITEM_TYPE_BOOKMARK, title));
        else addListItem(new ContactListItem(ContactListItem.ITEM_TYPE_TITLE, title));
    }

    /**
     * Adds a new contact item for contact list with only name.
     * @param name the name of the contact.
     */
    public void addContact(String name) {
        addListItem(new ContactListItem(ContactListItem.ITEM_TYPE_CONTACT, name));
    }

    /**
     * Adds a new contact item for contact list with a name and a image.
     * @param name the name of the contact.
     * @param image the name of the image.
     */
    public void addContact(String name, Drawable image) {
        addListItem(new ContactListItem(ContactListItem.ITEM_TYPE_CONTACT, name, image));
    }
}
