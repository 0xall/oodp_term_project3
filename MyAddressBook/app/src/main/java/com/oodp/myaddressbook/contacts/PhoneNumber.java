package com.oodp.myaddressbook.contacts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*******************************************************************************
 *
 *  The PhoneNumber class represents the number in the phone. It can save all
 * possible address number and sign exception if it is not possible address
 * number.
 *
 * @author Lee Ho Jun
 *
 ******************************************************************************/

public class PhoneNumber implements Comparable<PhoneNumber> {

    ////////////////////////////////////////////////////////////////////////////
	/* CONSTANT Variables */

    /**
     *  Regular expression for checking the phone number string is right syntax.
     *  Only numbers, hyphens, and space are allowed.
     */
    public static final String PHONE_NUMBER_REGULAR_EXPRESSION = "^[0-9\\-\\s]*$";

    /**
     *  regular expression for dividing area or phone code from phone number
     */
    private static final String AREA_PHONE_CODE_DIVIDER =
            "^((0|82)?(51|53|32|62|42|52|44|31|33|43|41|63|61|54" +
                    "|55|64|10|11|12|13|14|15|16|17|18|19))?([0-9\\-]*)$";

    public static final int NUMBER_OF_TYPES = 3;
    public static final int TYPE_PHONE_NUMBER = 0;
    public static final int TYPE_HOME_NUMBER = 1;
    public static final int TYPE_WORK_NUMBER = 2;


    ////////////////////////////////////////////////////////////////////////////
	/* MEMBER Variables */

	private int             phoneType;
    private String 			phoneNumber;


    ////////////////////////////////////////////////////////////////////////////
	/* CONSTRUCTORS */

    /**
     * Constructs a phone number with empty number.
     */
    public PhoneNumber()
    {
        // make instance with empty number
        phoneType = 0;
        phoneNumber = "";
    }


    /**
     * Constructs a phone number with a given number.
     * @param phoneNumber The phone number string.
     * @throws WrongSyntaxException Signal if the phone number string is wrong syntax.
     */
    public PhoneNumber(String phoneNumber) throws WrongSyntaxException
    {
        setPhoneNumber(phoneNumber);
    }

    public PhoneNumber(int phoneType, String phoneNumber) throws WrongSyntaxException
    {
        setPhoneType(phoneType);
        setPhoneNumber(phoneNumber);
    }

    /**
     * Initializes a newly created PhoneNumber object so that it represents the same
     * sequence of phone numbers as the argument.
     * @param phoneNumber the PhoneNumber object to copy.
     */
    public PhoneNumber(PhoneNumber phoneNumber)
    {
        setPhoneNumber(phoneNumber);
    }

    ////////////////////////////////////////////////////////////////////////////////
	/* METHODS */

    /**
     * Sets the phone number. Note that if wrong syntax, it throw the exception.
     * @param phoneNumber The phone number string.
     * @throws WrongSyntaxException Signal if the phone number string is wrong syntax.
     */
    public void setPhoneNumber(String phoneNumber) throws WrongSyntaxException
    {
        // if right syntax,
        if(phoneNumber.matches(PHONE_NUMBER_REGULAR_EXPRESSION))
        {
            // set phone number with removing all spaces
            this.phoneNumber = phoneNumber;
            normalize();
        }

        else	// if wrong syntax,
        {
            // throw exception
            throw new WrongSyntaxException();
        }
    }

    /**
     * Sets the phone number by the instance of the PhoneNumber class.
     * @param phoneNumber the instance of the phone number to set.
     */
    public void setPhoneNumber(PhoneNumber phoneNumber)
    {
        if(phoneNumber != null)
        {
            this.phoneType = phoneNumber.phoneType;
            this.phoneNumber = phoneNumber.phoneNumber;
        }
    }

    /**
     * Sets the phone number type.
     * @param phoneType the phone type.
     */
    public void setPhoneType(int phoneType) throws WrongSyntaxException {
        if(phoneType >= NUMBER_OF_TYPES || phoneType < 0) throw new WrongSyntaxException();
        this.phoneType = phoneType;
    }


