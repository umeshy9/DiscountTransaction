package com.umy.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// This is a test comment. I'm practicing with Git!
public class DateParser {

    public static String parseDate(String dateString){
        //String s = "17/11/2016";
        Date d = null;
        DateFormat df = new SimpleDateFormat("MM/yyyy");
        try {
            d = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String s1 = df.format(d);
        return s1; //will print 11
    }
}
