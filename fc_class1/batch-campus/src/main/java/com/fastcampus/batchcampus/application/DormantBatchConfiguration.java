package com.fastcampus.batchcampus.application;

import com.fastcampus.batchcampus.batch.Job;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DormantBatchConfiguration {

    public Job dormantBatchJob() {
        return new Job(

        )
    }
}
