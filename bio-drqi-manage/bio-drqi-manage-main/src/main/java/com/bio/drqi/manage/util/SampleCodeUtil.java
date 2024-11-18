package com.bio.drqi.manage.util;

import com.bio.common.core.util.StringUtils;

public class SampleCodeUtil {

    public static String nextSampleCode(String sampleCode) {
        return nextSampleCode(sampleCode, false);
    }

    public static String nextSampleCode() {
        return nextSampleCode(null, true);
    }

    public static String nextSampleCode(String sampleCode, boolean ifNextLetter) {
        if (StringUtils.isEmpty(sampleCode)) {
            return "AA01";
        }
        String letter = sampleCode.substring(0, 2);
        Integer number = Integer.valueOf(sampleCode.substring(2));
        if (number == 99) {
            return nextLetter(letter) + "01";
        } else {
            if(ifNextLetter){
                return nextLetter(letter)+"01";
            }
            return letter + StringUtils.padl(String.valueOf(number + 1), 2, '0');
        }
    }


    public static String nextLetter(String string) {
        char[] tempChar = string.toCharArray();
        for (int i = tempChar.length - 1; i >= 1; i--) {
            if (tempChar[i] < 'Z') {
                tempChar[i] = (char) (tempChar[i] + 1);
                break;
            } else {
                tempChar[i] = 'A';
                tempChar[i - 1] = (char) (tempChar[i - 1] + 1);
                if (tempChar[i - 1] <= 'z') {
                    i = 0;
                }
            }
        }
        return String.valueOf(tempChar);
    }


    public static void main(String[] args) {
        System.out.println(nextLetter("c"));
    }
}
