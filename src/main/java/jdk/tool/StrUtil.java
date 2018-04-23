package jdk.tool;

public class StrUtil {
    public static String firstLetter(String name) {

        return name.toLowerCase();
    }

    public static String notFirstLetter(String name) {
        String first = name.substring(0, 1).toUpperCase();
        String rest = name.substring(1, name.length());
        String newStr = new StringBuffer(first).append(rest).toString();
        return newStr;
    }

    public static String tableNameRender(String name) {
        String reStr = "";
        String[] aa = name.split("_");
        for (int i = 0; i < aa.length; i++) {
            String a = aa[i];
            a = a.toLowerCase();
            reStr += notFirstLetter(a);
        }
        return reStr;
    }

    public static String colNameRender(String name) {
        String reStr = "";
        String[] aa = name.split("_");
        for (int i = 0; i < aa.length; i++) {
            String a = aa[i];
            if (i == 0) {
                reStr += firstLetter(a);
            } else {
                a = a.toLowerCase();
                reStr += notFirstLetter(a);
            }
        }
        return reStr;
    }

    public static boolean isEmpty(String s) {
        if (s == null) {
            return true;
        }
        if (s.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

}
