package util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * @author yangshuai
 * @date 2018/4/23 17:10
 */
public class PropertiesUtil {

    private static PropertiesUtil ec;

    private static Hashtable<String, Object> register = new Hashtable();

    public static PropertiesUtil getInstance() {
        if (ec == null) {
            ec = new PropertiesUtil();
        }
        return ec;
    }

    public static Properties getProperties(String fileName) {
        InputStream is = null;
        Properties p = null;
        try {
            p = (Properties) register.get(fileName);
            if (p == null) {
                try {
                    is = new FileInputStream(fileName);
                } catch (Exception e) {
                    if (fileName.startsWith("/")) {
                        is = PropertiesUtil.class.getResourceAsStream(fileName);
                    } else {
                        is = PropertiesUtil.class.getResourceAsStream("/" + fileName);
                    }
                }
                p = new Properties();
                p.load(is);
                register.put(fileName, p);
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return p;
    }

    public String getPropertyValue(String fileName, String strKey) {
        Properties p = getProperties(fileName);
        try {
            return p.getProperty(strKey);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return null;
    }

    public static void main(String[] args) {
        Properties p = getProperties("db.properties");
        System.out.println(p.getProperty("count"));
    }

}
