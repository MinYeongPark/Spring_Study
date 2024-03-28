package com.fastcampus.batchcampus.batch.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
class SettleDetailReader implements ItemReader<KeyAndCount>, StepExecutionListener {

    private Iterator<Map.Entry<Key, Long>> iterator;

    @Override
    public KeyAndCount read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // iterator에 있는 것을 하나씩 읽어오면 됨

        if (!iterator.hasNext())
            return null; // step 종료

        final Map.Entry<Key, Long> map = iterator.next();

        return new KeyAndCount(map.getKey(), map.getValue());
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        final JobExecution jobExecution = stepExecution.getJobExecution();
        final Map<Key, Long> snapshots = (ConcurrentHashMap<Key, Long>) jobExecution.getExecutionContext().get("snapshots"); // 이전 스텝에서 snapshot에 넣었던 것을 가져옴
        iterator = snapshots.entrySet().iterator();
    }
}
