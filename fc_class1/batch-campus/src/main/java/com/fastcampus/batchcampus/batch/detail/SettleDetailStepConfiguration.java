package com.fastcampus.batchcampus.batch.detail;

import com.fastcampus.batchcampus.domain.ApiOrder;
import com.fastcampus.batchcampus.domain.SettleDetail;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SettleDetailStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    // 첫번째 스텝은 파일의 고객 + 서비스 별로 집계를 해서 Execution Context 안에 넣는다.
    // ex) A 고객이 1번 서비스를 13번 호출한다.
    // Key(A, 1) 13번 호출

    @Bean
    public Step preSettleDetailStep(
            FlatFileItemReader<ApiOrder> preSettleDetailReader,
            PreSettleDetailWriter preSettleDetailWriter,
            ExecutionContextPromotionListener executionContextPromotionListener
    ) {
        return new StepBuilder("preSettleDetailStep", jobRepository)
                .<ApiOrder, Key>chunk(5000, platformTransactionManager)
                .reader(preSettleDetailReader)
                .processor(new PreSettleDataProcessor()) // 여기에서 processor를 따로 빈으로 등록할 필요가 없어서 여기서는 그냥 생성해주겠음
                .writer(preSettleDetailWriter)
                .listener(executionContextPromotionListener)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<ApiOrder> preSettleDetailReader(
            @Value("#{jobParameters['targetDate']}") String targetDate
    ) {

        final String fileName = targetDate + "_api_orders.csv";

        return new FlatFileItemReaderBuilder<ApiOrder>()
                .name("preSettleDetailReader")
                .resource(new ClassPathResource("/datas/" + fileName))
                .linesToSkip(1) // 첫번째 라인 스킵
                .delimited() // 쉼표로 구분되어 있음
                .names("id", "customerId", "url", "state", "createdAt")
                .targetType(ApiOrder.class)
                .build();
    }

    // 두번째 스텝은 집계된 Execution Context 데이터를 가지고 DB에 write를 한다.

    @Bean
    public Step settleDetailStep(
            SettleDetailReader settleDetailReader,
            SettleDetailProcessor settleDetailProcessor,
            JpaItemWriter<SettleDetail> settleDetailWriter
    ) {
        return new StepBuilder("settleDetailStep", jobRepository)
                .<KeyAndCount, SettleDetail>chunk(1000, platformTransactionManager)
                .reader(settleDetailReader)
                .processor(settleDetailProcessor)
                .writer(settleDetailWriter)
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        final ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"snapshots"});
        return listener;
    }

    @Bean
    public JpaItemWriter<SettleDetail> settleDetailWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<SettleDetail>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}