package com.fastcampus.batchcampus;

import com.fastcampus.batchcampus.batch.BatchStatus;
import com.fastcampus.batchcampus.batch.Job;
import com.fastcampus.batchcampus.batch.JobExecution;
import com.fastcampus.batchcampus.customer.Customer;
import com.fastcampus.batchcampus.customer.CustomerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
class DormantBatchJobTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private Job dormantBatchJob;

    @BeforeEach
    public void setup() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 시간이 1년을 경과한 고객이 3명이고, 1년 이내에 로그인한 고객이 5명이면 3명의 고객이 휴면전환대상이다.")
    void test1() {

        // given
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(366);

        saveCustomer(364);
        saveCustomer(364);
        saveCustomer(364);
        saveCustomer(364);
        saveCustomer(364);

        // when
        final JobExecution result = dormantBatchJob.execute();

        // then
        final long dormantCount = customerRepository.findAll()
                .stream()
                .filter(it -> it.getStatus() == Customer.Status.DORMANT)
                .count();

        Assertions.assertThat(dormantCount).isEqualTo(3);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("고객이 10명 있지만 모두 다 휴면전환대상(1년 경과한 사람)이면 휴면전환 대상은 10명이다.")
    void test2() {

        // given
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);
        saveCustomer(400);

        // when
        final JobExecution result = dormantBatchJob.execute();

        // then
        final long dormantCount = customerRepository.findAll()
                .stream()
                .filter(it -> it.getStatus() == Customer.Status.DORMANT)
                .count();

        Assertions.assertThat(dormantCount).isEqualTo(10); // 휴면 전환된 사람의 수가 0명이어야 함
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("고객이 없는 경우에도 배치는 정상동작해야 한다.")
    void test3() {
        // when
        final JobExecution result = dormantBatchJob.execute();

        // then
        final long dormantCount = customerRepository.findAll()
                .stream()
                .filter(it -> it.getStatus() == Customer.Status.DORMANT)
                .count();

        Assertions.assertThat(dormantCount).isEqualTo(0);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("배치가 실패하면 BatchStatus는 FAILED를 반환해야 한다.")
    void test4() {

        // given
        final Job dormantBatchJob = new Job(null);

        // when
        final JobExecution result = dormantBatchJob.execute();

        // then
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.FAILED);
    }


    private void saveCustomer(long loginMinusDays) {
        final String uuid = UUID.randomUUID().toString();
        final Customer test = new Customer(uuid, uuid + "@fastcampus.com");
        test.setLoginAt(LocalDateTime.now().minusDays(loginMinusDays));
        customerRepository.save(test);
    }
}