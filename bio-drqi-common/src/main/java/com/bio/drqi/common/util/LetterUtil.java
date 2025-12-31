package com.bio.drqi.common.util;

import java.util.Random;

public class LetterUtil {
    public static String nextLetter(String str) {
        char next = (char) (str.toCharArray()[0] + 1);
        if (next > 'z') {
            return "a";
        } else {
            return String.valueOf(next);
        }
    }

    public static String nextLetterForInstantVerify(String str) {
        if (str.length() == 1) {
            char next = (char) (str.toCharArray()[0] + 1);
            if (next > 'Z') {
                return "AA";
            } else {
                return String.valueOf(next);
            }
        } else if (str.length() == 2) {
            String firstLetter = str.substring(0, 1);
            String lastLetter = str.substring(1);
            char nextLastLetter = (char) (lastLetter.toCharArray()[0] + 1);
            char nextFirstLetter = (char) (firstLetter.toCharArray()[0] + 1);
            if (nextLastLetter > 'Z') {
                return nextFirstLetter + "A";
            } else {
                return firstLetter + nextLastLetter;
            }
        }
        return null;
    }

    public static String randomLetter(Integer length){

        StringBuilder stringBuilder=new StringBuilder("");
        for (int i=0;i<length;i++){
            Random random = new Random();
            char randomLetter1 = (char) (random.nextInt(26) + 'A');
            stringBuilder.append(randomLetter1);
        }
        return  stringBuilder.toString();

    }

    public static boolean  isNumeric(String str) {
        return str.matches("\\d+");
    }

    public static void main(String[] args) {
        //System.out.println(nextLetterForInstantVerify("AZ"));
        System.out.println(isNumeric("434"));
    }

}
