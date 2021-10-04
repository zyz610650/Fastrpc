package com.fastrpc.adaptive;

/**
 * @author: @zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
public class AdaptiveTest1 {
    public static void main(String[] args) {
        String[] value;

        char[] charArray = AdaptiveTest1.class.getSimpleName().toCharArray();
        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isUpperCase(charArray[i])) {
                if (i != 0) {
                    sb.append(".");
                }
                sb.append(Character.toLowerCase(charArray[i]));
            } else {
                sb.append(charArray[i]);
            }
        }
        value = new String[]{sb.toString()};
        for (String str:value)
            System.out.println(str);
    }
}
