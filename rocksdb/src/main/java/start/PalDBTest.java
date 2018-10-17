package start;

import com.linkedin.paldb.api.Configuration;
import com.linkedin.paldb.api.PalDB;
import com.linkedin.paldb.api.StoreReader;
import com.linkedin.paldb.api.StoreWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ShortStr;
import util.StrUtil;

import java.io.File;
import java.util.Map;

/**
 * paldb不支持在已有的文件中新增数据
 * @author yangshuai
 * @date 2018/10/11 9:46
 */
public class PalDBTest {

    private final static Logger logger = LoggerFactory.getLogger("PalDBTest");

    /**
     * paldb41  4字符100万
     * paldb42  4字符1000万
     * paldb81  8字符100万
     * paldb82  8字符1000万
     */
    private final String PATH = "/db/paldb41/store.paldb";

    /**
     * 保存
     * @param num
     */
    public void save(int num, int charNum){
        Configuration config = PalDB.newConfiguration();
        config.set(Configuration.CACHE_ENABLED, "true");
        File file = new File(PATH);
        StoreWriter writer = PalDB.createWriter(file,config);
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            try {
                String str = getStr(ShortStr.toStr(i),charNum);
                int n = StrUtil.getNum(2);
                writer.put(str, n);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        writer.close();
        long end = System.currentTimeMillis();
        logger.info("PalDB写入" + num + "条耗时：" + (end - start) + "毫秒");
    }

    /**
     * 遍历key读取
     * String val1 = reader.get("foo");
     * int[] val2 = reader.get(1213);
     * @param num
     */
    public void read(int num, int charNum){
        long start = System.currentTimeMillis();
        StoreReader reader = PalDB.createReader(new File(PATH));
        for (int i = 0; i < num; i++) {
            try {
                String str = getStr(ShortStr.toStr(i),charNum);
                logger.info(str + "：" + reader.get(str));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        reader.close();
        long end = System.currentTimeMillis();
        logger.info("PalDB遍历key读取" + num + "条耗时：" + (end - start) + "毫秒");
    }

    /**
     * 遍历读取
     */
    public void iter(int num){
        long start = System.currentTimeMillis();
        StoreReader reader = PalDB.createReader(new File(PATH));
        Iterable<Map.Entry<String, String>> iterable = reader.iterable();
        long end = System.currentTimeMillis();
        logger.info("PalDB读取" + num + "条耗时：" + (end - start) + "毫秒？");
        for (Map.Entry<String, String> entry : iterable) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            logger.info(key + "：" + value);
        }
        reader.close();
        long end2 = System.currentTimeMillis();
        logger.info("PalDB批量遍历读取输出" + num + "条耗时：" + (end2 - start) + "毫秒");
    }

    /**
     * 补全字符
     * @param str
     * @param charNum
     * @return
     */
    private String getStr(String str, int charNum){
        if (charNum == 4) {
            if (str.length() == 1) {
                str = "000" + str;
            } else if (str.length() == 2) {
                str = "00" + str;
            } else if (str.length() == 3) {
                str = "0" + str;
            }
        } else {
            if (str.length() == 1) {
                str = "0000000" + str;
            } else if (str.length() == 2) {
                str = "000000" + str;
            } else if (str.length() == 3) {
                str = "00000" + str;
            } else if (str.length() == 4) {
                str = "0000" + str;
            } else if (str.length() == 5) {
                str = "000" + str;
            } else if (str.length() == 6) {
                str = "00" + str;
            } else if (str.length() == 7) {
                str = "0" + str;
            }
        }
        return str;
    }

    public static void main(String[] args){
        PalDBTest palDBTest = new PalDBTest();
        int num = 1000000;
        int charNum = 4;
        palDBTest.save(num,charNum);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        palDBTest.read(num,charNum);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        palDBTest.iter(num);
    }

}
