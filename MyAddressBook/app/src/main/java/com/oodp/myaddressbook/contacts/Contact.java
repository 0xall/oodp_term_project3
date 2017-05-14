package com.oodp.myaddressbook.contacts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;


/*******************************************************************************
 *
 * Contact class represents an address saved in the Phone address book.
 *
 * @author Lee Ho Jun
 *
 ******************************************************************************/

public class Contact implements Iterable<PhoneNumber> {

    ////////////////////////////////////////////////////////////////////////////
	/* CONSTANT Variables */

    /**
     * Regular expression for checking the email address syntax. The email
     * address string is only allowed alphabets, numbers, and under bar(_),
     * exactly one "@" and dots in the address.
     */
    private static final String	EMAIL_REGULAR_EXPRESSION =
            "^\\s*[a-zA-Z0-9_]+@[a-zA-Z0-9._]+\\s*$";

    private static final String
            XML_ELEMENT_NAME = "NAME",
            XML_ELEMENT_PHONE_NUMBERS = "PHONE_NUMBERS",
            XML_ELEMENT_HOME_ADDRESS = "HOME_ADDRESS",
            XML_ELEMENT_OFFICE_ADDRESS = "OFFICE_ADDRESS",
            XML_ELEMENT_GROUP = "GROUP",
            XML_ELEMENT_EMAIL = "EMAIL",
            XML_ELEMENT_URL = "URL",
            XML_ELEMENT_BIRTHDAY = "BIRTHDAY";




    ////////////////////////////////////////////////////////////////////////////
	/* MEMBER Variables */

	private int                    key;
    private String 					name;
    private ArrayList<PhoneNumber> 	phoneNumberList;
    private String					homeAddress, officeAddress;
    private String					group;
    private String					email;
    private String					url;
    private Calendar				birthday;
    private String                 memo;


    /////////////////////////////////////////////////////////////////////////////
	/* STATIC METHODS */

    /**
     * Tests if the string is email string or not.
     * @param email the string to test
     * @return true if the string is email syntax, and false if otherwise.
     */
    private static boolean isEmail(String email)
    {
        // if the string is right email syntax,
        if(email.matches(EMAIL_REGULAR_EXPRESSION)) return true;
        return false;
    }

    /**
     * Tests if the day exists or not.
     * @param year the year for checking
     * @param month the month for checking
     * @param day the day for checking
     * @return true if the day exists, and false if otherwise.
     */
    private static boolean isValidDay(int year, int month, int day)
    {
        if(month < 1 || day < 1) return false;

        switch(month)
        {
            case 2 :	// February checking
                // if leap year,
                if(((year % 4 == 0) && (year % 100 != 0)) || year % 400 == 0)
                {
                    // if the day is over 29, return false
                    if(day > 29) return false;
                }

                else
                {
                    // if the day is over 28, return false
                    if(day > 28) return false;
                }

                break;

            case 1 : case 3 : case 5 : case 7 : case 8 : case 10 : case 12 :
            if(day > 31) return false;
            break;

            case 4 : case 6 : case 9 : case 11 :
            if(day > 30) return false;
            break;

            default :
                // if the month is over 12,
                return false;
        }

        return true;
    }



    /////////////////////////////////////////////////////////////////////////////
	/* CONSTRUCTORS */

    /**
     * Constructs an address with empty value.
     */
    public Contact()
    {
        name = "";
        phoneNumberList = new ArrayList<PhoneNumber>();
        homeAddress = ""; officeAddress = "";
        group = "";
        email = "";
        url = "";
        birthday = null;
    }

    public Contact(Contact address)
    {
        name = address.name;
        homeAddress = address.homeAddress;
        officeAddress = address.officeAddress;
        group = address.group;
        email = address.email;
        url = address.url;
        birthday = (Calendar) address.birthday.clone();

        // deep copy of the phone number list
        phoneNumberList =
                new ArrayList<PhoneNumber>(address.phoneNumberList.size());

        for(int i=0; i<address.phoneNumberList.size(); ++i)
        {
            phoneNumberList.add(new PhoneNumber(address.phoneNumberList.get(i)));
        }
    }

