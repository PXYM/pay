package com.seinfish.pay.utils;


import org.springframework.context.ApplicationContext;

/**
 * spring 工具类
 */
public final class SpringUtil {

  private static ApplicationContext context;

  static void setApplicationContext(ApplicationContext applicationContext){
    context = applicationContext;
  }

  public static ApplicationContext getContext(){
    return context;
  }
  public static <T> T getBean(Class<T> obj){
    return context.getBean(obj);
  }
}
