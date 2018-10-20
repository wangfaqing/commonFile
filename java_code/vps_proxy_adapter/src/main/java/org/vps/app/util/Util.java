package org.vps.app.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author sund
 */
public class Util {

	public static String getIp(String clientIp) {
		String[] str = clientIp.split(":");
		if (str.length == 2)
			return str[0];
		return "";
	}

	/**
	 * @author sund
	 * @return
	 * @des get now time
	 */
	public static String getNowTime() {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String time = dateFormat.format(now);
		return time;
	}

	/**
     * @author wangyaofeng
     * @param param uri路径与参数
     * @return uri参数keyValue对list
     */
    public static ArrayList<keyValue<String, String>> getPamater(String param) {
        ArrayList<keyValue<String, String>> retlist = new ArrayList<>();
        int index = param.indexOf("?");
        if (index >= 0 && param.length() > index) {
            String strParams = param.substring(index + 1);
            String []params = strParams.split("&");
            for (String p : params) {
                if (p == null || p.length() == 0) continue;
                if (p.indexOf("=") < 0) continue;
                String[] keyValue = p.split("=");
                String key = "", value = "";
                if (keyValue.length == 1) {
                    key = keyValue[0];
                } else if (keyValue.length >= 2){
                    key = keyValue[0];
                    value = keyValue[1];
                }
                if (!key.isEmpty())
                    retlist.add(new keyValue<String, String>(key, value));
            }
        }
        return retlist;
    }
}
