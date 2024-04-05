package com.seinfish.pay.service.handler.base;

import static com.seinfish.pay.common.exception.ErrorCode.PAY_SERVER_LOAD_PAY_HANDLER_ERROR;

import com.seinfish.pay.common.exception.PayException;
import com.seinfish.pay.utils.SpringUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 支付处理器选择器
 */
@Component
public class PayHandlerSelector implements ApplicationListener<ApplicationReadyEvent> {

  /**
   * 执行策略集合
   */
  private final Map<String, PayHandler> payHandlerMap = new HashMap<>();

  private static final AtomicBoolean loading = new AtomicBoolean(false);

  public PayHandler select(String mark){
    return payHandlerMap.get(mark);
  }

  /**
   * 将所有支付策略存储到容器中
   */
  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    if (loading.get()) {
      return;
    }
    ApplicationContext context = SpringUtil.getContext();
    Map<String, PayHandler> beansOfType = context.getBeansOfType(PayHandler.class);
    beansOfType.forEach((beanName, bean) -> {
      PayHandler payHandler = payHandlerMap.get(bean.mark());
      Optional.ofNullable(payHandler).ifPresentOrElse(
          (val) -> {
            // 存在不用执行
          },
          () -> {
            // 不存在则加入容器中
            payHandlerMap.put(bean.mark(), bean);
          });
    });
    boolean loaded = loading.compareAndSet(false, true);
    if (!loaded){
      throw new PayException(PAY_SERVER_LOAD_PAY_HANDLER_ERROR);
    }
  }
}
