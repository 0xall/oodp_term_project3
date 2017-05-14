package com.oodp.myaddressbook.contacts;

import android.graphics.drawable.Drawable;

/**
 * ContactListItem class is an item class for supporting the contact ListView.
 */
public class ContactListItem {

    private int itemType;
    private Drawable contactImage;
    private String name;
    private Contact contact;

    public static final int ITEM_TYPE_TITLE = 0;
    public static final int ITEM_TYPE_BOOKMARK = 1;
    public static final int ITEM_TYPE_CONTACT = 2;

    /**
     * Constructs a contact list item with no data
     * @param itemType the type of the item..
     */
    public ContactListItem(int itemType) {
        this.itemType = itemType;
        this.name = "";
        this.contact = null;
    }

    /**
     * Constructs a contact list item with only name.
     * @param name the name of the contact if the type is ITEM_TYPE_CONTACT or the title of the item if the type is ITEM_TYPE_TITLE.
     * @param itemType = the type of the item.
     */
    public ContactListItem(int itemType, String name) {
        this(itemType);
        this.name = name;
    }

    /**
     * Constructs a contact list item with name and image.
     * @param contactImage the image of the contact.
     * @param name the name of the contact if the type is ITEM_TYPE_CONTACT or the title of the item if the type is ITEM_TYPE_TITLE.
     * @param itemType the type of the item.
     */
    public ContactListItem(int itemType, String name, Drawable contactImage) {
        this(itemType, name);
        this.contactImage = contactImage;
    }

    /**
     * Sets the image of the contact.
     * @param image the image of the contact.
     */
    public void setContactImage(Drawable image) {
        this.contactImage = image;
    }

    /**
     * Sets the name of the contact.
     * @param name the name of the contact.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the type of the item.
     * @param itemType the item type.
     */
    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    /**
     * Sets the Contact instance of the item.
     * @param contact the contact of the item.
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * Returns the image of the contact.
     * @return the image of the contact.
     */
    public Drawable getContactImage() {
        return contactImage;
    }

    /**
     * Returns the name of the contact.
     * @return the name of the contact.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the item type.
     * @return the item type.
     */
    public int getItemType() {
        return itemType;
    }

    /**
     * Returns the Contact instance.
     * @return the Contact instance.
     */
    public Contact getContact() {
        return contact;
    }
}
