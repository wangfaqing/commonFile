package org.vps.app;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Booter {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
	    try {
	        new ClassPathXmlApplicationContext("applicationContext.xml");    
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
