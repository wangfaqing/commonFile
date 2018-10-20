package org.vps.service.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到  
@Documented //说明该注解将被包含在javadoc中 
public @interface DBAnnotation {
    
    /*
     * 数据库 config 表中cfg_key的值
     */
    String name() default "";
    
    /*
     * 自动设置
     */
    boolean autoSet() default true;
}
