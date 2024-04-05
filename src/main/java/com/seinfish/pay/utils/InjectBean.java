package com.seinfish.pay.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 将工具类中注入bean
 */
@Component
public class InjectBean implements ApplicationContextAware {

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    StringRedisTemplate stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
    PayUtil.setStringRedisTemplate(stringRedisTemplate);
    SpringUtil.setApplicationContext(applicationContext);
  }

}
