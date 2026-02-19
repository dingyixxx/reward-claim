package module2consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetryListener extends RetryListenerSupport {

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (context.getRetryCount() > 0) {
            log.info("重试结束，总共重试了 {} 次", context.getRetryCount());
            if (context.getRetryCount() >= 4) { // 5次重试失败
                log.error("=== 达到最大重试次数，消息将进入死信队列 ===");
            }
        }
        super.close(context, callback, throwable);
    }
}
