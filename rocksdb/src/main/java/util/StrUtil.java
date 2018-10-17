package util;

/**
 * 随机字符串
 * @author yangshuai
 * @date 2018/10/11 14:27
 */
public class StrUtil {

    private final static String STR = "0123456789abcdefghijkmlnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final static String NUM = "0123456789";

    /**
     * 随机获取字符串
     * @param num
     * @return
     */
    public static String getStr(int num){
        String Result = "";
        char[] str = STR.toCharArray();
        for (int i = 0; i < num; i++) {
            int index = (int) (Math.random() * str.length);
            Result += str[index];
        }
        return Result;
    }

    /**
     * 随机获取数字
     * @param num
     * @return
     */
    public static int getNum(int num){
        if (num > 9) {
            num = 9;
        }
        String Result = "";
        char[] str = NUM.toCharArray();
        for (int i = 0; i < num; i++) {
            int index = (int) (Math.random() * str.length);
            Result += str[index];
        }
        return Integer.parseInt(Result);
    }

}
