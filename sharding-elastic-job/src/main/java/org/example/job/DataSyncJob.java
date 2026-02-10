package org.example.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataSyncJob implements SimpleJob {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void execute(ShardingContext context) {
        int shardingItem = context.getShardingItem();
        System.out.println("当前分片项: " + shardingItem);

        String sql = "select id,student.name as sname,age,address from student WHERE id % ? = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, 5, shardingItem);

        for (Map<String, Object> row : list) {
            System.out.println("处理订单: " + row.get("id") + ", name=" + row.get("sname")+ ", age=" + row.get("age")+ ", address=" + row.get("address"));
            // 执行你的业务逻辑
        }
    }
}