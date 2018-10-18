package util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public final class HttpClientUtil {

    private static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 3000;
    private static final String CHARSET = "utf-8";

    private static final HttpClientUtil INSTANCE = new HttpClientUtil();
    private static SSLConnectionSocketFactory sslConnectionSocketFactory;
    private static RequestConfig config;
	
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static HttpClientUtil getInstance() {
        return INSTANCE;
    }

    private HttpClientUtil() {
        initTrustHosts();
        initConfig();
    }

    private void initConfig() {
        config = RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT).setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).build();
    }

    private void initTrustHosts() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public CloseableHttpClient createClient() {
        return HttpClients.custom().setDefaultRequestConfig(config).setSSLSocketFactory(sslConnectionSocketFactory).build();
    }

    public static String basicGet(final String url, final Map<String, String> params) throws IOException {
        StringBuilder sb = new StringBuilder(url);
        String result = null;
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            sb.append("?").append(EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET)));
        }
        HttpGet httpGet = new HttpGet(sb.toString());
        CloseableHttpResponse response = HttpClientUtil.getInstance().createClient().execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
                EntityUtils.consume(entity);
            }
        } else {
            httpGet.abort();
        }
        response.close();
        return result;
    }

    public static String basicPost(final String url, final Map<String, String> params) throws IOException {
        UrlEncodedFormEntity urlEncodedFormEntity = null;
        String result = null;
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), value));
                }
            }
            urlEncodedFormEntity = new UrlEncodedFormEntity(pairs, CHARSET);
        }
        HttpPost httpPost = new HttpPost(url);
        if (urlEncodedFormEntity != null) {
            httpPost.setEntity(urlEncodedFormEntity);
        }
        CloseableHttpResponse response = HttpClientUtil.getInstance().createClient().execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
                EntityUtils.consume(entity);
            }
        } else {
            httpPost.abort();
        }
        response.close();
        return result;
    }

    /**
     * @Description: 请求返回json
     * @author: yangshuai --- 2016-5-20下午7:39:11
     */
	public static JSONObject doPost(String url) {
		@SuppressWarnings("resource")
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost method = new HttpPost(url);
		JSONObject jsonResult = null;
		try {
			// post请求返回结果
			try {
				HttpResponse result = httpClient.execute(method);
				url = URLDecoder.decode(url, "UTF-8");
				/** 请求发送成功，并得到响应 **/
				if (result.getStatusLine().getStatusCode() == 200) {
					String str = "";
					try {
						/** 读取服务器返回过来的json字符串数据 **/
						str = EntityUtils.toString(result.getEntity());
						jsonResult = JSONObject.parseObject(str);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonResult;
	}



    /**
     * @desc:  请求接口传json
     * @Author: qinjy
     * @Date: 2018/1/26 17:46
     */
    public static String jsonPost(final String url, final JSONObject json, final String tokenName, final String accessToken) throws IOException {
        String result = null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            if(StringUtils.isNotEmpty(tokenName)){  // 判断是否添加密钥
                httpPost.addHeader(tokenName, accessToken);
            }

            // 解决中文乱码问题
            UrlEncodedFormEntity urlEncodedFormEntity = null;
            StringEntity stringEntity = new StringEntity(json.toString(), CHARSET);
            stringEntity.setContentEncoding(CHARSET);

            httpPost.setEntity(stringEntity);

            System.out.println("Executing request " + httpPost.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {//
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {

                        HttpEntity entity = response.getEntity();

                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            result = httpclient.execute(httpPost, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }







}
