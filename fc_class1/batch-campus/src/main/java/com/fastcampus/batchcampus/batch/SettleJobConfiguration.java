package com.fastcampus.batchcampus.batch;

import com.fastcampus.batchcampus.batch.support.DateFormatJobParametersValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@RequiredArgsConstructor
public class SettleJobConfiguration {

    private final JobRepository jobRepository;

    @Bean
    public Job settleJob(
            Step preSettleDetailStep,
            Step settleDetailStep,
            Step settleGroupStep
    ) {
        return new JobBuilder("settleJob", jobRepository)
                .validator(new DateFormatJobParametersValidator(new String[]{"targetDate"}))
                .start(preSettleDetailStep)
                .next(settleDetailStep)
                // 주간정산 하는 날이면
                .next(isTuesdayDecider())
                // 주간정산 실행시켜줘!
                .on("COMPLETED").to(settleGroupStep)
                .build()
                .build();
    }

    // 매주 화요일마다 주간 정산을 한다.
    public JobExecutionDecider isTuesdayDecider() {
        return (jobExecution, stepExecution) -> {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            final String targetDate = stepExecution.getJobParameters().getString("targetDate");
            final LocalDate date = LocalDate.parse(targetDate, formatter);

            // 화요일이 아니면 아무것도 안 함
            if (date.getDayOfWeek() != DayOfWeek.TUESDAY) {
                return new FlowExecutionStatus("NOOP");
            }

            return FlowExecutionStatus.COMPLETED;
        };
    }
}
