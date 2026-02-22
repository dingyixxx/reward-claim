package com.example.sharding.algorithm;


import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 按小时分片算法
 * 根据 create_time 字段，将数据路由到对应的小时表
 * 例如：2026-02-22 15:30:00 -> t_order_2026022215
 */
@Slf4j
public class HourlyShardingAlgorithm implements StandardShardingAlgorithm<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");

    private Properties props;

    @Override
    public void init(Properties props) {
        this.props = props;
        log.info("HourlyShardingAlgorithm 初始化完成，props={}", props);
    }

    /**
     * 精准分片（单值查询/插入）
     * 用于：INSERT、WHERE create_time = ?
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames,
                             PreciseShardingValue<LocalDateTime> shardingValue) {
        // 获取分片键的值
        LocalDateTime createTime = shardingValue.getValue();

        // 计算目标表名
        String targetTable = getTargetTableName(createTime);

        log.info("【精准分片】createTime={} -> 目标表={}", createTime, targetTable);

        // 验证目标表是否在可用表列表中
        for (String tableName : availableTargetNames) {
            if (tableName.equals(targetTable)) {
                return targetTable;
            }
        }

        // 如果目标表不存在，返回第一个可用表（避免报错）
        log.warn("⚠️  目标表 {} 不存在于可用表列表，返回第一个可用表", targetTable);
        return availableTargetNames.iterator().next();
    }

    /**
     * 范围分片（范围查询）
     * 用于：WHERE create_time BETWEEN ? AND ?、WHERE create_time > ?
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames,
                                         RangeShardingValue<LocalDateTime> shardingValue) {
        // 获取范围值
        Range<LocalDateTime> valueRange = shardingValue.getValueRange();
        LocalDateTime lowerEndpoint = valueRange.lowerEndpoint();
        LocalDateTime upperEndpoint = valueRange.upperEndpoint();

        log.info("【范围分片】时间范围：{} ~ {}", lowerEndpoint, upperEndpoint);

        // 收集所有在范围内的表
        return availableTargetNames.stream()
                .filter(tableName -> isTableInRange(tableName, lowerEndpoint, upperEndpoint))
                .collect(Collectors.toList());
    }

    /**
     * 根据时间计算目标表名
     */
    private String getTargetTableName(LocalDateTime createTime) {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        String suffix = createTime.format(FORMATTER);
        return "t_order_" + suffix;
    }

    /**
     * 判断表名是否在时间范围内
     */
    private boolean isTableInRange(String tableName, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 从表名提取时间，如 t_order_2026022215 -> 2026-02-22 15:00:00
            String timeSuffix = tableName.replace("t_order_", "");
            LocalDateTime tableTime = LocalDateTime.parse(timeSuffix, FORMATTER);

            // 判断表时间是否在查询范围内
            boolean afterStart = (startTime == null) || !tableTime.isBefore(startTime);
            boolean beforeEnd = (endTime == null) || !tableTime.isAfter(endTime);

            return afterStart && beforeEnd;
        } catch (Exception e) {
            log.warn("解析表名时间失败：{}", tableName, e);
            return true; // 解析失败时返回 true，避免遗漏数据
        }
    }

    @Override
    public String getType() {
        return "HOURLY";
    }



    @Override
    public Properties getProps() {
        return this.props;
    }
}