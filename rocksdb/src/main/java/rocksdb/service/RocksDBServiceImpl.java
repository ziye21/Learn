package rocksdb.service;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/4/3.
 */
public class RocksDBServiceImpl {

    /**
     * 判断数据库路径文件夹中是否有指定的columnFamily表
     * @param dbPath rocksdb的数据库文件目录
     * @param table columnFamily表名
     * @return
     */
    public static boolean doesColumnFamilyExists(String dbPath, String table) {
        return RocksDBUtil.getInstance().doesColumnFamilyExists(dbPath, table);
    }

    /**
     * 获取当前所有可用的columnFamily名称集合
     * @param dbPath rocksdb的数据库文件目录
     * @return
     */
    public static String[] getColumnFamilyList(String dbPath) {
        return RocksDBUtil.getInstance().getColumnFamilyList(dbPath);
    }

    /**
     * 删除cloudEngine的ColumnFamily
     */
    public static boolean deleteCloudEngine() {
        return RocksDBUtil.getInstance().deleteCloudEngine();
    }

    /**
     * 创建cloudEngine的ColumnFamily
     */
    public static boolean createCloudEngine() {
        return RocksDBUtil.getInstance().createCloudEngine();
    }

    /******************以下ColumnFamily名称为：RocksConstant.COLUMN_FAMILY_NAME 的接口方法**********************/
    /**
     * link去重：保存键
     * @param key siteCode+md5，数据类型是byte数组
     */
    public static void put(byte[] key) {
        RocksDBUtil.getInstance().put(key);
    }

    /**
     * link去重：批量保存键
     * @param keys 【siteCode+md5】的集合，集合内部类型为byte数组
     */
    public static void multiPut(List<byte[]> keys) {
        RocksDBUtil.getInstance().multiPut(keys);
    }

    /**
     * link去重：判断key对应的值是否存在
     * @param key siteCode+md5，数据类型是byte数组
     * @return
     */
    public static boolean get(byte[] key) {
        return RocksDBUtil.getInstance().get(key);
    }

    /**
     * link去重：批量校验key是否存在
     * @param keys 【siteCode+md5】的集合，集合内部类型为byte数组
     * @return 返回存在的key集合
     */
    public static List<byte[]> multiGet(List<byte[]> keys) {
        return RocksDBUtil.getInstance().multiGet(keys);
    }

    /**
     * link去重：删除键
     * @param key siteCode+md5，数据类型是byte数组
     */
    public static void deleteKV(byte[] key) {
        RocksDBUtil.getInstance().deleteKV(key);
    }

    /******************以下ColumnFamily名称为：RocksConstant.COLUMN_FAMILY_NAME2 的接口方法**********************/
    /**
     * 网页hash去重：保存键值对
     * @param key
     * @param value
     */
    public static void putKeyValue(byte[] key, byte[] value) {
        RocksDBUtil.getInstance().putKeyValue(key, value);
    }

    /**
     * 网页hash去重：批量保存键值对
     * @param map
     */
    public static void multiPutKeyValue(Map<byte[], byte[]> map) {
        RocksDBUtil.getInstance().multiPutKeyValue(map);
    }

    /**
     * 网页hash去重：获取key对应的值
     * @param key
     * @return
     */
    public static byte[] getValue(byte[] key) {
        return RocksDBUtil.getInstance().getValue(key);
    }

    /**
     * 网页hash去重：批量获取key的值
     * @param keys
     * @return 返回键值对map
     */
    public static Map<byte[], byte[]> multiGetKeyValue(List<byte[]> keys) {
        return RocksDBUtil.getInstance().multiGetKeyValue(keys);
    }

    /**
     * 网页hash去重：删除键
     * @param key
     */
    public static void deleteKeyValue(byte[] key) {
        RocksDBUtil.getInstance().deleteKeyValue(key);
    }
}
