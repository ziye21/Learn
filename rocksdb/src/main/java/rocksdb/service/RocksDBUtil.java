package rocksdb.service;

import org.apache.commons.lang3.ArrayUtils;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocksdb.conf.RocksConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RocksDB单例类，提供接口，用于全站采集判断url是否已经爬取。
 * Created by wangwl on 2018/4/3.
 *          新增加用于保存网页hash，并判断。 modified at 2018-07-12
 */
public class RocksDBUtil {
    private final static Logger logger = LoggerFactory.getLogger("RocksDBUtil");

    static {
        RocksDB.loadLibrary();
    }

    private static RocksDBUtil instance = new RocksDBUtil();

    private ColumnFamilyHandle columnFamilyHandle;
    private ColumnFamilyHandle columnFamilyHandle2;

    private RocksDB rocksDB;

    private RocksDBUtil() {
        initDbAndCf(RocksConstant.ROCKS_DB_PATH, RocksConstant.COLUMN_FAMILY_NAME, RocksConstant.COLUMN_FAMILY_NAME2);
    }

    public static RocksDBUtil getInstance() {
        return instance;
    }

    /**
     * 初始化rocksDB连接和columnFamily表
     * @param dbPath db文件路径
     * @param table columnFamily名称:存放link的sc+md5
     * @param table2 columnFamily名称:存放网页的hash
     */
    private void initDbAndCf(String dbPath, String table, String table2) {
        List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>();
        Options options = new Options().setCreateIfMissing(true);

        boolean flag = false;//是否有指定的columnFamily
        boolean flag2 = false;//是否有指定的columnFamily
        List<byte[]> cfs = null;
        try {
            cfs = RocksDB.listColumnFamilies(options, dbPath);
            if(cfs.size() > 0) {
                for(byte[] cf : cfs) {
                    String cfName = new String(cf);
                    if (table.equals(cfName)) {
                        flag = true;
                    }
                    if (table2.equals(cfName)) {
                        flag2 = true;
                    }
                    columnFamilyDescriptors.add(new ColumnFamilyDescriptor(cf, new ColumnFamilyOptions()));
                }
            } else {
                columnFamilyDescriptors.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, new ColumnFamilyOptions()));
            }
        } catch (RocksDBException e) {
            logger.error("查询DB中所有cf时异常：", e);
        }

        //打开数据库中的所有columnFamily的连接
        DBOptions dbOptions = new DBOptions().setCreateIfMissing(true);
        //columnFamily控制器集合，通过它获取当前要查询的cf
        List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<>();
        try {
            rocksDB = RocksDB.open(dbOptions, dbPath, columnFamilyDescriptors, columnFamilyHandles);
        } catch (RocksDBException e) {
            logger.error("创建rocksDB连接实例时异常：", e);
        }

        //没有就创建columnFamily
        if (!flag) {
            try {
                ColumnFamilyHandle cfh = rocksDB.createColumnFamily(new ColumnFamilyDescriptor(table.getBytes(), new ColumnFamilyOptions()));
                columnFamilyHandles.add(cfh);
                columnFamilyDescriptors.add(new ColumnFamilyDescriptor(table.getBytes(), new ColumnFamilyOptions()));
            } catch (RocksDBException e) {
                logger.error("创建columnFamily1异常：", e);
            }
        }
        if (!flag2) {
            try {
                ColumnFamilyHandle cfh = rocksDB.createColumnFamily(new ColumnFamilyDescriptor(table2.getBytes(), new ColumnFamilyOptions()));
                columnFamilyHandles.add(cfh);
                columnFamilyDescriptors.add(new ColumnFamilyDescriptor(table2.getBytes(), new ColumnFamilyOptions()));
            } catch (RocksDBException e) {
                logger.error("创建columnFamily2异常：", e);
            }
        }

        //找到业务需要的columnFamily
        for(int i = 0; i < columnFamilyDescriptors.size(); i++) {
            String cfName = new String(columnFamilyDescriptors.get(i).columnFamilyName());
            if(cfName.equals(table)) {
                columnFamilyHandle = columnFamilyHandles.get(i);
            }
            if(cfName.equals(table2)) {
                columnFamilyHandle2 = columnFamilyHandles.get(i);
            }
        }
    }

    /**
     * 判断数据库路径文件夹中是否有指定的columnFamily表
     * @param dbPath rocksDB文件路径
     * @param table columnFamily名称
     * @return
     */
    public boolean doesColumnFamilyExists(String dbPath, String table) {
        Options options = new Options().setCreateIfMissing(true);

        boolean flag = false;
        List<byte[]> cfs = null;
        try {
            cfs = RocksDB.listColumnFamilies(options, dbPath);
            if (cfs.size() > 0) {
                for (byte[] cf : cfs) {
                    String tableName = new String(cf);
                    if (table.equals(tableName)) {
                        flag = true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("查询columnFamily是否存在，异常：", e);
        }
        return flag;
    }

    /**
     * 获取当前所有可用的columnFamily名称集合
     * @param dbPath
     * @return
     */
    public String[] getColumnFamilyList(String dbPath) {
        List<byte[]> cfList = null;
        try {
            cfList = RocksDB.listColumnFamilies(new Options(), dbPath);
        } catch (RocksDBException e) {
            logger.error("获取所有可用cf名称集合异常：", e);
        }
        if (cfList == null || cfList.size() == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List<String> result = new ArrayList<>(cfList.size());
        for (byte[] cf : cfList) {
            result.add(new String(cf));
        }
        return result.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /******************以下ColumnFamily名称为：RocksConstant.COLUMN_FAMILY_NAME 的接口方法**********************/

    /**
     * 删除cloudEngine的ColumnFamily
     */
    public boolean deleteCloudEngine() {
        boolean b = false;
        try {
            if (columnFamilyHandle != null) {
                rocksDB.dropColumnFamily(columnFamilyHandle);
                columnFamilyHandle = null;
                b = true;
            } else {
                logger.debug("'cloudEngine'的ColumnFamily已经不存在了！");
            }
        } catch (Exception e) {
            logger.error("删除'cloudEngine'的ColumnFamily异常：", e);
        }
        return b;
    }

    /**
     * 创建cloudEngine的ColumnFamily
     */
    public boolean createCloudEngine() {
        boolean b = false;
        try {
            if (columnFamilyHandle == null) {
                columnFamilyHandle = rocksDB.createColumnFamily(new ColumnFamilyDescriptor(RocksConstant.COLUMN_FAMILY_NAME.getBytes(), new ColumnFamilyOptions()));
                b = true;
            } else {
                logger.debug("'cloudEngine'的ColumnFamily已经存在，不需要创建！");
            }
        } catch (Exception e) {
            logger.error("创建'cloudEngine'的ColumnFamily异常：", e);
        }
        return b;
    }

    /**
     * link去重：保存键
     * @param key
     */
    public void put(byte[] key) {
        try {
            if (columnFamilyHandle != null) {
                rocksDB.put(columnFamilyHandle, key, new byte[]{});
            } else {
                logger.debug("'cloudEngine'的ColumnFamily不存在，请使用createCloudEngine()方法创建！");
            }
        } catch (RocksDBException e) {
            logger.error("RocksDB保存键key时异常：", e);
        }
    }

    /**
     * link去重：批量保存键
     * @param keys
     */
    public void multiPut(List<byte[]> keys) {
        if (columnFamilyHandle != null) {
            for (byte[] b : keys) {
                put(b);
            }
        } else {
            logger.debug("'cloudEngine'的ColumnFamily不存在，请使用createCloudEngine()方法创建！");
        }
    }

    /**
     * link去重：判断key对应的值是否存在
     * @param key
     * @return
     */
    public boolean get(byte[] key) {
        boolean value = false;
        try {
            if (columnFamilyHandle != null) {
                byte[] getValue = rocksDB.get(columnFamilyHandle, key);
                if (getValue != null) {
                    value = true;
                }
            } else {
                logger.debug("'cloudEngine'的ColumnFamily不存在，请使用createCloudEngine()方法创建！");
            }
        } catch (RocksDBException e) {
            logger.error("RocksDB判断key是否存在时异常：", e);
        }
        return value;
    }

    /**
     * link去重：批量校验key是否存在
     * @param keys
     * @return 返回存在的key集合
     */
    public List<byte[]> multiGet(List<byte[]> keys) {
        List<byte[]> list = new ArrayList<>();
        try {
            if (columnFamilyHandle != null) {
                List<ColumnFamilyHandle> handleList = new ArrayList<>();
                int len = keys.size();
                for (int i = 0; i < len; i++) {
                    handleList.add(columnFamilyHandle);
                }

                //以map形式批量获取键值对
                Map<byte[], byte[]> multiGet = rocksDB.multiGet(handleList, keys);
                for (Map.Entry<byte[], byte[]> entry : multiGet.entrySet()) {
                    list.add(entry.getKey());
                }
            } else {
                logger.debug("'cloudEngine'的ColumnFamily不存在，请使用createCloudEngine()方法创建！");
            }
        } catch (Exception e) {
            logger.error("批量校验key是否存在异常：", e);
        }
        return list;
    }

    /**
     * link去重：删除键
     * @param key
     */
    public void deleteKV(byte[] key) {
        try {
            if (columnFamilyHandle != null) {
                rocksDB.delete(columnFamilyHandle, key);
            } else {
                logger.debug("'cloudEngine'的ColumnFamily不存在，请使用createCloudEngine()方法创建！");
            }
        } catch (RocksDBException e) {
            logger.error("RocksDB删除时异常：", e);
        }
    }

    /******************以下ColumnFamily名称为：RocksConstant.COLUMN_FAMILY_NAME2 的接口方法**********************/

    /**
     * 网页hash去重：保存键值对
     * @param key
     * @param value
     */
    public void putKeyValue(byte[] key, byte[] value) {
        try {
            rocksDB.put(columnFamilyHandle2, key, value);
        } catch (RocksDBException e) {
            logger.error("RocksDB保存键值对时异常：", e);
        }
    }

    /**
     * 网页hash去重：批量保存键值对
     * @param map
     */
    public void multiPutKeyValue(Map<byte[], byte[]> map) {
        for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
            putKeyValue(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 网页hash去重：获取key对应的值
     * @param key
     * @return
     */
    public byte[] getValue(byte[] key) {
        byte[] value = null;
        try {
            value = rocksDB.get(columnFamilyHandle2, key);
        } catch (RocksDBException e) {
            logger.error("RocksDB获取key的值时异常：", e);
        }
        return value;
    }

    /**
     * 网页hash去重：批量获取key的值
     * @param keys
     * @return 返回键值对map
     */
    public Map<byte[], byte[]> multiGetKeyValue(List<byte[]> keys) {
        Map<byte[], byte[]> map = new HashMap<>();
        try {
            List<ColumnFamilyHandle> handleList = new ArrayList<>();
            int len = keys.size();
            for (int i=0;i<len;i++) {
                handleList.add(columnFamilyHandle2);
            }

            //以map形式批量获取键值对
            Map<byte[], byte[]> multiGet = rocksDB.multiGet(handleList, keys);
            for(Map.Entry<byte[], byte[]> entry : multiGet.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            logger.error("批量获取键值对异常：", e);
        }
        return map;
    }

    /**
     * 网页hash去重：删除键
     * @param key
     */
    public void deleteKeyValue(byte[] key) {
        try {
            rocksDB.delete(columnFamilyHandle2, key);
        } catch (RocksDBException e) {
            logger.error("RocksDB删除时异常：", e);
        }
    }

}
