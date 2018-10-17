package rocksdb.conf;

/**
 * 提供RocksDB的信息
 * Created by admin on 2018/4/4.
 */
public class RocksConstant {

    /**
     * rocks41  4字符100万
     * rocks42  4字符1000万
     * rocks81  8字符100万
     * rocks82  8字符1000万
     * rocksDB的文件路径
     */
    public static final String ROCKS_DB_PATH = "/db/rocks41/rocksdb_datas";

    /**
     * rocksDB数据存储的columnFamily名称：存放link和link_upper的sc+md5,用来排重
     */
    public static final String COLUMN_FAMILY_NAME = "cloudEngine";

    /**
     * rocksDB数据存储的columnFamily名称:存放网页hash值
     */
    public static final String COLUMN_FAMILY_NAME2 = "siteHash";
}
