//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.saint.util;

import com.github.wxpay.sdk.WXPayUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtils {
    public HttpClientUtils() {
    }

    public static void main(String[] args) {
        String path = System.getProperty("user.dir").replace("\\", "/") + "/src/com.saint/WebRoot/WEB-INF/cert/" + "1560444091" + ".p12";
        System.out.println(path);
    }

    public static String doPost(String url, Map<String, Object> paramMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(15000).setConnectionRequestTimeout(60000).setSocketTimeout(60000).build();
        httpPost.setConfig(requestConfig);
        if (paramMap != null && paramMap.size() > 0) {
            List<NameValuePair> nvpsList = new ArrayList();
            Set<Entry<String, Object>> entrySet = paramMap.entrySet();
            Iterator iterator = entrySet.iterator();

            while(iterator.hasNext()) {
                Entry<String, Object> mapEntry = (Entry)iterator.next();
                nvpsList.add(new BasicNameValuePair((String)mapEntry.getKey(), mapEntry.getValue().toString()));
            }

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nvpsList));
            } catch (UnsupportedEncodingException var12) {
                var12.printStackTrace();
            }
        }

        String result = "";

        try {
            httpResponse = ((CloseableHttpClient)httpClient).execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return result;
    }

    public static String doPostXml(String url, String requestDataXml) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(15000).setConnectionRequestTimeout(60000).setSocketTimeout(60000).build();
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(new StringEntity(requestDataXml, "UTF-8"));
        httpPost.addHeader("Content-Type", "text/xml");
        String result = "";

        try {
            httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity, "UTF-8");
        } catch (ClientProtocolException var8) {
            var8.printStackTrace();
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        return result;
    }

    public static String doPostWithCertXml(String strUrl, String requestDataXml, String mch_id) throws Exception {
        String UTF8 = "UTF-8";
        URL httpUrl = new URL(strUrl);
        char[] password = mch_id.toCharArray();
        String path = "F:/workspace/PayPlatform/WebRoot/WEB-INF/cert/" + mch_id + ".p12";
        InputStream certStream = new FileInputStream(new File(path));
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(certStream, password);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), (TrustManager[])null, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpURLConnection httpURLConnection = (HttpURLConnection)httpUrl.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(60000);
        httpURLConnection.connect();
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(requestDataXml.getBytes(UTF8));
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;

        while((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }

        String resp = stringBuffer.toString();
        if (stringBuffer != null) {
            try {
                bufferedReader.close();
            } catch (IOException var22) {
                var22.printStackTrace();
            }
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException var21) {
                var21.printStackTrace();
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException var20) {
                var20.printStackTrace();
            }
        }

        if (certStream != null) {
            try {
                certStream.close();
            } catch (IOException var19) {
                var19.printStackTrace();
            }
        }

        return resp;
    }

    public String requestWithoutCert(String strUrl, Map<String, String> reqData, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String UTF8 = "UTF-8";
        String reqBody = WXPayUtil.mapToXml(reqData);
        URL httpUrl = new URL(strUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection)httpUrl.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(connectTimeoutMs);
        httpURLConnection.setReadTimeout(readTimeoutMs);
        httpURLConnection.connect();
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(reqBody.getBytes(UTF8));
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;

        while((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }

        String resp = stringBuffer.toString();
        if (stringBuffer != null) {
            try {
                bufferedReader.close();
            } catch (IOException var18) {
                var18.printStackTrace();
            }
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException var17) {
                var17.printStackTrace();
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException var16) {
                var16.printStackTrace();
            }
        }

        return resp;
    }
}