    /////////////////////////////////////////////////////////////////////////////
	/* METHODS */

    /**
     * Sets the key of the contact.
     * @param key the key of the contact.
     */
	public void setKey(int key) {
        this.key = key;
    }

    /**
     * Gets the key of the contact.
     * @return the key of the contact.
     */
    public int getKey() {
        return key;
    }

    /**
     * Sets the name of the address.
     * @param name the name of the address.
     */
    public void setName(String name)
    {
        // remove space around the string and set the name
        this.name = name.trim();
    }

    /**
     * Returns the name of the address.
     * @return the name of the address.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Adds a phone number to the address.
     * @param phoneNumber the phone number to add.
     * @return true if succeeded to add or false if failed to add
     * because the phone number is duplicated.
     */
    public boolean addPhoneNumber(PhoneNumber phoneNumber)
    {
        // if already exist, return with doing nothing
        if(phoneNumberList.contains(phoneNumber)) return false;
        phoneNumberList.add(phoneNumber);
        return true;
    }

    /**
     * Deletes a phone number by the index in the phone number list.
     * @param index the index of the phone number list for deleting.
     */
    public void deletePhoneNumber(int index)
    {
        phoneNumberList.remove(index);
    }

    /**
     * Deletes a phone number by the PhoneNumber instance.
     * @param phoneNumber the same value of PhoneNumber object to remove.
     */
    public void deletePhoneNumber(PhoneNumber phoneNumber)
    {
        phoneNumberList.remove(phoneNumber);
    }

    /**
     * Modifies a phone number.
     * @param index the index of the phone number to modify.
     * @param phoneNumber the modifying phone number value.
     */
    public void modifyPhoneNumber(int index, PhoneNumber phoneNumber)
    {
        // if the phone number for changing already exists in the array,
        // return doing nothing
        if(containsPhoneNumber(phoneNumber)) return;

        // get the PhoneNumber object in the array and change the number
        PhoneNumber modPhoneNumber = phoneNumberList.get(index);
        modPhoneNumber.setPhoneNumber(phoneNumber);
    }

    /**
     * Modifies a phone number.
     * @param phoneNumber the phone number to be changed.
     * @param changingPhoneNumber the phone number value to change
     */
    public void modifyPhoneNumber(PhoneNumber phoneNumber, PhoneNumber changingPhoneNumber)
    {
        // if the phone number for changing already exists in the array,
        // return doing nothing
        if(containsPhoneNumber(changingPhoneNumber)) return;

        // change the number
        Collections.replaceAll(phoneNumberList, phoneNumber, changingPhoneNumber);
    }

    /**
     * Returns true if the phone number exists in the list, and return
     * false if otherwise.
     * @param phoneNumber the phone number to be tested.
     * @return true if the phone number is in the list.
     */
    public boolean containsPhoneNumber(PhoneNumber phoneNumber)
    {
        if(phoneNumberList.contains(phoneNumber)) return true;
        return false;
    }

    /**
     * Returns the phone number of the address.
     * @param index the phone number index in the list.
     * @return PhoneNumber object in the list.
     * @throws IndexOutOfBoundsException signal if the index is out of
     * bounds.
     */
    public PhoneNumber getPhoneNumber(int index)
            throws IndexOutOfBoundsException
    {
        return phoneNumberList.get(index);
    }

    /**
     * Sets the home address of the address.
     * @param homeAddress the home address.
     */
    public void setHomeAddress(String homeAddress)
    {
        // remove space around the string and set the home address
        this.homeAddress = homeAddress.trim();
    }

    /**
     * Returns the home address of the address.
     * @return the home address of the address.
     */
    public String getHomeAddress()
    {
        return homeAddress;
    }

    /**
     * Sets the office address of the address.
     * @param officeAddress the office address.
     */
    public void setOfficeAddress(String officeAddress)
    {
        // remove space around the string and set the office address
        this.officeAddress = officeAddress.trim();
    }

