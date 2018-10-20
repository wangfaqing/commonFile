package test;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import edu.hziee.cap.common.util.Md5Utils;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.QueryStringDecoder;
import jodd.util.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test1
{
	@Test
    public void test11(){
    	@SuppressWarnings({ "resource", "unused" })
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

    	try{

    	}catch(Exception e){
    		e.printStackTrace();
    	}

	}

    @Test
    public void test12() {
		@SuppressWarnings({"resource", "unused"})
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");


		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Thread(new ThreadTest(), "1111").start();
		new Thread(new ThreadTest(), "2222").start();
		new Thread(new ThreadTest(), "3333").start();
		new Thread(new ThreadTest(), "4444").start();
		new Thread(new ThreadTest(), "5555").start();
		new Thread(new ThreadTest(), "6666").start();
//		new Thread(new ThreadTest(), "7777").start();
//		new Thread(new ThreadTest(), "8888").start();
//		new Thread(new ThreadTest(), "9999").start();
//		new Thread(new ThreadTest(), "0000").start();
	}

    @Test
    public void readFileCountFlow(){
		String startDate = "2018-10-15 17:35:49";
		String endDate = "2018-10-15 17:35:55";
		String path = "D:\\/test\\/test_201810\\/test_20181015.txt";
		BufferedReader fr = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(path);
			fr = new BufferedReader(fileReader);
			String line = null;
			int flow = 0;
			int reqCount = 0;
			int again = 0;
			while((line = fr.readLine()) != "") {
				if(StringUtils.isBlank(line)) {
					if(++again < 3) {
						continue;
					}
					break;
				}
				String dateStr = line.substring(line.indexOf(" ") + 1, line.indexOf(" 订单"));
				Long dateL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr).getTime();
				Long startL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startDate).getTime();
				Long endL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate).getTime();
				if(startL <= dateL && endL >= dateL) {
					String bLine = line.substring(line.lastIndexOf(" "));
					String lastLine = bLine.substring(1, bLine.indexOf("B"));
					flow += Integer.valueOf(lastLine);
					reqCount++;
				}
			}
			System.out.println(flow);
			System.out.println(reqCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(fileReader != null) {
					fileReader.close();
				}
				if(fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void len(){
		long time =  new Date().getTime()/1000;
		System.out.println(time);
		String str = Base64.encodeToString("User:orderId=test&time="+time + "&sign=" + Md5Utils.md5Hex("orderId=test&secret=test&time="+time).toLowerCase());
		System.out.println(str);

		System.out.println(Md5Utils.md5Hex("orderId=test&secret=test&time="+time).toLowerCase());

		System.out.println(DigestUtils.md5Hex("orderId=test&secret=test&time="+time).toLowerCase());
		String author = "Basic " + Base64.encodeToString("User:orderId=test&time="+time + "&sign=" + Md5Utils.md5Hex("orderId=test&secret=test&time="+time).toLowerCase());
		System.out.println(author);

//		HttpHost proxy = new HttpHost("127.0.0.1", 14222);
//		System.out.println(proxy.getHostName());
//		System.out.println(proxy.getPort());

//		String code = "434f4e4e454354207777772e62616964752e636f6d3a34343320485454502f312e310d0a486f73743a207777772e62616964752e636f6d0d0a557365722d4167656e743a204170616368652d48747470436c69656e742f342e352e3220284a6176612f312e382e305f313632290d0a0d0a";
//		byte[] bytes = hexStr2Bytes(code);
//		System.out.println(new String(bytes));
	}

	//hex串转为byte
	static byte[] hexStr2Bytes(String hexStr) {
		if (null == hexStr || hexStr.length() < 1) return null;

		int byteLen = hexStr.length() / 2;
		byte[] result = new byte[byteLen];
		char[] hexChar = hexStr.toCharArray();
		for(int i=0 ;i<byteLen;i++){
			result[i] = (byte)(Character.digit(hexChar[i*2],16)<<4 | Character.digit(hexChar[i*2+1],16));
		}

		return result;
	}

	@Test
	public void testQueryStringDecoder() {
		String query = "name=wfq&age=23&gender=男";
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(query, HttpConstants.DEFAULT_CHARSET, false, 15);
		Map<String, List<String>> keyMap = queryStringDecoder.parameters();
		System.out.println(getKey(keyMap, "name"));
		System.out.println(getKey(keyMap, "age"));
		System.out.println(getKey(keyMap, "gender"));
	}

	public String getKey(Map<String, List<String>> keyMap, String key) {
		List<String> list = keyMap.get(key);
		if(list == null || list.isEmpty()) return "";
		return list.get(0);
	}
}