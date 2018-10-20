package test;

import edu.hziee.cap.common.util.Md5Utils;
import io.netty.buffer.ByteBuf;
import jodd.util.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Date;

public class ThreadTest implements Runnable {

    @Override
    public synchronized void run() {
        try {
            int j = 0;
            for (int i = 0; i < 10; i++) {
                //System.out.println(Thread.currentThread().getName() +  ": " + (i+1) + " ----------");
                Thread.sleep(500);
                //		String pwd1 = ProxyNewPwd.build("Fh8caXRpoBxwNaBV","-1", "uid_test",1,-1,-1);
                long time =  new Date().getTime()/1000;
                String author = "Basic " + Base64.encodeToString("User:orderId=test&time="+time + "&sign=" + Md5Utils.md5Hex("orderId=test&secret=test&time="+time).toLowerCase());
                CloseableHttpClient httpCilent2 = HttpClients.createDefault();
                RequestConfig requestConfig = RequestConfig.custom()
                        .setProxy(new HttpHost("127.0.0.1", 14222))
                        .build();
                HttpGet httpGet2 = new HttpGet("https://www.baidu.com");
                httpGet2.setConfig(requestConfig);
                httpGet2.setHeader("proxy-authorization",author);
                try {
                    HttpResponse httpResponse = httpCilent2.execute(httpGet2);
//                    System.out.println("------------内容-------------");
//                    InputStream is =  httpResponse.getEntity().getContent();
//                    Header[] headers = httpResponse.getAllHeaders();
//                    int headLineSize = httpResponse.getStatusLine().toString().getBytes().length;
//                    int localSize = httpResponse.getLocale().toString().getBytes().length;
//                    int protocolSize = httpResponse.getProtocolVersion().toString().getBytes().length;
//                    //System.out.println(headers.length);
//                    System.out.println("------------头部-------------");
//                    int headerSize = 0;
//                    for(Header header : headers) {
//                        headerSize += header.toString().getBytes().length;
//                        //System.out.println(header.toString());
//                    }
//                    System.out.println("------------头部-------------");
//                    BufferedInputStream bis = new BufferedInputStream(is);
//                    byte[] b = new byte[1024];
//                    int h = 0;
//                    int lenght = 0;
//                    StringBuilder sb = new StringBuilder();
//                    while ((lenght = bis.read(b, 0, 1024)) != -1) {
//                        String str = new String(b, 0, b.length);
//                        sb.append(str);
//                        h += lenght;
//                    }
//                    System.out.println("------------内容-------------");
//                    //System.out.println(sb.toString().trim());
//                    System.out.println(h);
//                    System.out.println(headerSize);
//                    System.out.println(headLineSize);
//                    System.out.println(localSize);
//                    System.out.println(protocolSize);
//                    System.out.println(h + headerSize + headLineSize + localSize + protocolSize);
//                    System.out.println(sb.toString().getBytes().length);
                    if(httpResponse.getStatusLine().getStatusCode() == 200) {
                        j++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    httpCilent2.close();
                }
                //System.out.println(Thread.currentThread().getName() +  ": " + (i+1) + " 结束----------");
            }
            System.out.println(Thread.currentThread().getName() +  ": 成功次数" + j + " ----------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
