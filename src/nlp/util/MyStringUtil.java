package nlp.util;

public class MyStringUtil {

    static public String ArrayToString(String[] str, String separator) {
        String resultStr = "";
        for (int i = 0; i < str.length; i++) {
            if (i != str.length - 1) {
                resultStr += str[i] + separator + " ";
            } else {
                resultStr += str[i];
            }
        }
        return resultStr;
    }

    static public String[] StringToArray(String str, String... separator) {
        String separatorRegex = "[" + ArrayToString(separator, "") + "]";
        return str.split(separatorRegex);
    }

    public static boolean isCapitalize(String s) {
        return Character.isUpperCase(s.charAt(0));
    }

    public static boolean isUncapitalize(String s) {
        return Character.isLowerCase(s.charAt(0));
    }

    public static boolean isUpperWord(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isUpperCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    public static String unCapitalize(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
    
    public static void main(String[] args) {
        System.out.println(MyStringUtil.unCapitalize("Ong_ay"));
    }
}
