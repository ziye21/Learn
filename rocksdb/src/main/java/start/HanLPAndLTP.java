package start;

import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import db.mongo.Mongodb;
import io.github.yizhiru.thulac4j.POSTagger;
import io.github.yizhiru.thulac4j.SPChineseTokenizer;
import io.github.yizhiru.thulac4j.Segmenter;
import io.github.yizhiru.thulac4j.term.TokenItem;
import io.github.yizhiru.thulac4j.util.ChineseUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * HanLP、LTP、THULAC三种分词情况对比
 *
 * @author yangshuai
 * @date 2018/10/16 10:35
 */
public class HanLPAndLTP {

    private final static Logger logger = LoggerFactory.getLogger("HanLPAndLTP");

    /**
     * 睡眠时间
     */
    private static final int sleepTime = 1000;

    /**
     * 待分词文章路径队列
     */
    private static Queue<String> beanQueue = new LinkedBlockingQueue<>();

    /**
     * 队列里最大文档数
     */
    private static final int BEAN_QUEUE_MAX = 100;

    /**
     * 保存结果最大值
     */
    private static final int SAVE_QUEUE_MAX = 1000;

    /**
     * 待保存分词结果队列
     */
    private static Queue<Document> insertQueue = new LinkedBlockingQueue<>();

    private static MongoDatabase mongoUnshardDatabase = Mongodb.getUnshardMongoDb().getDatabase("cloud_db");

    /**
     * D:/4/bm
     */
    private static final String readFile = "/data/spark/article2";

    /**
     * 清华分词模型地址
     */
    private static final String THULAC="E:\\workspace-idea\\Learn\\Learns\\rocksdb\\build\\libs\\";

