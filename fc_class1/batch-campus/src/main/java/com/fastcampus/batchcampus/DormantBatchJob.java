package com.fastcampus.batchcampus;

import com.fastcampus.batchcampus.batch.BatchStatus;
import com.fastcampus.batchcampus.batch.JobExecution;
import com.fastcampus.batchcampus.customer.Customer;
import com.fastcampus.batchcampus.customer.CustomerRepository;
import com.fastcampus.batchcampus.customer.EmailProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DormantBatchJob {

    private final CustomerRepository customerRepository;
    private final EmailProvider emailProvider;

    public DormantBatchJob(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.emailProvider = new EmailProvider.Fake();
    }

    public JobExecution execute() {

        final JobExecution jobExecution = new JobExecution();
        jobExecution.setStatus(BatchStatus.STARTING);
        jobExecution.setStartTime(LocalDateTime.now());

        int pageNo = 0;

        try {
            while (true) {

                // 1. 유저를 조회한다.
                final PageRequest pageRequest = PageRequest.of(pageNo, 1, Sort.by("id").ascending()); // size : 1 => 1개씩 조회
                final Page<Customer> page = customerRepository.findAll(pageRequest);

                final Customer customer;
                if (page.isEmpty()) {
                    break; // 배치 잡이 끝난 것으로 간주
                } else {
                    pageNo ++;
                    customer = page.getContent().get(0); // 1개 사이즈만 조회했으니까 0번째 인덱스에 존재한다
                }

                // 2. 휴면 계정 대상을 추출 및 변환한다.
                // 로그인 날짜 / 365일 전 / 오늘
                final boolean isDormantTarget = LocalDateTime.now()
                        .minusDays(365)
                        .isAfter(customer.getLoginAt().toLocalDate().atStartOfDay());

                if (isDormantTarget) {
                    customer.setStatus(Customer.Status.DORMANT);
                } else {
                    continue;
                }

                // 3. 휴면 계정으로 상태를 변경한다.
                customerRepository.save(customer);

                // 4. 메일을 보낸다.
                emailProvider.send(customer.getEmail(), "휴면전환 안내 메일입니다.", "내용");
            }
            jobExecution.setStatus(BatchStatus.COMPLETED); // 별다른 exception이 없으면 성공으로 간주

        } catch (Exception e) {
            jobExecution.setStatus(BatchStatus.FAILED);
        }

        jobExecution.setEndTime(LocalDateTime.now());

        emailProvider.send("admin@fastcampus.com",
                "배치 완료 알림",
                "DormantBatchJob이 수행되었습니다. status : " + jobExecution.getStatus()
        );

        return jobExecution;
    }
}
