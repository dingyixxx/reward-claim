import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CanalTcpTest {
    public static void main(String[] args) {
        // 创建连接器
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress("192.168.200.131", 11111),
                "example",
                "",
                ""
        );

        // 连接
        connector.connect();
        // 订阅
        connector.subscribe(".*\\..*");
        // 回滚
        connector.rollback();

        System.out.println("✓ Canal TCP 连接成功！");

        // 获取数据
        int batchSize = 100;
        while (true) {
            Message message = connector.getWithoutAck(batchSize, 1L, TimeUnit.SECONDS);
            long batchId = message.getId();
            int size = message.getEntries().size();

            if (batchId == -1 || size == 0) {
                System.out.println("等待数据...");
            } else {
                System.out.println("收到数据：batchId=" + batchId + ", size=" + size);
                printEntry(message.getEntries());
                connector.ack(batchId);
            }

            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
    }

    private static void printEntry(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN ||
                    entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("解析数据失败", e);
            }

            CanalEntry.EventType eventType = rowChange.getEventType();
            System.out.println(String.format("================> binlog[%s:%s], name[%s,%s], eventType: %s",
                    entry.getHeader().getLogfileName(),
                    entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(),
                    entry.getHeader().getTableName(),
                    eventType));

            // 遍历每行数据
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                } else {
                    System.out.println("------- Before -------");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("------- After -------");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }

    /**
     * 打印列数据
     */
    private static void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() +
                    "    update=" + column.getUpdated());
        }
    }
}

//
//CREATE TABLE IF NOT EXISTS t_order_test (
//        id BIGINT PRIMARY KEY AUTO_INCREMENT,
//        order_no VARCHAR(50),
//user_id BIGINT,
//amount DECIMAL(10,2)
//);
//
//        -- 插入测试数据
//INSERT INTO t_order_test (order_no, user_id, amount) VALUES ('TEST001', 1001, 99.99);
//INSERT INTO t_order_test (order_no, user_id, amount) VALUES ('TEST002', 1002, 188.88);
//INSERT INTO t_order_test (order_no, user_id, amount) VALUES ('TEST003', 1003, 256.56);
//INSERT INTO t_order_test (order_no, user_id, amount) VALUES ('TEST004', 1004, 369.99);
//INSERT INTO t_order_test (order_no, user_id, amount) VALUES ('TEST005', 1005, 456.78);
//INSERT INTO t_order_test (order_no, user_id, amount) VALUES ('TEST006', 1006, 543.21);
//
//        -- 查看数据
//SELECT * FROM t_order_test;
