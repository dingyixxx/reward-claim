package module1controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    // Redis 写操作
    public void setValue(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void setValueWithExpire(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void setObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setObjectWithExpire(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // Redis 读操作
    public String getValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 删除操作
    public Boolean deleteKey(String key) {
        return stringRedisTemplate.delete(key);
    }

    // 判断是否存在
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    // 设置过期时间
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }
    // 获取所有 keys（使用 scan 避免 KEYS 命令的性能问题）
    public Set<String> getAllKeys() {
        Set<String> keys = new HashSet<>();
        stringRedisTemplate.execute((RedisCallback<Object>) connection -> {
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match("*").count(1000).build());
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
            return null;
        });
        return keys;
    }

}
