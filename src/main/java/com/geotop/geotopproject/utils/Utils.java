package com.geotop.geotopproject.utils;


import com.geotop.geotopproject.exception.ExceptionMessageConstants;

import java.util.Iterator;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Utils {

    public static String generateID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String wrapBoundaries(String word) {
        return "(.*)(\\b" + word + "\\b)(.*)";
    }

    public static String decodeUnicode(String in){
        String working = in;
        int index;
        index = working.indexOf("\\u");
        while(index > -1) {
            int length = working.length();
            if(index > (length-6))break;
            int numStart = index + 2;
            int numFinish = numStart + 4;
            String substring = working.substring(numStart, numFinish);
            int number = Integer.parseInt(substring,16);
            String stringStart = working.substring(0, index);
            String stringEnd   = working.substring(numFinish);
            working = stringStart + ((char)number) + stringEnd;
            index = working.indexOf("\\u");
        }
        return working;
    }

    public static <T> T nvl(T arg0, T arg1) {
        return (arg0 == null) ? arg1 : arg0;
    }

    public static <T> T getSingle(List<T> list) throws RuntimeException {
        Iterator<T> iterator = list.iterator();
        if (!iterator.hasNext()) {
            throw new RuntimeException(String.format(ExceptionMessageConstants.EMPTY_COLLECTION, list.getClass().getName()));
        }
        T element = iterator.next();
        if (iterator.hasNext()) {
            throw new RuntimeException(String.format(ExceptionMessageConstants.IS_NOT_SINGLE, list.getClass().getName()));
        }
        return element;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double average(Double... args) {
        Double sum = 0.0;
        for (Double arg : args) {
            if (arg != null) {
                sum += arg;
            }
        }

        return round(sum / args.length, 2);
    }
}
