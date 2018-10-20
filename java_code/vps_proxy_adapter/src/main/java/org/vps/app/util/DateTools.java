package org.vps.app.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTools {
    
    private static final Logger logger = LoggerFactory.getLogger(DateTools.class);
    
    public static String getDate() {
        try {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());
            
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            
            return format.format(c1.getTime());    
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("getDate", e);
            }
        }
        return null;
    }
    
    public static String getTime() {
        try {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());
            
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            return format.format(c1.getTime());    
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("getTime", e);
            }
        }
        return null;
    }

    public static String getTime(Date date) {
        try {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date);
            
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            return format.format(c1.getTime());    
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("getTime", e);
            }
        }
        return "";
    }

    /**
     * 获得指定日期增加天数
     * @param date
     * @param days
     * @return
     */
    public static Date incrDayBy(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * 获得日期格式为 yyyyMM
     * @return
     */
    public static String getMonthDate() {
        try {
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date());
            SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
            return format.format(c1.getTime());
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("getDate", e);
            }
        }
        return null;
    }
    
}
