package com.bio.cer.util;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

public class SampleNoUtil {
    private static final String DATA_PATTERN = "yyyyMMddHHmmss";

    public static void main(String[] args) {
     for (int i=0;i<26;i++){
         System.out.println(geneSampleNo(i));
     }

    }

    //2023-12-38:12:23:34
    public static String geneSampleNo(int num) {
        StringBuffer stringBuffer = new StringBuffer("");
        String datetime = DateUtil.format(new Date(), DATA_PATTERN).substring(4);
        char[] c = new char[datetime.length() / 2];
        for (int i = 0; i < datetime.length() / 2; i++) {
            int currentNum = Integer.parseInt(datetime.substring(i, (i + 1) * 2));
            c[i] = (char) (currentNum % 26 + 'A');
        }
        if (num / 26 < datetime.length() / 2) {
            for (int j = 0; j < c.length; j++) {
                char temp = c[0];
                c[0] = c[num / 26];
                c[num / 26] = temp;
            }
        }
        for (int i = 0; i < c.length; i++) {
            stringBuffer.append(c[i]);
        }
        stringBuffer.append((char) (num % 26 + 'A'));
        return stringBuffer.toString();
    }

    


}