    /**
     * Sets the phone number to standard address number with hyphen(-).
     */
    public void normalize()
    {
        // remove all space and hyphen in the string
        phoneNumber = phoneNumber.replaceAll("[\\s\\-]+", "");

        Pattern pattern = Pattern.compile(AREA_PHONE_CODE_DIVIDER);
        Matcher codeMatcher = pattern.matcher(phoneNumber);

        if(codeMatcher.find())
        {
            // divide area or phone code from phone number
            String code = codeMatcher.group(1);
            String remain = codeMatcher.group(4);

            // the number of numbers in the phone number
            int numOfNumbers = phoneNumber.replaceAll("[^0-9]", "").length();

            // if code is not searched, it is unknown number expression,
            // so set phone number with no hyphen and end function.
            if(code == null || code == "")
            {
                phoneNumber = remain;
                return;
            }

            // subtract code length from the number of numbers
            numOfNumbers -= code.length();

            // if the number of numbers excluding the code is greater than 8,
            // it is unknown phone number expression, so set phone number with
            // no hyphen
            if(numOfNumbers > 8)
                phoneNumber = code + remain;

                // if the number of remain numbers is 0
            else if(numOfNumbers == 0)
                phoneNumber = code;

                // if the number of remain numbers is between 1 and 3
                // express it like xxx-xx
            else if(numOfNumbers >= 1 && numOfNumbers <= 3)
                phoneNumber = code + "-" + remain;

                // if the number of remain numbers is between 4 and 7
                // express it like xxx-xxx-xxxx
            else if(numOfNumbers > 3 && numOfNumbers <= 7)
                phoneNumber = code + "-" + remain.substring(0, 3) + "-" + remain.substring(3);

                // if the number of remain numbers is 8
                // express it like xxx-xxxx-xxxx
            else if(numOfNumbers == 8)
                phoneNumber = code + "-" + remain.substring(0, 4) + "-" + remain.substring(4);
        }
    }


    /**
     * Tests if the number is empty.
     * @return true if and only if the phone number is empty.
     */
    public boolean isEmpty()
    {
        if(phoneNumber == "") return true;
        else return false;
    }


    /**
     * Gets the phone number.
     * @return phone number.
     */
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    /**
     * Gets the phone number with no hyphens.
     * @return phone number with no hyphens.
     */
    public String getPhoneNumberWithOnlyNumber() {
        return phoneNumber.replaceAll("[\\-\\s]+", "");
    }

    /**
     * Gets the phone number type.
     * @return phone number type.
     */
    public int getPhoneType() {
        return phoneType;
    }

    /**
     * Returns a phone number.
     * @return phone number.
     */
    public String toString()
    {
        return phoneNumber;
    }

    /**
     * Compares two phone number for ordering.
     * @param phoneNumber The phone number to be compared.
     * @return the value 0 if this phone number is equal to the argument phone
     * number; a value less than 0 if this phone number is lexicographically
     * less than the argument phone number; a value greater than 0 if this
     * phone number is lexicographically greater than the argument phone number.
     */
    public int compareTo(PhoneNumber phoneNumber)
    {
        return this.phoneNumber.compareTo(phoneNumber.phoneNumber);
    }

    public int hashCode()
    {
        int hashCode = 0, index = phoneNumber.length() - 1;
        int checkCount = 0;

        while(index >= 0 && checkCount < 4)
        {
            char ch = phoneNumber.charAt(index);
            if(ch >= '0' && ch <= '9')
            {
                checkCount++;
                hashCode = (ch - '0') + hashCode * 10;
            }

            --index;
        }

        return hashCode;
    }

    /**
     * Compares this phone number to the specified object. The result is true if
     * and only if the argument is instance of PhoneNumber or String and phone
     * number is equals to this phone number.
     * @param obj The object to compare this phone number against
     * @return true if the argument is instance of PhoneNumber or String and the
     * phone number is equals to this phone number, and false otherwise.
     */
    public boolean equals(Object obj)
    {
        return this.phoneNumber.equals(obj.toString());
    }


    ////////////////////////////////////////////////////////////////////////////
	/* EXCEPTIONS */

    /**
     * This exception is thrown by the PhoneNumber if it is wrong syntax when
     * you set or change the phone number. The phone number is only allowed
     * numbers(0~9), hyphens(-), and spaces.
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
            super("Wrong phone number syntax!");
        }
    }

    ////////////////////////////////////////////////////////////////////////////

}
