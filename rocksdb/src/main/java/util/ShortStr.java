package util;

/**
 * 62进制的短字符串
 * @author yangshuai
 * @date 2018/10/11 17:49
 */
public class ShortStr {

    private static final char[] str = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static final String _str = new String(str);

    static int len = str.length;

    public static String toStr(long num) {
        if(num < len){
            return "" + str[(int)num];
        }
        int a = (int)num % len;
        int b = (int)num / len;
        char e = str[a];
        if(b >= len){
            return "" + toStr(b) + e;
        }else{
            return "" + str[b] + e;
        }
    }

    public static long toNum(String s) {
        int a = s.length() - 1;
        long val = 0;
        for(int i = a; i >= 0; i--){
            char c = s.charAt(i);
            val += (_str.indexOf(c) * Math.pow(len, a - i));
        }
        return val;
    }

    public static void main(String[] args) {
        long num=193530;
        String enCode = toStr(num);
        long deCode = toNum(enCode);
        System.out.println("短Id是："+enCode);
        System.out.println("Id是："+deCode);
    }

}