    /**
     * Returns the office address of the address.
     * @return the office address of the address.
     */
    public String getOfficeAddress()
    {
        return officeAddress;
    }

    /**
     * Sets the involved group of the address.
     * @param group the involved group of the address.
     */
    public void setGroup(String group)
    {
        // remove space around the string and set the involved group name
        this.group = group.trim();
    }

    /**
     * Returns the involved group of the address.
     * @return the involved group of the address.
     */
    public String getGroup()
    {
        return group;
    }

    /**
     * Sets the email address of the address.
     * @param email the email of the address.
     * @return true if and only if the string is right email syntax, and
     * if otherwise, return false.
     */
    public boolean setEmail(String email)
    {
        // if the string is right email syntax,
        if(Contact.isEmail(email))
        {
            this.email = email.trim();
            return true;
        }

        // if otherwise, return false
        return false;
    }

    /**
     * Returns the email address of the address.
     * @return the email address of the address.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sets own site URL of the address.
     * @param url own site URL of the address.
     */
    public void setURL(String url)
    {
        this.url = url.trim();
    }

    /**
     * Returns the own site URL of the address.
     * @return the own site URL of the address.
     */
    public String getURL()
    {
        return url;
    }

    /**
     * Sets the birthday of the address.
     * @param birthday the birthday of the address.
     * @return true if succeeding to change birthday, and false if
     * failed to change birthday because the day does not exist.
     */
    public boolean setBirthday(Calendar birthday)
    {
        // if the day exists, change the birthday
        if(Contact.isValidDay(birthday.get(Calendar.YEAR),
                birthday.get(Calendar.MONTH),
                birthday.get(Calendar.DATE)))
        {
            // if it is not initialized, initialize it.
            if(this.birthday == null) this.birthday =
                    Calendar.getInstance();

            this.birthday = birthday;
            return true;
        }

        // if the day does not exist, return false with doing nothing
        return false;
    }

    /**
     * Sets the birthday of the address.
     * @param year the year of the birthday.
     * @param month the month of the birthday.
     * @param day the day of the birthday.
     * @return true if succeeding to change birthday, and false if
     * failed to change birthday because the day does not exist.
     */
    public boolean setBirthday(int year, int month, int day)
    {
        // if the day exists, change the birthday
        if(Contact.isValidDay(year, month, day))
        {
            // if it is not initialized, initialize it.
            if(this.birthday == null) this.birthday =
                    Calendar.getInstance();

            this.birthday.set(year, month - 1, day);
            return true;
        }

        // if the day does not exist, return false with doing nothing
        return false;
    }

    /**
     * Returns the birthday of the address. Returns null if the birthday
     * is not initialized.
     * @return the birthday of the address. Null if the birthday is not
     * initialized.
     */
    public Calendar getBirthday()
    {
        return birthday;
    }

    /**
     * Sets the memo of the contact.
     * @param memo the memo of the contact.
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * Gets the memo of the contact.
     * @return the memo of the contact.
     */
    public String getMemo() {
        return memo;
    }

    @Override
    /**
     * Returns an iterator over the elements in the phone number list in proper
     * sequence.
     * @return an iterator over the elements in the phone number list in proper
     * sequence.
     */
    public Iterator<PhoneNumber> iterator() {
        return phoneNumberList.iterator();
    }


    ////////////////////////////////////////////////////////////////////////////
	/* EXCEPTIONS */

    /**
     * This exception is thrown by Contact object if failed to load data from
     * the XML files.
     *
     * @author Kang Seung Won
     */
    @SuppressWarnings("serial")
    public static class WrongSyntaxException extends Exception
    {

        /**
         * Constructs a exception representing the number is wrong syntax.
         */
        public WrongSyntaxException()
        {
            super("XML Load Failed - Wrong Address Element syntax!");

        }
    }


    ////////////////////////////////////////////////////////////////////////////

}
