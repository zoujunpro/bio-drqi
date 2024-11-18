package com.bio.drqi.manage.util;

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

    public static void main(String[] args) {
        System.out.println(nextLetterForInstantVerify("AF"));
    }

}
