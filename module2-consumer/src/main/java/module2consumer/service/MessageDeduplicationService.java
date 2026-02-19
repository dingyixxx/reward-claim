package module2consumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class MessageDeduplicationService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String PROCESSED_PREFIX = "processed_msg:";
    private static final Duration EXPIRE_TIME = Duration.ofHours(24);

    /**
     * 检查消息是否已处理
     */
    public boolean isProcessed(String messageId) {
        if (messageId == null || messageId.isEmpty()) {
            return false;
        }

        String key = PROCESSED_PREFIX + messageId;
        try {
            Boolean exists = redisTemplate.hasKey(key);
            boolean result = exists != null && exists;
            if (result) {
                log.debug("消息已处理: {}", messageId);
            }
            return result;
        } catch (Exception e) {
            log.error("检查消息处理状态失败: {}", messageId, e);
            return false;
        }
    }

    /**
     * 标记消息为已处理
     */
    public void markAsProcessed(String messageId) {
        if (messageId == null || messageId.isEmpty()) {
            return;
        }

        String key = PROCESSED_PREFIX + messageId;
        try {
            redisTemplate.opsForValue().set(key, "1", EXPIRE_TIME);
            log.debug("标记消息为已处理: {}", messageId);
        } catch (Exception e) {
            log.error("标记消息处理状态失败: {}", messageId, e);
        }
    }

    /**
     * 批量检查消息是否已处理
     */
    public boolean isAnyProcessed(String... messageIds) {
        for (String messageId : messageIds) {
            if (isProcessed(messageId)) {
                return true;
            }
        }
        return false;
    }
}
