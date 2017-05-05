package com.devchallenge.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
    public static Integer getLastDigits(String str){
        Pattern p = Pattern.compile("[0-9]+$");
        Matcher m = p.matcher(str);
        if(m.find()) {
            return Integer.parseInt(m.group());
        }
        throw new IllegalStateException("Cannot find digits to parse");
        //TODO: handle
    }

}
