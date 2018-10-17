package start;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

/**
 * @Description：
 * @Company：开普互联
 * @author：baixy@ucap.com.cn
 * @date：2018/10/17
 */
public class HandFile {

    private final static Logger logger = LoggerFactory.getLogger("HandFile");

    private static Queue<String> beanQueue = new LinkedBlockingDeque<String>();

    private static final String outPath = "D:\\data\\deal\\";
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
            if (f.isDirectory()) {
                if (f.isFile()) {
                   //读取文件数据
                    beanQueue.add(f.getPath());
                } else {
                    getAllFilePaths(f);
                }
            } else {
                if (f.isFile()) {
                   //读取文件数据
                    beanQueue.add(f.getPath());
                }
            }
        }
    }

    /**
     * 处理单个文件
     * @param input
     * @throws Exception
     */
    private static void dealFile(File input) throws Exception{
            InputStream is = new FileInputStream(input);
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String temp = "";

            String path = outPath+input.getName();
            File output = new File(path);
            //文件不存在说明第一次创建
            boolean flag = false;
            if(!output.exists()){
                flag = true;
                output.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(output,true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            while((temp=br.readLine())!=null){
                JSONObject json = JSONObject.parseObject(temp);
                if(!StringUtils.isEmpty(json.getString("url")) && !StringUtils.isEmpty(json.getString("tl"))
                        && !StringUtils.isEmpty(json.getString("pd")) && !StringUtils.isEmpty(json.getString("aTxt"))){
                    String content = "";
                    if(flag){//第一特殊处理
                        content="["+temp;
                        flag =false;
                    }else {
                        content=",\n"+temp;
                    }
                    bw.write(content);
                }
            }
            bw.write("]");
            bw.flush();
            bw.close();
            fos.close();
            br.close();
            is.close();
    }

    public static void main(String[] args) {

        getAllFilePaths(new File("D:\\data\\old"));

        Executor executor = Executors.newFixedThreadPool(3);
        executor.execute(new Thread(new Start()));
        executor.execute(new Thread(new Start()));
        executor.execute(new Thread(new Start()));

    }

    private static class Start implements Runnable {
        @Override
        public void run() {
            try{
                while(beanQueue.size()>0){
                    System.out.println("队列剩余数据："+beanQueue.size());
                    String path = beanQueue.poll();
                    if(!StringUtils.isEmpty(path)){
                        dealFile(new File(path));
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