    public static void main(String[] args) {
        try {
            //1、获取文章路径并记录文章数
            Thread queryDataThread = new Thread(new ReadData());
            queryDataThread.start();
            Thread.sleep(sleepTime);

            //2、LTP分句然后记录LTP、HanLP、THULAC分词情况
            for (int i = 0; i < 1; i++) {
                Thread analyzeThread = new Thread(new Start());
                analyzeThread.start();
            }
            Thread.sleep(sleepTime);

            //3、分词结果批量保存到mongo
            Thread wordCountThread = new Thread(new SaveResult());
            wordCountThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文章存放目录放入队列并计数
     */
    private static class ReadData implements Runnable {
        @Override
        public void run() {
            try {
                getAllFilePaths(new File(readFile));
            } catch (Exception e) {
                logger.error("读取文章目录异常---》》" + e);
            }
        }
    }

    /**
     * 读取文章存放目录放入队列并计数
     *
     * @param filePath
     */
    private static void getAllFilePaths(File filePath) {
        File[] files = filePath.listFiles();
        String str = "";
        for (File f : files) {
            str = f.getPath();
            logger.error(str);
            while (beanQueue.size() >= BEAN_QUEUE_MAX) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (f.isDirectory()) {
                if (f.isFile()) {
                    beanQueue.add(f.getPath());
                } else {
                    getAllFilePaths(f);
                }
            } else {
                if (f.isFile()) {
                    beanQueue.add(str);
                }
            }
        }
    }

    /**
     * 对比任务开启，结果加到待保存集
     */
    private static class Start implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    if (beanQueue != null && beanQueue.size() > 0) {
                        String path = beanQueue.poll();
                        if (StringUtils.isNotEmpty(path)) {
                            FileReader fr = new FileReader(path);
                            LineNumberReader lnr = new LineNumberReader(fr);
                            while (lnr.readLine() != null) {
                                try {
                                    // 获取文章
                                    JSONObject jsonObject = JSONObject.parseObject(lnr.readLine());
                                    String article = jsonObject.getString("aTxt");
                                    // 分句
                                    ArrayList<String> sents = sentenceSplit(article);
                                    for (String sent : sents) {
                                        Document d = new Document();
                                        d.put("path", path);
                                        d.put("aNum", article.length());
                                        d.put("sentNum", sents.size());
                                        d.put("sent", sent);
                                        try {
                                            // 分词
                                            d = HanLP(d, sent);
                                            d = LTP(d, sent);
                                            d = THULAC(d, sent);
                                        } catch (Exception e) {
                                            logger.error("分词文章句子分词异常---》》" + sent + "|" + e);
                                        }
                                        insertQueue.add(d);
                                    }
                                } catch (Exception e) {
                                    logger.error("分词文章读取或分句异常---》》" + path + "|" + e);
                                }
                            }
                        }
                    } else {
                        Thread.sleep(sleepTime);
                    }
                } catch (Exception e) {
                    logger.error("分词处理异常---》》" + e);
                }
            }
        }
    }

    /**
     * 分词结果批量保存到mongo，等待30s各队列都为空退出
     */
    private static class SaveResult implements Runnable {
        @Override
        public void run() {
            List<InsertOneModel<Document>> documents = new ArrayList<>();
            while (true) {
                try {
                    if (insertQueue.size() == 0) {
                        Thread.sleep(sleepTime);
                    } else {
                        Document d = insertQueue.poll();
                        if (d != null) {
                            documents.add(new InsertOneModel<>(d));
                        }
                        if (documents != null && documents.size() >= SAVE_QUEUE_MAX) {
                            mongoUnshardDatabase.getCollection("z_ys").bulkWrite(documents);
                            documents.clear();
                        }
                    }
                    if (beanQueue.size() == 0 && insertQueue.size() == 0) {
                        Thread.sleep(sleepTime * 10 * 3);
                        if (beanQueue.size() == 0 && insertQueue.size() == 0) {
                            if (documents != null && documents.size() > 0) {
                                mongoUnshardDatabase.getCollection("z_ys").bulkWrite(documents);
                                documents.clear();
                            }
                            System.exit(0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * LTP分句
     *
     * @param article
     */
    private static ArrayList<String> sentenceSplit(String article) {
        ArrayList<String> sents = new ArrayList<String>();
        System.loadLibrary("splitsnt");
        //SplitSentence.splitSentence(article, sents); fixme
        return sents;
    }

    /**
     * LTP分词，词性转换（北大）
     *
     * @param d
     * @param str
     */
    private static Document LTP(Document d, String str) {
        long start = System.currentTimeMillis();
        // fixme 分词
        long end = System.currentTimeMillis();
        String[] ls = {};
        String[] lg1 = {};
        String[] lg2 = {};
        // fixme 分词结果处理
        d.put("lt", end - start);
        d.put("ls", ls);
        d.put("lg1", lg1);
        d.put("lg2", lg2);
        return d;
    }

    /**
     * HanLP分词，词性转换（北大）
     * ht：分词耗时毫秒；hs：分词结果数组；hg1：词性数组；hg2：北大词性数组
     *
     * @param d
     * @param str
     */
    private static Document HanLP(Document d, String str) {
        long start = System.currentTimeMillis();
        Segment segment = HanLP.newSegment();
        // 自动识别地名，标注为ns:
        segment.enablePlaceRecognize(true);
        // 自动识别机构名，标注为nt:
        segment.enableOrganizationRecognize(true);
        //开启人名识别
        segment.enableNameRecognize(true);
        //开启词性标注
        segment.enablePartOfSpeechTagging(true);

        List<Term> termList = segment.seg(str);
        long end = System.currentTimeMillis();

        String[] hs = {};
        String[] hg1 = {};
        String[] hg2 = {};
        for (int i = 0; i < termList.size(); i++) {
            Term t = termList.get(i);
            hs[i] = t.word;
            hg1[i] = t.nature.toString();
            hg2[i] = t.nature.toString();//fixme 词性转换
        }
        d.put("ht", end - start);
        d.put("hs", hs);
        d.put("hg1", hg1);
        d.put("hg2", hg2);
        return d;
    }

    /**
     * THULAC分词，词性转换（北大）
     * tt：分词耗时毫秒；ts：分词结果数组；tg1：词性数组；tg2：北大词性数组
     * @param d
     * @param str
     */
    private static Document THULAC(Document d, String str) {
        long start = System.currentTimeMillis();
        List<TokenItem> wordsList = new ArrayList<TokenItem>();
        try {
            String weightPath = THULAC + "models/model_c_model.bin";
            String featurePath = THULAC + "models/model_c_dat.bin";
            POSTagger pos = new POSTagger(weightPath, featurePath);
            //分词器是关闭书名号内黏词，如需开启则
            //pos.enableTitleWord();

            // 添加自定义词典 注意词典后一次添加会覆盖掉前一次
            //pos.addUserWords(new ArrayList<String>());

            //繁体转简体：
            //String s = ChineseUtils.simplified("世界商機大發現");

            //停用词过滤：
            //pos.enableFilterStopWords();
            wordsList = pos.tokenize(str);
        }catch (Exception e){
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        String[] ts = new String[wordsList.size()];
        String[] tg1 = new String[wordsList.size()];
        String[] tg2 = new String[wordsList.size()];
        for (int i = 0; i < wordsList.size(); i++) {
            TokenItem t = wordsList.get(i);
            ts[i] = t.word;
            tg1[i] = t.pos;
            tg2[i] = t.pos;//fixme 词性转换
        }
        d.put("tt", end - start);
        d.put("ts", ts);
        d.put("tg1", tg1);
        d.put("tg2", tg2);
        return d;
    }



}
