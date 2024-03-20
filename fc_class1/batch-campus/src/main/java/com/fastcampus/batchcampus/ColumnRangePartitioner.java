package com.fastcampus.batchcampus;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ColumnRangePartitioner implements Partitioner {

    private final JdbcTemplate jdbcTemplate;

    public ColumnRangePartitioner(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) { // 5
        final Integer min = jdbcTemplate.queryForObject("SELECT MIN(id) from USER", Integer.class);// 내 경우에는 지금 16
        final Integer max = jdbcTemplate.queryForObject("SELECT MAX(id) from USER", Integer.class); // 내 경우에는 지금 45
        int targetSize = (max - min) / gridSize + 1; // 내 경우에는 6개씩 worker step이 읽게 됨!

        final Map<String, ExecutionContext> result = new HashMap<>();
        int number = 0;
        int start = min; // 16
        int end = start + targetSize - 1; // 21

        while (start <= max) {
            final ExecutionContext value = new ExecutionContext();
            result.put("partition" + number, value);

            if (end >= max) {
                end = max;
            }
            value.putInt("minValue", start);
            value.putInt("maxValue", end);

            start += targetSize;
            end += targetSize;
            number++;
        }
        return result;
    }

    // 실행 결과
    // partition0 : 16 ~ 21
    // partition1 : 22 ~ 27
    // partition2 : 28 ~ 33
    // partition3 : 34 ~ 39
    // partition4 : 40 ~ 45
}
