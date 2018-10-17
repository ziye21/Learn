package start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocksdb.service.RocksDBServiceImpl;
import util.ShortStr;
import util.StrUtil;

/**
 * @author yangshuai
 * @date 2018/10/11 9:46
 */
public class RocksDBTest {

    private final static Logger logger = LoggerFactory.getLogger("RocksDBTest");

    /**
     * 保存
     * @param num
     */
    public void save(int num, int charNum){
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            try {
                String str = getStr(ShortStr.toStr(i),charNum);
                int n = StrUtil.getNum(2);
                RocksDBServiceImpl.putKeyValue(str.getBytes(),String.valueOf(n).getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        logger.info("RocksDB写入" + num + "条耗时：" + (end - start) + "毫秒");
    }

    /**
     * 遍历key读取
     * @param num
     */
    public void read(int num, int charNum){
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            try {
                String str = getStr(ShortStr.toStr(i),charNum);
                byte[] b = RocksDBServiceImpl.getValue(str.getBytes());
                logger.info(str + "：" + new String(b));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        logger.info("RocksDB遍历key读取" + num + "条耗时：" + (end - start) + "毫秒");
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

    public static void main(String[] args) {
        int num = 1000000;
        int charNum = 4;
        RocksDBTest rocksDBTest = new RocksDBTest();
        rocksDBTest.save(num,charNum);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rocksDBTest.read(num,charNum);
    }

}
