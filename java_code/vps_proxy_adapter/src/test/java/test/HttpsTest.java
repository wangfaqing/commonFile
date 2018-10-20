package test;

import edu.hziee.cap.common.util.Md5Utils;
import jodd.util.Base64;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class HttpsTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(HttpsTest.class);

    public static void main(String[] args) {
        httpGetAUZ("127.0.0.1", 14222, "https://www.mi.com/");
    }

    public static void httpGetAUZ(String proxyHostName, int proxyPort, String destUrl) {
        HttpHost proxyHost = getProxyHost(proxyHostName, proxyPort);
        HttpHost target = getDestHost(destUrl);
        HttpGet httpGet = getHttpGet(destUrl, proxyHost);
        CloseableHttpClient httpClient = getHttpClient(proxyHost);
        try {
            CloseableHttpResponse response = httpClient.execute(target, httpGet);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));
            } finally {
                response.close();
            }
        } catch (Exception e) {
            LOGGER.error("Any error msg that u want to write" + ", msg: " + e.getMessage(), e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                LOGGER.error("Any error msg that u want to write" + ", msg: " + e.getMessage(), e);
            }
        }
    }

    private static HttpHost getProxyHost(String host, int port) {
        return new HttpHost(host, port);
    }

    private static HttpHost getDestHost(String strUrl) {
        URL url = null;
        try {
            url = new URL(strUrl);
            return new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
        } catch (MalformedURLException e) {
            e.getMessage();
        }
        return null;
    }

    private static HttpGet getHttpGet(String strUrl, HttpHost proxyHost) {
        URL url = null;
        try {
            url = new URL(strUrl);
            HttpGet httpGet = new HttpGet(url.getFile());
            httpGet.setConfig(RequestConfig.custom().setProxy(proxyHost).build());
            return httpGet;
        } catch (MalformedURLException e) {
            e.getMessage();
        }
        return null;
    }

    private static CloseableHttpClient getHttpClient(HttpHost proxyHost) {
        long time =  new Date().getTime()/1000;
        String pwd = "orderId=test&time="+time + "&sign=" + Md5Utils.md5Hex("orderId=test&secret=test&time="+time).toLowerCase();
        try {
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(proxyHost.getHostName(), proxyHost.getPort()),
                    new UsernamePasswordCredentials("User", pwd));

            CloseableHttpClient httpclient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .setSSLSocketFactory(sslsf)
                    .build();
            return httpclient;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }
}
