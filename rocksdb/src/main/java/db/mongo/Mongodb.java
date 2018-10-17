package db.mongo;

import com.mongodb.*;
import util.PropertiesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author yangshuai
 * @date 2018/4/25 14:04
 */
public class Mongodb {

    private static Properties prop = PropertiesUtil.getProperties("db.properties");

    /**
     * 默认的mongo客户端，每次写入需要确认
     */
    private static MongoClient mongo = null;

    /**
     * 非分片表的mongo客户端，每次写入需要确认
     */
    private static MongoClient unshardMongo = null;

    /**
     * 获得mongodb官方java driver的客户端接口
     *
     * @return
     */
    public static MongoClient getMongoDb() {
        return getMongoDb(prop.getProperty("mongo.database"));
    }

    /**
     * 获得非分片的mongodb客户端接口
     *
     * @return
     */
    public static MongoClient getUnshardMongoDb() {
        return getUnshardMongoDb(prop.getProperty("unshardMongo.database"));
    }

    /**
     * 指定mongodb name,构造mongodb client
     *
     * @param dbname
     * @return
     */
    public static MongoClient getMongoDb(String dbname) {
        if (null == mongo) {
            try {
                MongoCredential credential = MongoCredential.createScramSha1Credential(prop.getProperty("mongo.username"), dbname, prop.getProperty("mongo.password").toCharArray());
                List<ServerAddress> addresses = new ArrayList<ServerAddress>();
                for (String host : prop.getProperty("mongo.host").split(",")) {
                    ServerAddress address = new ServerAddress(host, Integer.parseInt(prop.getProperty("mongo.port")));
                    addresses.add(address);
                }

                mongo = new MongoClient(addresses, Arrays.asList(credential));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mongo;
    }

    /**
     * 指定mongodb name,构造mongodb client
     *
     * @param dbname
     * @return
     */
    public static MongoClient getUnshardMongoDb(String dbname) {
        if (null == unshardMongo) {
            try {
                MongoCredential credential = MongoCredential.createScramSha1Credential(prop.getProperty("unshardMongo.username"), dbname, prop.getProperty("unshardMongo.password").toCharArray());
                List<ServerAddress> addresses = new ArrayList<ServerAddress>();
                for (String host : prop.getProperty("unshardMongo.host").split(",")) {
                    ServerAddress address = new ServerAddress(host, Integer.parseInt(prop.getProperty("unshardMongo.port")));
                    addresses.add(address);
                }

                unshardMongo = new MongoClient(addresses, Arrays.asList(credential));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return unshardMongo;
    }

    private static MongoClientOptions getConfOptions() {
        return new MongoClientOptions.Builder().socketKeepAlive(true) // 是否保持长链接
                .connectTimeout(3000) // 链接超时时间
                .socketTimeout(3000) // read数据超时时间
                .readPreference(ReadPreference.primary()) // 最近优先策略
                .connectionsPerHost(200) // 每个地址最大请求数
                .maxWaitTime(1000 * 60 * 2) // 长链接的最大等待时间
                .threadsAllowedToBlockForConnectionMultiplier(50) // 一个socket最大的等待请求数
                .writeConcern(WriteConcern.UNACKNOWLEDGED).build();  //不做写入确认
    }

}
